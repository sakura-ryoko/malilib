package fi.dy.masa.malilib.interfaces;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableMap;
import org.joml.Matrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

public interface IRenderer
{
    /**
     * Called after the vanilla "drawer" overlays have been rendered
     */
//    default void onRenderGameOverlayLastDrawer(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc) {}

    /**
     * Called after the vanilla overlays have been rendered, with advanced Parameters such as ticks, drawer, profiler
     */
    default void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc) {}

    /**
     * Called after the vanilla overlays have been rendered (Original)
     */
    default void onRenderGameOverlayPost(DrawContext drawContext) {}

    /**
     * Called before vanilla Main rendering (Only after the Sky is Drawn)
     */
//    default void onRenderWorldPreMain(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler) {}

    /**
     * Called during each and every RenderLayer Pass of the Main World Rendering.
     * Append `renderObjects` with your additional blocks to render on this layer by passing along each 'Baked Object' per a Built Chunk (Using the chunkIterator)
     */
//    default void onRenderWorldLayerPass(RenderLayer layer, Matrix4f posMatrix, Matrix4f projMatrix, Vec3d camera, Profiler profiler,
//                                        ObjectListIterator<ChunkBuilder.BuiltChunk> chunkIterator,
//                                        ArrayList<RenderPass.RenderObject> renderObjects) {}

    /**
     * Called after vanilla debug rendering (Chunk Borders, etc)
     */
    default void onRenderWorldPostDebugRender(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate immediate, Vec3d camera, Profiler profiler) {}

    /**
     * Called before vanilla Weather rendering
     */
//    default void onRenderWorldPreParticles(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, BufferBuilderStorage buffers, Profiler profiler) {}

    /**
     * Called before vanilla Weather rendering
     */
    default void onRenderWorldPreWeather(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, BufferBuilderStorage buffers, Profiler profiler) {}

    /**
     * Called after vanilla world rendering, with advanced Parameters, such as Frustum, Camera, and Fog
     */
    default void onRenderWorldLastAdvanced(Framebuffer fb, Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, BufferBuilderStorage buffers, Profiler profiler) {}

    /**
     * Called after vanilla world rendering (Original)
     */
    default void onRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix) {}

    /**
     * Called only after the tooltip text adds the Item Name.
     * If you want to 'Modify' the item name/Title, this is where
     * you should do it; or just insert text below it as normal.
     */
    default void onRenderTooltipComponentInsertFirst(Item.TooltipContext context, ItemStack stack, Consumer<Text> list) {}

    /**
     * Called before the regular tooltip text data components
     * of an item, such as the Music Disc info, Trims, and Lore,
     * but after the regular item 'additional' item tooltips.
     */
    default void onRenderTooltipComponentInsertMiddle(Item.TooltipContext context, ItemStack stack, Consumer<Text> list) {}

    /**
     * Called after the tooltip text components of an item has been added,
     * and occurs before the item durability, id, and component count.
     */
    default void onRenderTooltipComponentInsertLast(Item.TooltipContext context, ItemStack stack, Consumer<Text> list) {}

    /**
     * Called after the tooltip text of an item has been rendered
     */
    default void onRenderTooltipLast(DrawContext drawContext, ItemStack stack, int x, int y) {}

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
    default void onRegisterSpecialGuiRenderer(GuiRenderer guiRenderer, VertexConsumerProvider.Immediate immediate, MinecraftClient mc, ImmutableMap.Builder<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> builder) { }
}
