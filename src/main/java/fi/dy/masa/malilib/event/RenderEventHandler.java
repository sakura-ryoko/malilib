package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.item.ItemStack;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IRenderDispatcher;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.InfoUtils;

public class RenderEventHandler implements IRenderDispatcher
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();

    private final List<IRenderer> overlayRenderers = new ArrayList<>();
    private final List<IRenderer> tooltipLastRenderers = new ArrayList<>();
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();

    private RenderPass malilibRenderPass;
    private Matrix4f posMatrix;
    private Matrix4f projMatrix;
    private MinecraftClient mc;

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
     */
    @ApiStatus.Internal
    public void onRenderWorldPre(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc)
    {
        if (this.mc == null)
        {
            this.mc = mc;
        }

        this.posMatrix = posMatrix;
        this.projMatrix = projMatrix;
    }

    /**
     * This creates the MaLiLib Render Phase
     * @param frameGraphBuilder (Required object)
     */
    @ApiStatus.Internal
    public void onRenderWorldCreatePass(FrameGraphBuilder frameGraphBuilder)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            this.malilibRenderPass = frameGraphBuilder.createPass(MaLiLibReference.MOD_ID);
        }
    }

    /**
     * This is the "Execution" phase of the new WorldRenderer system.  This new method supports Shaders.
     * @param fbSet (WorldRenderer FrameBufferSet Object)
     * @param frustum (Frustum Object)
     * @param camera (Camera Object)
     */
    @ApiStatus.Internal
    public void onRenderWorldRunPass(DefaultFramebufferSet fbSet, Frustum frustum, Camera camera)
    {
        if (this.worldLastRenderers.isEmpty() == false &&
            this.malilibRenderPass != null)
        {
            Handle<Framebuffer> handle;
            //Handle<Framebuffer> handle2;

            this.mc.getProfiler().push(MaLiLibReference.MOD_ID+"_render_pass");

            // FIXME --> Don't write to translucent Frame Buffer, bad things will happen,
            //  at Best, the Player will be able to see through objects with a Stained Glass Block.
            /*
            if (fbSet.translucentFramebuffer != null)
            {
                fbSet.translucentFramebuffer = this.malilibRender.transfer(fbSet.translucentFramebuffer);
                handle2 = fbSet.translucentFramebuffer;
            }
            else
            {
             */
                fbSet.mainFramebuffer = this.malilibRenderPass.transfer(fbSet.mainFramebuffer);
                //handle2 = null;
            //}

            handle = fbSet.mainFramebuffer;

            this.malilibRenderPass.setRenderer(() ->
            {
                Fog fog = RenderSystem.getShaderFog();
                //RenderSystem.setShaderFog(Fog.DUMMY);

                /*
                if (handle2 != null)
                {
                    handle2.get().setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                    handle2.get().clear();
                    handle2.get().copyDepthFrom(handle.get());
                }
                 */

                ShaderProgram shaders = RenderSystem.getShader();

                if (shaders != null)
                {
                    shaders.initializeUniforms(VertexFormat.DrawMode.QUADS, this.posMatrix, this.projMatrix, this.mc.getWindow());
                    shaders.bind();
                }

                handle.get().beginWrite(false);
                this.onRenderWorldPost(frustum, camera, fog);
                //handle.get().endWrite();

                if (shaders != null)
                {
                    shaders.unbind();
                }

                //RenderSystem.setShaderFog(fog);
            });
        }

        this.mc.getProfiler().pop();
    }

    /**
     * This is the "Execution" phase of the new WorldRenderer system.  Frustum / Camera only passed along in case it's wanted later.
     * @param frustum (Frustum Object)
     * @param camera (Camera Object)
     */
    @ApiStatus.Internal
    public void onRenderWorldPost(Frustum frustum, Camera camera, Fog fog)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            this.mc.getProfiler().swap(MaLiLibReference.MOD_ID+"_render_post");
            //Framebuffer fb = null;
            /*
            if (this.hasTransparency && this.mc.worldRenderer != null)
            {
                try
                {
                    fb = MinecraftClient.isFabulousGraphicsOrBetter() ? this.mc.worldRenderer.getTranslucentFramebuffer() : null;
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
             */

            for (IRenderer renderer : this.worldLastRenderers)
            {
                this.mc.getProfiler().push(renderer.getProfilerSectionSupplier());
                // This really should be used either or, and never both in the same mod.
                renderer.onRenderWorldLastAdvanced(this.posMatrix, this.projMatrix, frustum, camera, fog);
                renderer.onRenderWorldLast(this.posMatrix, this.projMatrix);
                this.mc.getProfiler().pop();
            }

            /*
            if (fb != null)
            {
                this.mc.getFramebuffer().beginWrite(false);
            }
             */
        }
    }

    @ApiStatus.Internal
    public void onRenderWorldEnd()
    {
        if (this.malilibRenderPass != null)
        {
            this.malilibRenderPass = null;
        }
        if (this.posMatrix != null)
        {
            this.posMatrix = null;
        }
        if (this.projMatrix != null)
        {
            this.projMatrix = null;
        }
    }
}
