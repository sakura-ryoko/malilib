package fi.dy.masa.malilib.interfaces;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;

public interface IRenderer
{
    /**
     * Called after the vanilla "drawer" overlays have been rendered
     */
    default void onRenderGameOverlayLastDrawer(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc) {}

    /**
     * Called after the vanilla overlays have been rendered, with advanced Parameters such as ticks, drawer, profiler
     */
    default void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc) {}

    /**
     * Called after the vanilla overlays have been rendered (Original)
     */
    default void onRenderGameOverlayPost(DrawContext drawContext) {}

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
     * Called only after the tooltip text adds the Item Name.
     * If you want to 'Modify' the item name/Title, this is where
     * you should do it; or just insert text below it as normal.
     */
    default void onRenderTooltipComponentInsertFirst(Item.TooltipContext context, ItemStack stack, List<Text> list) {}

    /**
     * Called before the regular tooltip text data components
     * of an item, such as the Music Disc info, Trims, and Lore,
     * but after the regular item 'additional' item tooltips.
     */
    default void onRenderTooltipComponentInsertMiddle(Item.TooltipContext context, ItemStack stack, List<Text> list) {}

    /**
     * Called after the tooltip text components of an item has been added,
     * and occurs before the item durability, id, and component count.
     */
    default void onRenderTooltipComponentInsertLast(Item.TooltipContext context, ItemStack stack, List<Text> list) {}

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
