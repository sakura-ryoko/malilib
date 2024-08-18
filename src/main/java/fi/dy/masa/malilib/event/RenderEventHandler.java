package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.interfaces.IRenderDispatcher;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.InfoUtils;

public class RenderEventHandler implements IRenderDispatcher
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();

    private final List<IRenderer> overlayRenderers = new ArrayList<>();
    private final List<IRenderer> tooltipLastRenderers = new ArrayList<>();
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();

    public static IRenderDispatcher getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void registerGameOverlayRenderer(IRenderer renderer)
    {
        if (this.overlayRenderers.contains(renderer) == false)
        {
            this.overlayRenderers.add(renderer);
        }
    }

    @Override
    public void registerTooltipLastRenderer(IRenderer renderer)
    {
        if (this.tooltipLastRenderers.contains(renderer) == false)
        {
            this.tooltipLastRenderers.add(renderer);
        }
    }

    @Override
    public void registerWorldLastRenderer(IRenderer renderer)
    {
        if (this.worldLastRenderers.contains(renderer) == false)
        {
            this.worldLastRenderers.add(renderer);
        }
    }

    @ApiStatus.Internal
    public void onRenderGameOverlayPost(DrawContext drawContext, MinecraftClient mc, float partialTicks)
    {
        mc.getProfiler().push("malilib_rendergameoverlaypost");

        if (this.overlayRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.overlayRenderers)
            {
                mc.getProfiler().push(renderer.getProfilerSectionSupplier());
                renderer.onRenderGameOverlayPost(drawContext);
                mc.getProfiler().pop();
            }
        }

        mc.getProfiler().push("malilib_ingamemessages");
        InfoUtils.renderInGameMessages(drawContext);
        mc.getProfiler().pop();

        mc.getProfiler().pop();
    }

    @ApiStatus.Internal
    public void onRenderTooltipLast(DrawContext drawContext, ItemStack stack, int x, int y)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.tooltipLastRenderers)
            {
                renderer.onRenderTooltipLast(drawContext ,stack, x, y);
            }
        }
    }

    @ApiStatus.Internal
    public void onRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, boolean hasTransparency)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            mc.getProfiler().swap("malilib_renderworldlast");

            // This is required to prevent crashes
            if (hasTransparency)
            {
                Framebuffer fb = MinecraftClient.isFabulousGraphicsOrBetter() ? mc.worldRenderer.getTranslucentFramebuffer() : null;

                if (fb != null)
                {
                    fb.beginWrite(false);
                }

                for (IRenderer renderer : this.worldLastRenderers)
                {
                    mc.getProfiler().push(renderer.getProfilerSectionSupplier());
                    renderer.onRenderWorldLast(posMatrix, projMatrix);
                    mc.getProfiler().pop();
                }

                if (fb != null)
                {
                    mc.getFramebuffer().beginWrite(false);
                }
            }
            else
            {
                for (IRenderer renderer : this.worldLastRenderers)
                {
                    mc.getProfiler().push(renderer.getProfilerSectionSupplier());
                    renderer.onRenderWorldLast(posMatrix, projMatrix);
                    mc.getProfiler().pop();
                }
            }
        }
    }
}
