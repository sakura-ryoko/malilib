package fi.dy.masa.malilib.test.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.test.config.ConfigTestEnum;
import fi.dy.masa.malilib.test.data.TestDataSyncer;
import fi.dy.masa.malilib.test.misc.TestSelector;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
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
    public void onRenderGameOverlayPostAdvanced(GuiContext ctx, float partialTicks, ProfilerFiller profiler)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            if (MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getBooleanValue() &&
                MaLiLibConfigs.Test.TEST_INVENTORY_OVERLAY.getKeybind().isKeybindHeld())
            {
	            TestInventoryOverlayHandler.getInstance().getRenderContext(ctx, profiler);
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

                RenderUtils.renderText(ctx, 4, 4, MaLiLibConfigs.Test.TEST_CONFIG_FLOAT.getFloatValue(), 0xFFE0E0E0, 0xA0505050, HudAlignment.TOP_LEFT, true, false, true, list);
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
//    public void onRenderWorldPostDebugRender(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate immediate, Vec3d camera, Profiler profiler)
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
    public void onRenderWorldPreWeather(RenderTarget fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, RenderBuffers buffers, ProfilerFiller profiler)
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
    public void onRenderWorldLastAdvanced(RenderTarget fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, RenderBuffers buffers, ProfilerFiller profiler)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null)
            {
                profiler.push(MaLiLibReference.MOD_ID + "_selector");

                if (TestSelector.INSTANCE.shouldRender())
                {
                    TestSelector.INSTANCE.render(posMatrix, projMatrix, profiler, mc);
                }

                profiler.popPush(MaLiLibReference.MOD_ID + "_targeting_overlay");
                this.renderTargetingOverlay(posMatrix, mc);

                profiler.popPush(MaLiLibReference.MOD_ID + "_test_walls");

                if (ConfigTestEnum.TEST_WALLS_HOTKEY.getBooleanValue())
                {
                    if (TestRenderWalls.INSTANCE.needsUpdate(mc.getCameraEntity(), mc))
                    {
                        TestRenderWalls.INSTANCE.update(camera, mc.getCameraEntity(), mc);
                    }

                    TestRenderWalls.INSTANCE.render(camera, posMatrix, projMatrix, mc, profiler);
                }

                profiler.pop();
            }
        }
    }

    @Override
    public void onRenderTooltipComponentInsertFirst(Item.TooltipContext context, ItemStack stack, Consumer<Component> list)
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
    public void onRenderTooltipComponentInsertMiddle(Item.TooltipContext context, ItemStack stack, Consumer<Component> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            list.accept(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.middle"));
        }
    }

    @Override
    public void onRenderTooltipComponentInsertLast(Item.TooltipContext context, ItemStack stack, Consumer<Component> list)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            list.accept(StringUtils.translateAsText(MaLiLibReference.MOD_ID+".gui.tooltip.test.last"));
        }
    }

    @Override
    public void onRenderTooltipLast(GuiContext ctx, ItemStack stack, int x, int y)
    {
        Item item = stack.getItem();
        ProfilerFiller profiler = Profiler.get();

        if (item instanceof MapItem)
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_map_preview");
                RenderUtils.renderMapPreview(ctx, stack, x, y, 160, false);
                profiler.pop();
            }
        }
        else if (stack.getComponents().has(DataComponents.CONTAINER) && InventoryUtils.shulkerBoxHasItems(stack))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_shulker_preview");
                RenderUtils.renderShulkerBoxPreview(ctx, stack, x, y, true);
                profiler.pop();
            }
        }
        else if (stack.getComponents().has(DataComponents.BUNDLE_CONTENTS) && InventoryUtils.bundleHasItems(stack))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                profiler.push(MaLiLibReference.MOD_ID + "_bundle_preview");
                RenderUtils.renderBundlePreview(ctx, stack, x, y, MaLiLibConfigs.Test.TEST_BUNDLE_PREVIEW_WIDTH.getIntegerValue(), true);
                profiler.pop();
            }
        }
        else if (stack.is(Items.ENDER_CHEST))
        {
            if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
            {
                Minecraft mc = Minecraft.getInstance();
                Level world = WorldUtils.getBestWorld(mc);

                if (mc.player == null || world == null)
                {
                    return;
                }

                Player player = world.getPlayerByUUID(mc.player.getUUID());

                if (player != null)
                {
                    Pair<Entity, CompoundData> pair = TestDataSyncer.getInstance().requestEntity(world, player.getId());
                    PlayerEnderChestContainer inv;

                    if (pair != null && pair.getRight() != null && pair.getRight().contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
                    {
                        inv = InventoryUtils.getPlayerEnderItemsFromData(pair.getRight(), world.registryAccess());
                    }
                    else if (pair != null && pair.getLeft() instanceof Player pe && !pe.getEnderChestInventory().isEmpty())
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
                            ListData list = nbtInv.sorted().toDataList(world.registryAccess());
                            CompoundData data = new CompoundData();

                            data.put(NbtKeys.ENDER_ITEMS, list);
                            RenderUtils.renderDataItemsPreview(ctx, stack, data, x, y, false);
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

    private void renderTargetingOverlay(Matrix4f posMatrix, Minecraft mc)
    {
        Entity entity = mc.getCameraEntity();

        if (entity != null &&
            mc.hitResult != null &&
            mc.hitResult.getType() == HitResult.Type.BLOCK &&
            MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
            GuiBase.isCtrlDown())
        {
            BlockHitResult hitResult = (BlockHitResult) mc.hitResult;
            Color4f color = Color4f.fromColor(StringUtils.getColor("#C03030F0", 0));

            RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.getDirection(),
                    hitResult.getLocation(),
                    color, posMatrix);
        }
    }
}
