package fi.dy.masa.malilib.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.game.BlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtInventory;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.time.TickUtils;

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
    public void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            if (MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getBooleanValue() &&
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

	            if (TestInventoryOverlayHandler.getInstance().isNewCode())
	            {
		            TestInventoryOverlayHandler.getInstance().getRenderContextNew(drawContext, profiler, mc);
	            }
				else
	            {
		            TestInventoryOverlayHandler.getInstance().getRenderContext(drawContext, profiler, mc);
	            }
            }

            if (ConfigTestEnum.TEST_TEXT_LINES.getBooleanValue())
            {
                List<String> list = new ArrayList<>();
                list.add("Test Line 1");
                list.add("Test Line 2");
                list.add("Test Line 3");
                list.add("Test Line 4");
                list.add("Test Line 5");
                
                if (TickUtils.getInstance().isValid())
                {
                    String result = getMeasuredTPS();
                    list.addFirst(result);
                    list.removeLast();
                }

                RenderUtils.renderText(drawContext, 4, 4, MaLiLibConfigs.Test.TEST_CONFIG_FLOAT.getFloatValue(), 0xFFE0E0E0, 0xA0505050, HudAlignment.TOP_LEFT, true, false, true, list);
            }
        }
    }

    private static @Nonnull String getMeasuredTPS()
    {
        final float tickRate = TickUtils.getTickRate();
        final double clampedTps = TickUtils.getMeasuredTPS();
        final double actualTps = TickUtils.getActualTPS();
        final double avgMspt = TickUtils.getAvgMSPT();
        final double avgTps = TickUtils.getAvgTPS();
        final double mspt = TickUtils.getMeasuredMSPT();
        final String rst = GuiBase.TXT_RST;
        final String preTps = clampedTps >= tickRate ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
        String preMspt;
        boolean isEstimated = TickUtils.isEstimated();
        boolean isSprinting = TickUtils.isSprinting();
        String sprintStr = isSprinting ? "- "+GuiBase.TXT_LIGHT_PURPLE+GuiBase.TXT_BOLD+"Sprinting"+rst : "";

        if      (mspt <= 40) { preMspt = GuiBase.TXT_GREEN; }
        else if (mspt <= 45) { preMspt = GuiBase.TXT_YELLOW; }
        else if (mspt <= 50) { preMspt = GuiBase.TXT_GOLD; }
        else                 { preMspt = GuiBase.TXT_RED; }

        return isEstimated ?
               String.format("Server TPS: %s%.1f%s (MSPT [est]: %s%.1f%s) (R: %s%.1f%s, avMS: %.2f, avTPS: %.2f, [actTPS: %.2f]) %s",
                             preTps, clampedTps, rst, preMspt, mspt, rst,
                             GuiBase.TXT_AQUA, tickRate, rst,
                             avgMspt, avgTps, actualTps,
                             sprintStr) :
               String.format("Server TPS: %s%.1f%s MSPT: %s%.1f%s (R: %s%.1f%s, avMS: %.2f, avTPS: %.2f, [actTPS: %.2f]) %s",
                             preTps, clampedTps, rst, preMspt, mspt, rst,
                             GuiBase.TXT_AQUA, tickRate, rst,
                             avgMspt, avgTps, actualTps,
                             sprintStr)
                ;
    }

//    @Override
//    public void onRenderWorldPreMain(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler)
//    {
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
//                TestWalls.INSTANCE.draw(camera, posMatrix, projMatrix, mc, profiler);
//            }
//
//            profiler.pop();
//        }
//    }

//    @Override
//    public void onRenderWorldLayerPass(RenderLayer layer, Matrix4f posMatrix, Matrix4f projMatrix, Vec3d camera, Profiler profiler, ObjectListIterator<ChunkBuilder.BuiltChunk> chunkIterator, ArrayList<RenderPass.RenderObject> renderObjects)
//    {
//        // NO-OP
//    }

    @Override
    public void onRenderWorldPostDebugRender(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate immediate, Vec3d camera, Profiler profiler)
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
//                TestWalls.INSTANCE.draw(camera, posMatrix, projMatrix, mc, profiler);
//            }
//
//            profiler.pop();
//        }
    }

