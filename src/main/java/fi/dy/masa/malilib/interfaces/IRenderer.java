package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.Frustum;
import net.minecraft.item.ItemStack;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered, with advanced Parameters such as ticks, drawer, profiler
     */
    default void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, LayeredDrawer layeredDrawer, Profiler profiler, MinecraftClient mc) {}

    /**
     * Called after the vanilla overlays have been rendered (Original)
     */
    default void onRenderGameOverlayPost(DrawContext drawContext) {}

    /**
     * Called before vanilla Particle rendering
     */
    default void onRenderWorldPreParticle(Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, Profiler profiler) {}

    /**
     * Called before vanilla Weather rendering
     */
    default void onRenderWorldPreWeather(Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, Profiler profiler) {}

    /**
     * Called after vanilla world rendering, with advanced Parameters, such as Frustum, Camera, and Fog
     */
    default void onRenderWorldLastAdvanced(Matrix4f posMatrix, Matrix4f projMatrix, Frustum frustum, Camera camera, Fog fog, Profiler profiler) {}

    /**
     * Called after vanilla world rendering (Original)
     */
    default void onRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix) {}

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
