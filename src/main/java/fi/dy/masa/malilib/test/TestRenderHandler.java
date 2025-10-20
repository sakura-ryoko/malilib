package fi.dy.masa.malilib.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.*;
import fi.dy.masa.malilib.util.game.BlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

@ApiStatus.Experimental
public class TestRenderHandler implements IRenderer
{
    private static final TestRenderHandler INSTANCE = new TestRenderHandler();

    public TestRenderHandler()
    {
        // NO-OP
    }

    public static TestRenderHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void onRenderGameOverlayLastDrawer(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
            MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getBooleanValue() &&
            MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getKeybind().isKeybindHeld())
        {
            /*
            profiler.push(this.getProfilerSectionSupplier() + "_inventory_overlay");
            InventoryOverlay.Context context = RayTraceUtils.getTargetInventory(mc, true);

            if (context != null)
            {
                renderInventoryOverlay(context, drawContext, mc);
            }

            profiler.pop();
             */

            TestInventoryOverlayHandler.getInstance().getRenderContext(drawContext, profiler, mc);
        }

        if (ConfigTestEnum.TEST_TEXT_LINES.getBooleanValue())
        {
            List<String> list = new ArrayList<>();
            list.add("Test Line 1");
            list.add("Test Line 2");
            list.add("Test Line 3");
            list.add("Test Line 4");
            list.add("Test Line 5");

            RenderUtils.renderText(4, 4, 0.5F, 0xFFE0E0E0, 0xA0505050, HudAlignment.TOP_LEFT, true, false, true, list, drawContext);
        }
    }

    @Override
    public void onRenderWorldLastAdvanced(Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, Profiler profiler)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc.player != null)
            {
                profiler.push(MaLiLibReference.MOD_ID + "_selector");

                if (TestSelector.INSTANCE.shouldRender())
                {
                    TestSelector.INSTANCE.render(posMatrix, projMatrix, profiler, mc);
                }

                profiler.swap(MaLiLibReference.MOD_ID + "_targeting_overlay");
                this.renderTargetingOverlay(posMatrix, mc);

                profiler.swap(MaLiLibReference.MOD_ID + "_test_walls");

                if (ConfigTestEnum.TEST_WALLS_HOTKEY.getBooleanValue())
                {
                    if (TestWalls.INSTANCE.needsUpdate(mc.getCameraEntity(), mc))
                    {
                        TestWalls.INSTANCE.update(camera, mc.getCameraEntity(), mc);
                    }

                    TestWalls.INSTANCE.render(camera, posMatrix, projMatrix, mc, profiler);
                }

                profiler.pop();
            }
        }
    }

    @Override
    public void onRenderTooltipComponentInsertFirst(Item.TooltipContext context, ItemStack stack, List<Text> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            // This can cause various problems unrelated to the tooltips; but it does work.
            /*
            MutableText itemName = list.getFirst().copy();
            MutableText title = Text.empty().append(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.title"));
            list.addFirst(title);
             */
            list.add(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.first"));
        }
    }

    @Override
    public void onRenderTooltipComponentInsertMiddle(Item.TooltipContext context, ItemStack stack, List<Text> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            list.add(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.middle"));
        }
    }

    @Override
    public void onRenderTooltipComponentInsertLast(Item.TooltipContext context, ItemStack stack, List<Text> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            list.add(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.last"));
        }
    }

    @Override
    public void onRenderWorldPreWeather(Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, Profiler profiler)
    {
//        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
//        {
//            MinecraftClient mc = MinecraftClient.getInstance();
//
//            profiler.push(MaLiLibReference.MOD_ID + "_test_walls");
//
//            if (ConfigTestEnum.TEST_WALLS_HOTKEY.getBooleanValue())
//            {
//                if (TestWalls.INSTANCE.needsUpdate(mc.getCameraEntity(), mc))
//                {
//                    TestWalls.INSTANCE.update(camera, mc.getCameraEntity(), mc);
//                }
//
//                TestWalls.INSTANCE.render(camera, posMatrix, projMatrix, mc, profiler);
//            }
//
//            profiler.pop();
//        }
    }

    @Override
    public void onRenderTooltipLast(DrawContext drawContext, ItemStack stack, int x, int y)
    {
        Item item = stack.getItem();
        Profiler profiler = Profilers.get();

        if (item instanceof FilledMapItem)
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_map_preview");
                RenderUtils.renderMapPreview(stack, x, y, 160, false, drawContext);
                profiler.pop();
            }
        }
        else if (stack.getComponents().contains(DataComponentTypes.CONTAINER) && InventoryUtils.shulkerBoxHasItems(stack))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_shulker_preview");
                RenderUtils.renderShulkerBoxPreview(stack, x, y, true, drawContext);
                profiler.pop();
            }
        }
        else if (stack.getComponents().contains(DataComponentTypes.BUNDLE_CONTENTS) && InventoryUtils.bundleHasItems(stack))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_bundle_preview");
                RenderUtils.renderBundlePreview(stack, x, y, MaLiLibConfigs.Test.TEST_BUNDLE_PREVIEW_WIDTH.getIntegerValue(), true, drawContext);
                profiler.pop();
            }
        }
        else if (stack.isOf(Items.ENDER_CHEST))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                MinecraftClient mc = MinecraftClient.getInstance();
                World world = WorldUtils.getBestWorld(mc);

                if (mc.player == null || world == null)
                {
                    return;
                }

                PlayerEntity player = world.getPlayerByUuid(mc.player.getUuid());

                if (player != null)
                {
                    Pair<Entity, NbtCompound> pair = TestDataSyncer.getInstance().requestEntity(world, player.getId());
                    EnderChestInventory inv;

                    if (pair != null && pair.getRight() != null && pair.getRight().contains(NbtKeys.ENDER_ITEMS))
                    {
                        inv = InventoryUtils.getPlayerEnderItemsFromNbt(pair.getRight(), world.getRegistryManager());
                    }
                    else
                    {
                        inv = player.getEnderChestInventory();
                    }

                    if (inv != null)
                    {
                        NbtCompound nbt = new NbtCompound();
                        NbtList list = inv.toNbtList(world.getRegistryManager());

                        nbt.put(NbtKeys.ENDER_ITEMS, list);
                        RenderUtils.renderNbtItemsPreview(stack, nbt, x, y, false, drawContext);
                    }

                    // TODO 1.21.6+
//                    EnderChestInventory inv;
//
//                    if (pair != null && pair.getRight() != null && pair.getRight().contains(NbtKeys.ENDER_ITEMS))
//                    {
//                        inv = InventoryUtils.getPlayerEnderItemsFromNbt(pair.getRight(), world.getRegistryManager());
//                    }
//                    else if (pair != null && pair.getLeft() instanceof PlayerEntity pe && !pe.getEnderChestInventory().isEmpty())
//                    {
//                        inv = pe.getEnderChestInventory();
//                    }
//                    else
//                    {
//                        // Last Ditch effort
//                        inv = player.getEnderChestInventory();
//                    }
//
//                    if (inv != null)
//                    {
//                        NbtInventory nbtInv = NbtInventory.fromInventory(inv);
//
//                        if (nbtInv.isEmpty())
//                        {
//                            return;
//                        }
//
//                        NbtCompound nbt = new NbtCompound();
//                        NbtList list = nbtInv.toNbtList();
//
//                        nbt.put(NbtKeys.ENDER_ITEMS, list);
//                        RenderUtils.renderNbtItemsPreview(drawContext, stack, nbt, x, y, false);
//                    }
                }
            }
        }
    }

    @Override
    public Supplier<String> getProfilerSectionSupplier()
    {
        return () -> MaLiLibReference.MOD_ID + "_test";
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

    /*
    // OG Method (Works)
    public static void renderInventoryOverlay(MinecraftClient mc, DrawContext drawContext)
    {
        World world = WorldUtils.getBestWorld(mc);
        Entity cameraEntity = EntityUtils.getCameraEntity();

        if (mc.player == null || world == null)
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
        Inventory inv = null;
        ShulkerBoxBlock shulkerBoxBlock = null;
        CrafterBlock crafterBlock = null;
        LivingEntity entityLivingBase = null;

        if (trace.getType() == HitResult.Type.BLOCK)
        {
            pos = ((BlockHitResult) trace).getBlockPos();
            Block blockTmp = world.getBlockState(pos).getBlock();

            if (blockTmp instanceof ShulkerBoxBlock)
            {
                shulkerBoxBlock = (ShulkerBoxBlock) blockTmp;
            }
            else if (blockTmp instanceof CrafterBlock)
            {
                crafterBlock = (CrafterBlock) blockTmp;
            }

            inv = InventoryUtils.getInventory(world, pos);
        }
        else if (trace.getType() == HitResult.Type.ENTITY)
        {
            Entity entity = ((EntityHitResult) trace).getEntity();

            *
            if (entity.getWorld().isClient &&
                    Configs.Generic.ENTITY_DATA_SYNC.getBooleanValue())
            {
                EntitiesDataStorage.getInstance().requestEntity(entity.getId());
            }
             *

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
                inv = ((IMixinAbstractHorseEntity) entity).malilib_getHorseInventory();
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
            if (crafterBlock != null && pos != null)
            {
                CrafterBlockEntity cbe = (CrafterBlockEntity) world.getWorldChunk(pos).getBlockEntity(pos);
                if (cbe != null)
                {
                    lockedSlots = BlockUtils.getDisabledSlots(cbe);
                }
            }

            MaLiLib.printDebug("renderInventory(): type [{}], size [{}]", type.name(), inv.size());
            RenderUtils.setShulkerboxBackgroundTintColor(shulkerBoxBlock, true);

            if (isHorse)
            {
                Inventory horseInv = new SimpleInventory(2);
                ItemStack horseArmor = (((AbstractHorseEntity) entityLivingBase).getBodyArmor());
                horseInv.setStack(0, horseArmor != null && !horseArmor.isEmpty() ? horseArmor : ItemStack.EMPTY);
                horseInv.setStack(1, inv.getStack(0));

                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
                if (type == InventoryOverlay.InventoryRenderType.LLAMA)
                {
                    InventoryOverlay.renderLlamaArmorBackgroundSlots(horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
                }
                else
                {
                    InventoryOverlay.renderHorseArmorBackgroundSlots(horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
                }
                InventoryOverlay.renderInventoryStacks(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
                xInv += 32 + 4;
            }
            if (totalSlots > 0)
            {
                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, mc);
                if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND)
                {
                    InventoryOverlay.renderBrewerBackgroundSlots(inv, xInv, yInv, drawContext);
                }
                InventoryOverlay.renderInventoryStacks(type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, firstSlot, totalSlots, lockedSlots, mc, drawContext);
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
            ItemStack wolfArmor = ((WolfEntity) entityLivingBase).getBodyArmor();
            wolfInv.setStack(0, wolfArmor != null && !wolfArmor.isEmpty() ? wolfArmor : ItemStack.EMPTY);
            InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
            InventoryOverlay.renderWolfArmorBackgroundSlots(wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
            InventoryOverlay.renderInventoryStacks(type, wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
        }

        if (entityLivingBase != null)
        {
            InventoryOverlay.renderEquipmentOverlayBackground(x, y, entityLivingBase, drawContext);
            InventoryOverlay.renderEquipmentStacks(entityLivingBase, x, y, mc, drawContext);
        }
        */

    // OG / Tweakeroo method also
    public static void renderInventoryOverlayOG(InventoryOverlay.Context context, DrawContext drawContext, MinecraftClient mc)
    {
        //MinecraftClient mc = MinecraftClient.getInstance();
        LivingEntity entityLivingBase = null;
        BlockEntity be = null;
        Inventory inv = null;
        NbtCompound nbt = new NbtCompound();

        if (context == null)
        {
            return;
        }

        if (context.be() != null)
        {
            be = context.be();
        }
        else if (context.entity() != null)
        {
            if (context.entity() instanceof LivingEntity)
            {
                entityLivingBase = context.entity();
            }
        }
        if (context.inv() != null)
        {
            inv = context.inv();
        }
        if (context.nbt() != null)
        {
            nbt.copyFrom(context.nbt());
        }

        //MaLiLib.logger.error("render: ctx-type [{}], inv [{}], raw Nbt [{}]", context.type().toString(), inv != null ? inv.size() : "null", nbt.isEmpty() ? "empty" : nbt.toString());

        final boolean isWolf = (entityLivingBase instanceof WolfEntity);
        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        int x = xCenter - 52 / 2;
        int y = yCenter - 92;

        MaLiLib.LOGGER.error("0: -> inv.type [{}] // nbt.type [{}]", context.inv() != null ? InventoryOverlay.getInventoryType(context.inv()) : null, context.nbt() != null ? InventoryOverlay.getInventoryType(context.nbt()) : null);
        MaLiLib.LOGGER.error("1: -> inv.size [{}] // inv.isEmpty [{}]", context.inv() != null ? context.inv().size() : -1, context.inv() != null ? context.inv().isEmpty() : -1);

        if (inv != null && inv.size() > 0)
        {
            final boolean isHorse = (entityLivingBase instanceof AbstractHorseEntity);
            final int totalSlots = isHorse ? inv.size() - 1 : inv.size();
            final int firstSlot = isHorse ? 1 : 0;

            InventoryOverlay.InventoryRenderType type = (entityLivingBase instanceof VillagerEntity) ? InventoryOverlay.InventoryRenderType.VILLAGER : InventoryOverlay.getBestInventoryType(inv, nbt, context);
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

            if (be != null && type == InventoryOverlay.InventoryRenderType.CRAFTER)
            {
                if (be instanceof CrafterBlockEntity cbe)
                {
                    lockedSlots = BlockUtils.getDisabledSlots(cbe);
                }
                else if (context.nbt() != null)
                {
                    lockedSlots = NbtBlockUtils.getDisabledSlotsFromNbt(context.nbt());
                }
            }

            if (context.be() != null && context.be().getCachedState().getBlock() instanceof ShulkerBoxBlock sbb)
            {
                RenderUtils.setShulkerboxBackgroundTintColor(sbb, true);
            }

            MaLiLib.LOGGER.warn("render():0: type [{}] // Nbt Type [{}]", type.toString(), context.nbt() != null ? InventoryOverlay.getInventoryType(context.nbt()) : "INVALID");

            if (isHorse)
            {
                Inventory horseInv = new SimpleInventory(2);
                ItemStack horseArmor = (((AbstractHorseEntity) entityLivingBase).getBodyArmor());
                horseInv.setStack(0, horseArmor != null && !horseArmor.isEmpty() ? horseArmor : ItemStack.EMPTY);
                horseInv.setStack(1, inv.getStack(0));

                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
                if (type == InventoryOverlay.InventoryRenderType.LLAMA)
                {
                    InventoryOverlay.renderLlamaArmorBackgroundSlots(horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
                }
                else
                {
                    InventoryOverlay.renderHorseArmorBackgroundSlots(horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
                }
                InventoryOverlay.renderInventoryStacks(type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
                xInv += 32 + 4;
            }

            if (totalSlots > 0)
            {
                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, mc);
                // TODO 1.21.4+
                if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND)
                {
                    InventoryOverlay.renderBrewerBackgroundSlots(inv, xInv, yInv, drawContext);
                }
                InventoryOverlay.renderInventoryStacks(type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, firstSlot, totalSlots, lockedSlots, mc, drawContext);
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
            ItemStack wolfArmor = ((WolfEntity) entityLivingBase).getBodyArmor();
            wolfInv.setStack(0, wolfArmor != null && !wolfArmor.isEmpty() ? wolfArmor : ItemStack.EMPTY);
            InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc);
            InventoryOverlay.renderWolfArmorBackgroundSlots(wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, drawContext);
            InventoryOverlay.renderInventoryStacks(type, wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc, drawContext);
        }

        if (entityLivingBase != null)
        {
            InventoryOverlay.renderEquipmentOverlayBackground(x, y, entityLivingBase, drawContext);
            InventoryOverlay.renderEquipmentStacks(entityLivingBase, x, y, mc, drawContext);
        }
    }
}
