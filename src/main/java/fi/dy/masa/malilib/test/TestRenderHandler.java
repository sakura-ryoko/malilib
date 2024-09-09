package fi.dy.masa.malilib.test;

import java.util.function.Supplier;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Frustum;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.mixin.test.IMixinAbstractHorseEntity;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.*;

public class TestRenderHandler implements IRenderer
{
    private boolean wasHeld = false;

    @Override
    public void onRenderGameOverlayPost(DrawContext drawContext)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isAltDown())
        {
            renderInventoryOverlay(mc, drawContext);
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
    public void onRenderWorldPreWeather(Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (wasHeld && !GuiBase.isShiftDown())
            {
                TestWalls.clear();
                wasHeld = false;
            }
            else if (GuiBase.isShiftDown())
            {
                if (TestWalls.needsUpdate(camera.getBlockPos()))
                {
                    TestWalls.update(camera, mc);
                }

                TestWalls.draw(camera.getPos(), posMatrix, projMatrix, mc);
                wasHeld = true;
            }
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
                RenderUtils.renderMapPreview(stack, x, y, 160, false);
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

        if (mc.player == null)
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

        Inventory inv = null;
        ShulkerBoxBlock shulkerBoxBlock = null;
        //CrafterBlock crafterBlock = null;
        LivingEntity entityLivingBase = null;

        if (trace.getType() == HitResult.Type.BLOCK)
        {
            BlockPos pos = ((BlockHitResult) trace).getBlockPos();
            Block blockTmp = world.getBlockState(pos).getBlock();

            if (blockTmp instanceof ShulkerBoxBlock)
            {
                shulkerBoxBlock = (ShulkerBoxBlock) blockTmp;
            }

            inv = fi.dy.masa.malilib.util.InventoryUtils.getInventory(world, pos);
        }
        else if (trace.getType() == HitResult.Type.ENTITY)
        {
            Entity entity = ((EntityHitResult) trace).getEntity();

            if (entity instanceof LivingEntity)
            {
                entityLivingBase = (LivingEntity) entity;
            }

            if (entity instanceof Inventory)
            {
                inv = (Inventory) entity;
            }
            else if (entity instanceof VillagerEntity)
            {
                inv = ((VillagerEntity) entity).getInventory();
            }
            else if (entity instanceof AbstractHorseEntity)
            {
                inv = ((IMixinAbstractHorseEntity) entity).tweakeroo_getHorseInventory();
            }
        }

        final boolean isWolf = (entityLivingBase instanceof WolfEntity);
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        int x = xCenter - 52 / 2;
        int y = yCenter - 92;

        if (inv != null && inv.size() > 0)
        {
            final boolean isHorse = (entityLivingBase instanceof AbstractHorseEntity);
            final int totalSlots = isHorse ? inv.size() - 1 : inv.size();
            final int firstSlot = isHorse ? 1 : 0;

            final InventoryOverlay.InventoryRenderType type = (entityLivingBase instanceof VillagerEntity) ? InventoryOverlay.InventoryRenderType.VILLAGER : InventoryOverlay.getInventoryType(inv);
            final InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, totalSlots);
            final int rows = (int) Math.ceil((double) totalSlots / props.slotsPerRow);
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

            RenderUtils.setShulkerboxBackgroundTintColor(shulkerBoxBlock, true);

            if (isHorse)
            {
                Inventory horseInv = new SimpleInventory(2);
                ItemStack horseArmor = (((AbstractHorseEntity) entityLivingBase).getBodyArmor());
                horseInv.setStack(0, horseArmor != null && !horseArmor.isEmpty() ? horseArmor : ItemStack.EMPTY);
                horseInv.setStack(1, inv.getStack(0));

                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
                InventoryOverlay.renderInventoryStacks(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
                xInv += 32 + 4;
            }

            if (totalSlots > 0)
            {
                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, mc);
                InventoryOverlay.renderInventoryStacks(type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, firstSlot, totalSlots, mc, drawContext);
            }
        }

        if (isWolf)
        {
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.HORSE;
            final fi.dy.masa.malilib.render.InventoryOverlay.InventoryProperties props = fi.dy.masa.malilib.render.InventoryOverlay.getInventoryPropsTemp(type, 2);
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
            ItemStack wolfArmor = ((WolfEntity) entityLivingBase).getBodyArmor();
            wolfInv.setStack(0, wolfArmor != null && !wolfArmor.isEmpty() ? wolfArmor : ItemStack.EMPTY);
            InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
            InventoryOverlay.renderInventoryStacks(type, wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
        }

        if (entityLivingBase != null)
        {
            InventoryOverlay.renderEquipmentOverlayBackground(x, y, entityLivingBase, drawContext);
            InventoryOverlay.renderEquipmentStacks(entityLivingBase, x, y, mc, drawContext);
        }
    }
}
