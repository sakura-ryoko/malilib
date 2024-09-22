package fi.dy.masa.malilib.test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Frustum;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.mixin.IMixinAbstractHorseEntity;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.sync.SyncUtils;
import fi.dy.masa.malilib.sync.data.*;
import fi.dy.masa.malilib.util.*;

public class TestRenderHandler implements IRenderer
{
    private boolean wasHeld = false;

    @Override
    public void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isAltDown())
        {
            profiler.push(this.getProfilerSectionSupplier() + "_render_overlay");
            renderInventoryOverlay(mc, drawContext);
            profiler.pop();
        }
    }

    @Override
    public void onRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player != null)
        {
            this.renderTargetingOverlay(posMatrix, mc);
        }
    }

    @Override
    public void onRenderWorldPreWeather(Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, Profiler profiler)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            MinecraftClient mc = MinecraftClient.getInstance();

            profiler.push(this.getProfilerSectionSupplier() + "_test_walls");
            if (wasHeld && !GuiBase.isShiftDown())
            {
                TestWalls.clear();
                wasHeld = false;
            }
            else if (GuiBase.isShiftDown())
            {
                if (TestWalls.needsUpdate(camera.getBlockPos()))
                {
                    profiler.swap(this.getProfilerSectionSupplier() + "_test_walls_update");
                    TestWalls.update(camera, mc);
                }

                profiler.swap(this.getProfilerSectionSupplier() + "_test_walls_draw");
                TestWalls.draw(camera.getPos(), posMatrix, projMatrix, mc, profiler);
                wasHeld = true;
            }
            profiler.pop();
        }
    }

    @Override
    public void onRenderTooltipLast(DrawContext drawContext, ItemStack stack, int x, int y)
    {
        Item item = stack.getItem();

        if (item instanceof FilledMapItem)
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                RenderUtils.renderMapPreview(stack, x, y, 160, false, drawContext);
            }
        }
        else if (stack.getComponents().contains(DataComponentTypes.CONTAINER) && InventoryUtils.shulkerBoxHasItems(stack))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                RenderUtils.renderShulkerBoxPreview(stack, x, y, true, drawContext);
            }
        }
    }

    @Override
    public Supplier<String> getProfilerSectionSupplier()
    {
        return () -> MaLiLibReference.MOD_ID + "_test_render";
    }

    private void renderTargetingOverlay(Matrix4f posMatrix, MinecraftClient mc)
    {
        Entity entity = mc.getCameraEntity();

        if (entity != null &&
                mc.crosshairTarget != null &&
                mc.crosshairTarget.getType() == HitResult.Type.BLOCK &&
                MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
                GuiBase.isCtrlDown())
        {
            BlockHitResult hitResult = (BlockHitResult) mc.crosshairTarget;
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();

            RenderUtils.setupBlend();

            Color4f color = Color4f.fromColor(StringUtils.getColor("#C03030F0", 0));

            RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.getSide(),
                    hitResult.getPos(),
                    color,
                    posMatrix,
                    mc);

            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
        }
    }

    private void renderInventoryOverlay(MinecraftClient mc, DrawContext drawContext)
    {
        World world = WorldUtils.getBestWorld(mc);
        Entity cameraEntity = EntityUtils.getCameraEntity();

        if (world == null || mc.player == null)
        {
            return;
        }

        if (cameraEntity == mc.player && world instanceof ServerWorld)
        {
            // We need to get the player from the server world (if available, ie. in single player),
            // so that the player itself won't be included in the ray trace
            Entity serverPlayer = world.getPlayerByUuid(mc.player.getUuid());

            if (serverPlayer != null)
            {
                cameraEntity = serverPlayer;
            }
        }

        HitResult trace = RayTraceUtils.getRayTraceFromEntity(world, cameraEntity, false);

        BlockPos pos = null;
        BlockState state;
        Inventory inv = null;
        SyncData fbe = null;
        SyncInventory si = null;
        SyncEquipment se = null;
        SyncMerchant sm = null;
        SyncHorse sh = null;
        CrafterBlockEntity ce = null;
        Entity entity = null;
        ShulkerBoxBlock shulkerBoxBlock = null;
        LivingEntity entityLivingBase = null;

        if (trace.getType() == HitResult.Type.BLOCK)
        {
            pos = ((BlockHitResult) trace).getBlockPos();
            state = world.getBlockState(pos);

            if (!state.hasBlockEntity())
            {
                BlockPos adjPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
                BlockState adjState = world.getBlockState(adjPos);

                // Check block Offset if it's not a collide-able block (trace doesn't land)
                if (adjState.hasBlockEntity() && adjState.getCollisionShape(world, adjPos).equals(VoxelShapes.empty()))
                {
                    pos = adjPos;
                    state = adjState;
                }
            }

            Block blockTmp = state.getBlock();
            if (state.hasBlockEntity())
            {
                if (blockTmp instanceof ShulkerBoxBlock)
                {
                    shulkerBoxBlock = (ShulkerBoxBlock) blockTmp;
                }
                if (blockTmp instanceof CrafterBlock)
                {
                    ce = (CrafterBlockEntity) world.getWorldChunk(pos).getBlockEntity(pos);
                }

                if (MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
                {
                    fbe = SyncUtils.getFakeBlockEntity(world, pos);

                    if (fbe instanceof SyncInventory)
                    {
                        si = (SyncInventory) fbe;
                    }
                }
            }

            inv = SyncUtils.getInventory(world, pos);
        }
        else if (trace.getType() == HitResult.Type.ENTITY)
        {
            entity = ((EntityHitResult) trace).getEntity();

            if (MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
            {
                fbe = SyncUtils.getFakeEntity(entity);
            }

            if (entity instanceof LivingEntity)
            {
                entityLivingBase = (LivingEntity) entity;
            }

            if (entity instanceof PiglinEntity)
            {
                inv = fbe != null ? ((SyncMerchant) fbe).getInventory() : ((PiglinEntity) entity).getInventory();
            }
            else if (entity instanceof MerchantEntity)
            {
                inv = fbe != null ? ((SyncMerchant) fbe).getInventory() : ((MerchantEntity) entity).getInventory();
            }
            else if (entity instanceof AbstractHorseEntity)
            {
                inv = fbe != null ? ((SyncHorse) fbe).getHorseInventory() : ((IMixinAbstractHorseEntity) entity).tweakeroo_getHorseInventory();
            }
            else if (entity instanceof Inventory)
            {
                if (fbe != null)
                {
                    inv = InventoryUtils.getAsInventory(((SyncInventory) fbe).getHeldStacks());
                }
                else
                {
                    inv = (Inventory) entity;
                }
            }

            if (MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
            {
                if (fbe instanceof SyncMerchant)
                {
                    sm = (SyncMerchant) fbe;
                }
                if (fbe instanceof SyncHorse)
                {
                    sh = (SyncHorse) fbe;
                }
                if (fbe instanceof SyncEquipment)
                {
                    se = (SyncEquipment) fbe;
                }
            }
        }

        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        boolean isWolf = (entityLivingBase instanceof WolfEntity) || (entity != null && entity.getType().equals(EntityType.WOLF));
        boolean isHorse = entityLivingBase instanceof AbstractHorseEntity;
        boolean isVillager = entityLivingBase instanceof MerchantEntity;
        int x = xCenter - 52 / 2;
        int y = yCenter - 92;

        if (fbe != null && (inv == null || inv.isEmpty()))
        {
            System.out.printf("render (No inv) - [FBE] type [%s], nbt [%s]\n", fbe.getClass().getTypeName(), fbe.getNbt().toString());
        }
        else
        {
            System.out.printf("render - [NO FBE] wolf: %s, horse: %s, villager: %s\n", isWolf, isHorse, isVillager);
        }

        if (inv != null && inv.size() > 0)
        {
            final int totalSlots = isHorse ? inv.size() - 1 : inv.size();
            final int firstSlot = isHorse ? 1 : 0;
            final InventoryOverlay.InventoryRenderType type = isVillager ? InventoryOverlay.InventoryRenderType.VILLAGER : InventoryOverlay.getInventoryType(inv);
            final InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, totalSlots);
            final int rows = (int) Math.ceil((double) totalSlots / props.slotsPerRow);
            Set<Integer> lockedSlots = new HashSet<>();
            int xInv = xCenter - (props.width / 2);
            int yInv = yCenter - props.height - 6;

            if (rows > 6)
            {
                yInv -= (rows - 6) * 18;
                y -= (rows - 6) * 18;
            }

            if (entityLivingBase != null)
            {
                x = xCenter - 55;
                xInv = xCenter + 2;
                yInv = Math.min(yInv, yCenter - 92);
            }

            if (type == InventoryOverlay.InventoryRenderType.CRAFTER && pos != null)
            {
                if (si != null)
                {
                    lockedSlots = si.getDisabledSlots();
                }
                else if (ce != null)
                {
                    lockedSlots = BlockUtils.getDisabledSlots(ce);
                }
            }
            if (fbe != null)
            {
                System.out.printf("render - [FBE]  type [%s], nbt id [%s]\n", type.toString(), fbe.getNbt().getString("id"));
            }
            else
            {
                System.out.printf("render - [NO FBE] type [%s], wolf: %s, horse: %s, villager: %s\n", type.toString(), isWolf, isHorse, isVillager);
            }

            RenderUtils.setShulkerboxBackgroundTintColor(shulkerBoxBlock, true);

            if (isHorse)
            {
                Inventory horseInv = new SimpleInventory(2);
                ItemStack horseArmor = sh != null ? sh.getBodyArmor() : ((AbstractHorseEntity) entityLivingBase).getBodyArmor();
                horseInv.setStack(0, horseArmor != null && !horseArmor.isEmpty() ? horseArmor : ItemStack.EMPTY);
                horseInv.setStack(1, inv.getStack(0));

                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
                InventoryOverlay.renderInventoryStacks(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
                xInv += 32 + 4;
            }

            if (totalSlots > 0)
            {
                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, mc);

                if (lockedSlots.isEmpty())
                {
                    InventoryOverlay.renderInventoryStacks(type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, firstSlot, totalSlots, mc, drawContext);
                }
                else
                {
                    DefaultedList<ItemStack> crafterInv = si != null ? si.getHeldStacks() : ce.getHeldStacks();
                    InventoryOverlay.renderCrafterStacks(crafterInv, lockedSlots, xInv + props.slotOffsetX, yInv + props.slotOffsetY, firstSlot, mc, drawContext);
                }
            }
        }

        if (isWolf)
        {
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.HORSE;
            final InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, 2);
            final int rows = (int) Math.ceil((double) 2 / props.slotsPerRow);
            int xInv;
            int yInv = yCenter - props.height - 6;

            if (rows > 6)
            {
                yInv -= (rows - 6) * 18;
                y -= (rows - 6) * 18;
            }

            x = xCenter - 55;
            xInv = xCenter + 2;
            yInv = Math.min(yInv, yCenter - 92);

            Inventory wolfInv = new SimpleInventory(2);
            ItemStack wolfArmor = null;
            if (entityLivingBase instanceof WolfEntity)
            {
                wolfArmor = se != null ? se.getBodyArmor() : ((WolfEntity) entityLivingBase).getBodyArmor();
            }
            wolfInv.setStack(0, wolfArmor != null && !wolfArmor.isEmpty() ? wolfArmor : ItemStack.EMPTY);
            InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
            InventoryOverlay.renderInventoryStacks(type, wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
        }

        MaLiLib.logger.error("IO: renderEquipmentStacks() - START");

        if (sm != null)
        {
            InventoryOverlay.renderFeEquipmentOverlayBackground(x, y, sm, drawContext);
            InventoryOverlay.renderFeEquipmentStacks(sm, x, y, mc, drawContext);
        }
        else if (sh != null)
        {
            InventoryOverlay.renderFeEquipmentOverlayBackground(x, y, sh, drawContext);
            InventoryOverlay.renderFeEquipmentStacks(sh, x, y, mc, drawContext);
        }
        else if (se != null)
        {
            InventoryOverlay.renderFeEquipmentOverlayBackground(x, y, se, drawContext);
            InventoryOverlay.renderFeEquipmentStacks(se, x, y, mc, drawContext);
        }
        else if (entityLivingBase != null)
        {
            InventoryOverlay.renderEquipmentOverlayBackground(x, y, entityLivingBase, drawContext);
            InventoryOverlay.renderEquipmentStacks(entityLivingBase, x, y, mc, drawContext);
        }
        else
        {
            MaLiLib.logger.error("IO: renderEquipmentStacks() - FAIL");
        }
    }
}