//    @Override
//    public void onRenderWorldPreParticles(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler)
//    {
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
//                TestWalls.INSTANCE.draw(camera, posMatrix, projMatrix, mc, profiler);
//            }
//
//            profiler.pop();
//        }
//    }

    @Override
    public void onRenderWorldPreWeather(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, BufferBuilderStorage buffers, Profiler profiler)
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
    public void onRenderWorldLastAdvanced(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, BufferBuilderStorage buffers, Profiler profiler)
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
    public void onRenderTooltipComponentInsertFirst(Item.TooltipContext context, ItemStack stack, Consumer<Text> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            // This can cause various problems unrelated to the tooltips; but it does work.
            /*
            MutableText itemName = list.getFirst().copy();
            MutableText title = Text.empty().append(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.title"));
            list.addFirst(title);
             */
            list.accept(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.first"));
        }
    }

    @Override
    public void onRenderTooltipComponentInsertMiddle(Item.TooltipContext context, ItemStack stack, Consumer<Text> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            list.accept(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.middle"));
        }
    }

    @Override
    public void onRenderTooltipComponentInsertLast(Item.TooltipContext context, ItemStack stack, Consumer<Text> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            list.accept(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.last"));
        }
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
                RenderUtils.renderMapPreview(drawContext, stack, x, y, 160, false);
                profiler.pop();
            }
        }
        else if (stack.getComponents().contains(DataComponentTypes.CONTAINER) && InventoryUtils.shulkerBoxHasItems(stack))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_shulker_preview");
                RenderUtils.renderShulkerBoxPreview(drawContext, stack, x, y, true);
                profiler.pop();
            }
        }
        else if (stack.getComponents().contains(DataComponentTypes.BUNDLE_CONTENTS) && InventoryUtils.bundleHasItems(stack))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_bundle_preview");
                RenderUtils.renderBundlePreview(drawContext, stack, x, y, MaLiLibConfigs.Test.TEST_BUNDLE_PREVIEW_WIDTH.getIntegerValue(), true);
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
                    else if (pair != null && pair.getLeft() instanceof PlayerEntity pe && !pe.getEnderChestInventory().isEmpty())
                    {
                        inv = pe.getEnderChestInventory();
                    }
                    else
                    {
                        // Last Ditch effort
                        inv = player.getEnderChestInventory();
                    }

                    if (inv != null)
                    {
                        try (NbtInventory nbtInv = NbtInventory.fromInventory(inv))
                        {
                            NbtList list = nbtInv.toNbtList(world.getRegistryManager());
                            NbtCompound nbt = new NbtCompound();

                            nbt.put(NbtKeys.ENDER_ITEMS, list);
                            RenderUtils.renderNbtItemsPreview(drawContext, stack, nbt, x, y, false);
                        }
                        catch (Exception ignored) { }
                    }
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

            /*
            RenderUtils.depthMask(false);
            RenderUtils.culling(false);
            RenderUtils.depthTest(false);
            RenderUtils.blend(true);
             */

            Color4f color = Color4f.fromColor(StringUtils.getColor("#C03030F0", 0));

            RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.getSide(),
                    hitResult.getPos(),
                    color, posMatrix);

            /*
            RenderUtils.blend(false);
            RenderUtils.depthTest(true);
            RenderUtils.culling(true);
            RenderUtils.depthMask(true);
             */
        }
    }

    // OG / Tweakeroo method also
    public static void renderInventoryOverlayOG(DrawContext drawContext, InventoryOverlay.Context context, MinecraftClient mc)
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

        final boolean isWolf = (entityLivingBase instanceof WolfEntity) || (entityLivingBase instanceof HappyGhastEntity) || (entityLivingBase instanceof CopperGolemEntity);
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

                InventoryOverlay.renderInventoryBackground(drawContext, type, xInv, yInv, 1, 2, mc);
                if (type == InventoryOverlay.InventoryRenderType.LLAMA)
                {
                    InventoryOverlay.renderLlamaArmorBackgroundSlots(drawContext, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY);
                }
                else
                {
                    InventoryOverlay.renderHorseArmorBackgroundSlots(drawContext, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY);
                }
                InventoryOverlay.renderInventoryStacks(drawContext, type, horseInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc);
                xInv += 32 + 4;
            }

            if (totalSlots > 0)
            {
                InventoryOverlay.renderInventoryBackground(drawContext, type, xInv, yInv, props.slotsPerRow, totalSlots, mc);
                // TODO 1.21.4+
                if (type == InventoryOverlay.InventoryRenderType.BREWING_STAND)
                {
                    InventoryOverlay.renderBrewerBackgroundSlots(drawContext, inv, xInv, yInv);
                }
                InventoryOverlay.renderInventoryStacks(drawContext, type, inv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, firstSlot, totalSlots, lockedSlots, mc);
            }
        }

        if (isWolf)
        {
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.WOLF;
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
            InventoryOverlay.renderInventoryBackground(drawContext, type, xInv, yInv, 1, 2, mc);
            InventoryOverlay.renderWolfArmorBackgroundSlots(drawContext, wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY);
            InventoryOverlay.renderInventoryStacks(drawContext, type, wolfInv, xInv + props.slotOffsetX, yInv + props.slotOffsetY, 1, 0, 2, mc);
        }

        if (entityLivingBase != null)
        {
            InventoryOverlay.renderEquipmentOverlayBackground(drawContext, x, y, entityLivingBase);
            InventoryOverlay.renderEquipmentStacks(drawContext, entityLivingBase, x, y, mc);
        }
    }
}
