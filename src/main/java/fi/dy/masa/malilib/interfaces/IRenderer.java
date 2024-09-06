package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.joml.Matrix4f;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.item.ItemStack;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered
     */
    default void onRenderGameOverlayPost(DrawContext drawContext) {}

    /**
     * Called after vanilla world rendering
     */
    default void onRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix) {}

    /**
     * Called after vanilla world rendering, and before the Weather Rendering, if Fabulous! Mode is active.
     */
    default void onRenderWorldPostEffects(@Nullable PostEffectProcessor processor, FrameGraphBuilder frameGraphBuilder, int fbWidth, int fbHeight, DefaultFramebufferSet fbSet) {}

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
}
