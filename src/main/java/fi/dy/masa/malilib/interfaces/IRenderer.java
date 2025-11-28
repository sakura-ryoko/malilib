package fi.dy.masa.malilib.interfaces;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.joml.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import fi.dy.masa.malilib.render.GuiContext;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered, with advanced Parameters such as ticks, drawer, profiler
     */
    default void onRenderGameOverlayPostAdvanced(GuiContext ctx, float partialTicks, ProfilerFiller profiler) {}

    /**
     * Called after the vanilla overlays have been rendered (Original)
     */
    default void onRenderGameOverlayPost(GuiContext ctx) {}

    /**
     * Called after vanilla debug rendering (Chunk Borders, etc)
     */
//    default void onRenderWorldPostDebugRender(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate immediate, Vec3d camera, Profiler profiler) {}

    /**
     * Called before vanilla Weather rendering
     */
    default void onRenderWorldPreWeather(RenderTarget fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, RenderBuffers buffers, ProfilerFiller profiler) {}

    /**
     * Called after vanilla world rendering, with advanced Parameters, such as Frustum, Camera, and Fog
     */
    default void onRenderWorldLastAdvanced(RenderTarget fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, RenderBuffers buffers, ProfilerFiller profiler) {}

    /**
     * Called after vanilla world rendering (Original)
     */
    default void onRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix) {}

    /**
     * Called only after the tooltip text adds the Item Name.
     * If you want to 'Modify' the item name/Title, this is where
     * you should do it; or just insert text below it as normal.
     */
    default void onRenderTooltipComponentInsertFirst(Item.TooltipContext context, ItemStack stack, Consumer<Component> list) {}

    /**
     * Called before the regular tooltip text data components
     * of an item, such as the Music Disc info, Trims, and Lore,
     * but after the regular item 'additional' item tooltips.
     */
    default void onRenderTooltipComponentInsertMiddle(Item.TooltipContext context, ItemStack stack, Consumer<Component> list) {}

    /**
     * Called after the tooltip text components of an item has been added,
     * and occurs before the item durability, id, and component count.
     */
    default void onRenderTooltipComponentInsertLast(Item.TooltipContext context, ItemStack stack, Consumer<Component> list) {}

    /**
     * Called after the tooltip text of an item has been rendered
     */
    default void onRenderTooltipLast(GuiContext ctx, ItemStack stack, int x, int y) {}

    /**
     * Returns a supplier for the profiler section name that should be used for this renderer
     */
    default Supplier<String> getProfilerSectionSupplier()
    {
        return () -> this.getClass().getName();
    }

    /**
     * Register your Special Gui Element (PIP) Renderer.
     * Simply bind your sSpecial Gui Element State / Renderer to the Immutable Map Builder using this.
     * -
     * !!!WARNING!!!  This is called in the early Game Pre-Init() 'clinit' phase!
     *
     * @param guiRenderer ()
     * @param immediate ()
     * @param mc ()
     * @param builder ()
     */
    default void onRegisterSpecialGuiRenderer(GuiRenderer guiRenderer, MultiBufferSource.BufferSource immediate, Minecraft mc, ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder) { }
}
