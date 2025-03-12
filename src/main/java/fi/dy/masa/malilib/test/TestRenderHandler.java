package fi.dy.masa.malilib.test;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.game.BlockUtils;
import fi.dy.masa.malilib.util.nbt.NbtBlockUtils;

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

    @Override
    public void onRenderWorldLayerPass(RenderLayer layer, Matrix4f posMatrix, Matrix4f projMatrix, Vec3d camera, Profiler profiler, ObjectListIterator<ChunkBuilder.BuiltChunk> chunkIterator, ArrayList<RenderPass.RenderObject> renderObjects)
    {
        // NO-OP
    }

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
    public void onRenderWorldPreWeather(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler)
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
    public void onRenderWorldLastAdvanced(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler)
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
             */
            RenderUtils.blend(true);

            Color4f color = Color4f.fromColor(StringUtils.getColor("#C03030F0", 0));

            RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.getSide(),
                    hitResult.getPos(),
                    color, posMatrix);

            RenderUtils.blend(false);
            /*
            RenderUtils.depthTest(true);
            RenderUtils.culling(true);
            RenderUtils.depthMask(true);
             */
        }
    }

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

                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc, drawContext);
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
                InventoryOverlay.renderInventoryBackground(type, xInv, yInv, props.slotsPerRow, totalSlots, mc, drawContext);
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
            InventoryOverlay.renderInventoryBackground(type, xInv, yInv, 1, 2, mc, drawContext);
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
