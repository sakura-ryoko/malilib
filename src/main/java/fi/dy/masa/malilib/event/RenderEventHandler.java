package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IRenderDispatcher;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.InfoUtils;

public class RenderEventHandler implements IRenderDispatcher
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();

    private final List<IRenderer> overlayRenderers = new ArrayList<>();
    private final List<IRenderer> tooltipLastRenderers = new ArrayList<>();
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();

    private Matrix4f posMatrix;
    private Matrix4f projMatrix;
    private MinecraftClient mc;
    private boolean hasTransparency;

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

    /**
     * This configures / stores the required information for the Post Phase.
     * @param posMatrix (Position Matrix)
     * @param projMatrix (Projection Matrix)
     * @param mc (Client)
     * @param hasTransparency (Whether the transparency buffers are being utilized)
     */
    @ApiStatus.Internal
    public void onRenderWorldPre(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc, boolean hasTransparency)
    {
        if (this.mc == null)
        {
            this.mc = mc;
        }

        this.posMatrix = posMatrix;
        this.projMatrix = projMatrix;
        this.hasTransparency = hasTransparency;
    }

    /**
     * This is the "Execution" phase of the new WorldRenderer system.
     * @param cameraX (x)
     * @param cameraY (y)
     * @param cameraZ (z)
     */
    @ApiStatus.Internal
    public void onRenderWorldPost(double cameraX, double cameraY, double cameraZ)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            this.mc.getProfiler().swap("malilib_renderworldpost");
            Framebuffer fb = null;
            //Fog fog = RenderSystem.getShaderFog();
            //RenderSystem.setShaderFog(Fog.DUMMY);

            if (this.hasTransparency && this.mc.worldRenderer != null)
            {
                try
                {
                    //fb = MinecraftClient.isFabulousGraphicsOrBetter() ? this.mc.worldRenderer.getTranslucentFramebuffer() : null;
                    fb = MinecraftClient.isFabulousGraphicsOrBetter() ? this.mc.worldRenderer.getEntityOutlinesFramebuffer() : null;
                }
                catch (Exception e)
                {
                    MaLiLib.logger.warn("onRenderWorldPost: getTranslucentFramebuffer() throw: [{}]", e.getMessage());
                }
            }

            if (fb != null)
            {
                fb.beginWrite(false);
            }

            //this.mc.gameRenderer.getLightmapTextureManager().enable();

            for (IRenderer renderer : this.worldLastRenderers)
            {
                this.mc.getProfiler().push(renderer.getProfilerSectionSupplier());
                renderer.onRenderWorldLast(this.posMatrix, this.projMatrix);
                this.mc.getProfiler().pop();
            }

            //this.mc.gameRenderer.getLightmapTextureManager().disable();

            if (fb != null)
            {
                this.mc.getFramebuffer().beginWrite(false);
            }

            //RenderSystem.setShaderFog(fog);
        }
    }
}
