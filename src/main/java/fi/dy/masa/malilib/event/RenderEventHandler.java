package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
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
    private final List<IRenderer> worldPreParticleRenderers = new ArrayList<>();
    private final List<IRenderer> worldPreWeatherRenderers = new ArrayList<>();
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
    public void registerWorldPreParticleRenderer(IRenderer renderer)
    {
        if (this.worldPreParticleRenderers.contains(renderer) == false)
        {
            this.worldPreParticleRenderers.add(renderer);
        }
    }

    @Override
    public void registerWorldPreWeatherRenderer(IRenderer renderer)
    {
        if (this.worldPreWeatherRenderers.contains(renderer) == false)
        {
            this.worldPreWeatherRenderers.add(renderer);
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

    /*
    @ApiStatus.Internal
    public void onRenderGameOverlayLastDrawer(DrawContext drawContext, MinecraftClient mc, float partialTicks)
    {
        Profiler profiler = Profilers.get();

        profiler.push("malilib_rendergameoverlaydrawer");

        if (this.overlayRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.overlayRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onRenderGameOverlayLastDrawer(drawContext, partialTicks, profiler, mc);
                profiler.pop();
            }
        }

        profiler.pop();
    }
     */

    @ApiStatus.Internal
    public void onRenderGameOverlayPost(DrawContext drawContext, MinecraftClient mc, float partialTicks)
    {
        Profiler profiler = Profilers.get();

        profiler.push("malilib_rendergameoverlaypost");

        if (this.overlayRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.overlayRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onRenderGameOverlayPostAdvanced(drawContext, partialTicks, profiler, mc);
                renderer.onRenderGameOverlayPost(drawContext);
                profiler.pop();
            }
        }

        profiler.push("malilib_ingamemessages");
        InfoUtils.renderInGameMessages(drawContext);
        profiler.pop();

        profiler.pop();
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
    public void runRenderWorldPreParticles(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc,
                                           FrameGraphBuilder frameGraphBuilder, DefaultFramebufferSet fbSet, Frustum frustum, Camera camera, Profiler profiler)
    {
        if (this.worldPreParticleRenderers.isEmpty() == false)
        {
            Handle<Framebuffer> handleMain;
            RenderPass renderPass = frameGraphBuilder.createPass(MaLiLibReference.MOD_ID);

            profiler.push(MaLiLibReference.MOD_ID+"_render_pre_particle");

            fbSet.mainFramebuffer = renderPass.transfer(fbSet.mainFramebuffer);
            handleMain = fbSet.mainFramebuffer;

            renderPass.setRenderer(() ->
            {
                Fog fog = RenderSystem.getShaderFog();
                ShaderProgram shaders = RenderSystem.getShader();

                if (shaders != null)
                {
                    shaders.initializeUniforms(VertexFormat.DrawMode.QUADS, posMatrix, projMatrix, mc.getWindow());
                    shaders.bind();
                }

                handleMain.get().beginWrite(false);
                for (IRenderer renderer : this.worldPreParticleRenderers)
                {
                    profiler.push(renderer.getProfilerSectionSupplier());
                    renderer.onRenderWorldPreParticle(posMatrix, projMatrix, frustum, camera, fog, profiler);
                    profiler.pop();
                }

                if (shaders != null)
                {
                    shaders.unbind();
                }
            });
        }

        profiler.pop();
    }

    @ApiStatus.Internal
    public void runRenderWorldPreWeather(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc,
                                           FrameGraphBuilder frameGraphBuilder, DefaultFramebufferSet fbSet, Frustum frustum, Camera camera, Profiler profiler)
    {
        if (this.worldPreWeatherRenderers.isEmpty() == false)
        {
            Handle<Framebuffer> handleMain;
            RenderPass renderPass = frameGraphBuilder.createPass(MaLiLibReference.MOD_ID);

            profiler.push(MaLiLibReference.MOD_ID+"_render_pre_weather");

            fbSet.mainFramebuffer = renderPass.transfer(fbSet.mainFramebuffer);
            handleMain = fbSet.mainFramebuffer;

            renderPass.setRenderer(() ->
            {
                Fog fog = RenderSystem.getShaderFog();
                ShaderProgram shaders = RenderSystem.getShader();

                if (shaders != null)
                {
                    shaders.initializeUniforms(VertexFormat.DrawMode.QUADS, posMatrix, projMatrix, mc.getWindow());
                    shaders.bind();
                }

                handleMain.get().beginWrite(false);
                for (IRenderer renderer : this.worldPreWeatherRenderers)
                {
                    profiler.push(renderer.getProfilerSectionSupplier());
                    renderer.onRenderWorldPreWeather(posMatrix, projMatrix, frustum, camera, fog, profiler);
                    profiler.pop();
                }

                if (shaders != null)
                {
                    shaders.unbind();
                }
            });
        }

        profiler.pop();
    }

    @ApiStatus.Internal
    public void runRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc,
                                   FrameGraphBuilder frameGraphBuilder, DefaultFramebufferSet fbSet, Frustum frustum, Camera camera, Profiler profiler)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            Handle<Framebuffer> handleMain;
            //Handle<Framebuffer> handleTransluclent;

            profiler.push(MaLiLibReference.MOD_ID+"_render_post");

            RenderPass renderPass = frameGraphBuilder.createPass(MaLiLibReference.MOD_ID);

            // FIXME --> Don't write to translucent Frame Buffer, bad things will happen,
            //  at Best, the Player will be able to see through objects ...
            /*
            if (fbSet.translucentFramebuffer != null)
            {
                fbSet.translucentFramebuffer = this.malilibRenderPass.transfer(fbSet.translucentFramebuffer);
                handleTransluclent = fbSet.translucentFramebuffer;
            }
            else
            {
             */
                fbSet.mainFramebuffer = renderPass.transfer(fbSet.mainFramebuffer);
                //handleTransluclent = null;
            //}

            handleMain = fbSet.mainFramebuffer;

            renderPass.setRenderer(() ->
            {
                Fog fog = RenderSystem.getShaderFog();
                //RenderSystem.setShaderFog(Fog.DUMMY);

                /*
                if (handleTransluclent != null)
                {
                    handleTransluclent.get().setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                    handleTransluclent.get().clear();
                    handleTransluclent.get().copyDepthFrom(handleMain.get());
                }
                 */

                ShaderProgram shaders = RenderSystem.getShader();

                if (shaders != null)
                {
                    shaders.initializeUniforms(VertexFormat.DrawMode.QUADS, posMatrix, projMatrix, mc.getWindow());
                    shaders.bind();
                }

                handleMain.get().beginWrite(false);
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
                        profiler.push(renderer.getProfilerSectionSupplier());
                        // This really should be used either or, and never both in the same mod.
                        renderer.onRenderWorldLastAdvanced(posMatrix, projMatrix, frustum, camera, fog, profiler);
                        renderer.onRenderWorldLast(posMatrix, projMatrix);
                        profiler.pop();
                    }

                /*
                if (fb != null)
                {
                    this.mc.getFramebuffer().beginWrite(false);
                }
                 */

                if (shaders != null)
                {
                    shaders.unbind();
                }

                //RenderSystem.setShaderFog(fog);
            });
        }

        profiler.pop();
    }
}
