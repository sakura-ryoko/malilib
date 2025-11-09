package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IRenderDispatcher;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.InfoUtils;

public class RenderEventHandler implements IRenderDispatcher
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();

    private final List<IRenderer> overlayRenderers = new ArrayList<>();
    private final List<IRenderer> tooltipLastRenderers = new ArrayList<>();
//    private final List<IRenderer> worldPreMainRenderers = new ArrayList<>();
    private final List<IRenderer> worldPostDebugRenderers = new ArrayList<>();
//    private final List<IRenderer> worldLayerPassRenderers = new ArrayList<>();
//    private final List<IRenderer> worldPreParticleRenderers = new ArrayList<>();
    private final List<IRenderer> worldPreWeatherRenderers = new ArrayList<>();
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();
    private final List<IRenderer> specialGuiRenderers = new ArrayList<>();

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

//    @Override
//    public void registerWorldPreMainRenderer(IRenderer renderer)
//    {
//        if (this.worldPreMainRenderers.contains(renderer) == false)
//        {
//            this.worldPreMainRenderers.add(renderer);
//        }
//    }

    @Override
    public void registerWorldPostDebugRenderer(IRenderer renderer)
    {
        if (this.worldPostDebugRenderers.contains(renderer) == false)
        {
            this.worldPostDebugRenderers.add(renderer);
        }
    }

//    @Override
//    public void registerWorldLayerPassRenderer(IRenderer renderer)
//    {
//        if (this.worldLayerPassRenderers.contains(renderer) == false)
//        {
//            this.worldLayerPassRenderers.add(renderer);
//        }
//    }

//    @Override
//    public void registerWorldPreParticleRenderer(IRenderer renderer)
//    {
//        if (this.worldPreParticleRenderers.contains(renderer) == false)
//        {
//            this.worldPreParticleRenderers.add(renderer);
//        }
//    }

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

    @Override
    public void registerSpecialGuiRenderer(IRenderer renderer)
    {
        if (this.specialGuiRenderers.contains(renderer) == false)
        {
            this.specialGuiRenderers.add(renderer);
        }
    }

//    @ApiStatus.Internal
//    public void onRenderGameOverlayLastDrawer(DrawContext drawContext, MinecraftClient mc, float partialTicks)
//    {
//        Profiler profiler = Profilers.get();
//
//        profiler.push(MaLiLibReference.MOD_ID+"_overlay_last_drawer");
//
//        if (this.overlayRenderers.isEmpty() == false)
//        {
//            for (IRenderer renderer : this.overlayRenderers)
//            {
//                profiler.push(renderer.getProfilerSectionSupplier());
//                renderer.onRenderGameOverlayLastDrawer(drawContext, partialTicks, profiler, mc);
//                profiler.pop();
//            }
//        }
//
//        profiler.pop();
//    }

    @ApiStatus.Internal
    public void onRenderGameOverlayPost(GuiGraphics drawContext, Minecraft mc, float partialTicks)
    {
        ProfilerFiller profiler = Profiler.get();

        profiler.push(MaLiLibReference.MOD_ID+"_game_overlay");

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

        profiler.popPush(MaLiLibReference.MOD_ID+"_game_messages");
        InfoUtils.renderInGameMessages(drawContext);
        profiler.pop();
    }

    @ApiStatus.Internal
    public void onRenderTooltipComponentInsertFirst(Item.TooltipContext context, ItemStack stack, Consumer<Component> list)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.tooltipLastRenderers)
            {
                renderer.onRenderTooltipComponentInsertFirst(context, stack, list);
            }
        }
    }

    @ApiStatus.Internal
    public void onRenderTooltipComponentInsertMiddle(Item.TooltipContext context, ItemStack stack, Consumer<Component> list)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.tooltipLastRenderers)
            {
                renderer.onRenderTooltipComponentInsertMiddle(context, stack, list);
            }
        }
    }

    @ApiStatus.Internal
    public void onRenderTooltipComponentInsertLast(Item.TooltipContext context, ItemStack stack, Consumer<Component> list)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.tooltipLastRenderers)
            {
                renderer.onRenderTooltipComponentInsertLast(context, stack, list);
            }
        }
    }

    @ApiStatus.Internal
    public void onRenderTooltipLast(GuiGraphics drawContext, ItemStack stack, int x, int y)
    {
        ProfilerFiller profiler = Profiler.get();

        profiler.push(MaLiLibReference.MOD_ID+"_tooltip");

        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.tooltipLastRenderers)
            {
                profiler.popPush(renderer.getProfilerSectionSupplier());
                renderer.onRenderTooltipLast(drawContext ,stack, x, y);
            }
        }

        profiler.pop();
    }

//    @ApiStatus.Internal
//    public void runRenderWorldPreMain(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc,
//                                           FrameGraphBuilder frameGraphBuilder, DefaultFramebufferSet fbSet,
//                                           Frustum frustum, Camera camera, BufferBuilderStorage buffers,
//                                           Profiler profiler)
//    {
//        profiler.push(MaLiLibReference.MOD_ID+"_pre_main");
//
//        if (this.worldPreMainRenderers.isEmpty() == false)
//        {
//            FramePass pass = frameGraphBuilder.createPass(MaLiLibReference.MOD_ID+"_pre_main");
//
//            fbSet.mainFramebuffer = pass.transfer(fbSet.mainFramebuffer);
//            Handle<Framebuffer> handleMain = fbSet.mainFramebuffer;
//
//            pass.setRenderer(() ->
//                             {
//                                 Fog fog = RenderSystem.getShaderFog();
//                                 RenderSystem.setShaderFog(Fog.DUMMY);
//
//                                 //handleMain.get().beginWrite(false);
//                                 // RenderUtils.fbStartDrawing();
//
//                                 for (IRenderer renderer : this.worldPreMainRenderers)
//                                 {
//                                     profiler.push(renderer.getProfilerSectionSupplier());
//                                     renderer.onRenderWorldPreMain(handleMain.get(), posMatrix, projMatrix, frustum, camera, fog, buffers, profiler);
//                                     profiler.pop();
//                                 }
//
//                                 if (!this.worldPreMainRenderers.isEmpty())
//                                 {
//                                     handleMain.get().blitToScreen();
//                                 }
//
//                                 RenderSystem.setShaderFog(fog);
//                             });
//
//            if (!this.worldPreMainRenderers.isEmpty())
//            {
//                pass.markToBeVisited();
//            }
//        }
//
//        profiler.pop();
//    }

//    @ApiStatus.Internal
//    public void runRenderWorldLayerPass(RenderLayer layer, Matrix4f posMatrix, Matrix4f projMatrix, Vec3d camera, MinecraftClient mc,
//                                        ObjectListIterator<ChunkBuilder.BuiltChunk> chunkIterator,
//                                        ArrayList<RenderPass.RenderObject> renderObjects)
//    {
//        Profiler profiler = Profilers.get();
//
//        profiler.push(MaLiLibReference.MOD_ID+"_render_layer");
//
//        if (this.worldLayerPassRenderers.isEmpty() == false)
//        {
//            for (IRenderer renderer : this.worldLayerPassRenderers)
//            {
//                profiler.push(renderer.getProfilerSectionSupplier());
//                renderer.onRenderWorldLayerPass(layer, posMatrix, projMatrix, camera, profiler, chunkIterator, renderObjects);
//                profiler.pop();
//            }
//        }
//
//        profiler.pop();
//    }

    @ApiStatus.Internal
    public void runRenderWorldPostDebug(PoseStack matrices, Frustum frustum, MultiBufferSource.BufferSource immediate, Vec3 camera)
    {
        ProfilerFiller profiler = Profiler.get();

        profiler.push(MaLiLibReference.MOD_ID+"_post_debug");

        if (this.worldPostDebugRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.worldPostDebugRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onRenderWorldPostDebugRender(matrices, frustum, immediate, camera, profiler);
                profiler.pop();
            }
        }

        profiler.pop();
    }

//    @ApiStatus.Internal
//    public void runRenderWorldPreParticles(Matrix4f posMatrix, Matrix4f projMatrix, MinecraftClient mc,
//                                           FrameGraphBuilder frameGraphBuilder, DefaultFramebufferSet fbSet,
//                                           Frustum frustum, Camera camera, BufferBuilderStorage buffers,
//                                           Profiler profiler)
//    {
//        profiler.push(MaLiLibReference.MOD_ID+"_pre_particles");
//
//        if (this.worldPreParticleRenderers.isEmpty() == false)
//        {
//            FramePass pass = frameGraphBuilder.createPass(MaLiLibReference.MOD_ID+"_pre_particles");
//
//            if (fbSet.particlesFramebuffer != null)
//            {
//                fbSet.particlesFramebuffer = pass.transfer(fbSet.particlesFramebuffer);
//                pass.dependsOn(fbSet.mainFramebuffer);
//            }
//            else
//            {
//                fbSet.mainFramebuffer = pass.transfer(fbSet.mainFramebuffer);
//            }
//
//            Handle<Framebuffer> handleMain = fbSet.mainFramebuffer;
//            Handle<Framebuffer> handleParticles = fbSet.particlesFramebuffer;
//
//            pass.setRenderer(() ->
//            {
//                Fog fog = RenderSystem.getShaderFog();
//                RenderSystem.setShaderFog(Fog.DUMMY);
//
//                if (handleParticles != null)
//                {
//                    handleParticles.get().copyDepthFrom(handleMain.get());
//                }
//
//                Framebuffer fb = handleParticles != null ? handleParticles.get() : handleMain.get();
//                //handleMain.get().beginWrite(false);
//                // RenderUtils.fbStartDrawing();
//
//                for (IRenderer renderer : this.worldPreParticleRenderers)
//                {
//                    profiler.push(renderer.getProfilerSectionSupplier());
//                    renderer.onRenderWorldPreParticles(fb, posMatrix, projMatrix, frustum, camera, fog, buffers, profiler);
//                    profiler.pop();
//                }
//
//                if (!this.worldPreParticleRenderers.isEmpty())
//                {
//                    fb.blitToScreen();
//                }
//
//                RenderSystem.setShaderFog(fog);
//            });
//
//            if (!this.worldPreParticleRenderers.isEmpty())
//            {
//                pass.markToBeVisited();
//            }
//        }
//
//        profiler.pop();
//    }

    @ApiStatus.Internal
    public void runRenderWorldPreWeather(Matrix4f posMatrix, Matrix4f projMatrix, Minecraft mc,
                                         FrameGraphBuilder frameGraphBuilder, LevelTargetBundle fbSet,
                                         Frustum frustum, Camera camera, RenderBuffers buffers,
                                         ProfilerFiller profiler)
    {
        profiler.push(MaLiLibReference.MOD_ID+"_pre_weather");

        if (this.worldPreWeatherRenderers.isEmpty() == false)
        {
            FramePass pass = frameGraphBuilder.addPass(MaLiLibReference.MOD_ID+"_pre_weather");

//            if (fbSet.weatherFramebuffer != null)
//            {
//                fbSet.weatherFramebuffer = pass.transfer(fbSet.weatherFramebuffer);
//                pass.dependsOn(fbSet.mainFramebuffer);
//            }
//            else
//            {
                fbSet.main = pass.readsAndWrites(fbSet.main);
//            }

            ResourceHandle<RenderTarget> handleMain = fbSet.main;
//            Handle<Framebuffer> handleWeather = fbSet.weatherFramebuffer;

            pass.executes(() ->
            {
                GpuBufferSlice fog = RenderSystem.getShaderFog();
//                RenderSystem.setShaderFog(Fog.DUMMY);

//                if (handleWeather != null)
//                {
//                    handleWeather.get().copyDepthFrom(handleMain.get());
//                }

//                Framebuffer fb = handleWeather != null ? handleWeather.get() : handleMain.get();
                RenderTarget fb = handleMain.get();
                //handleMain.get().beginWrite(false);
                //RenderUtils.fbStartDrawing();

                for (IRenderer renderer : this.worldPreWeatherRenderers)
                {
                    profiler.push(renderer.getProfilerSectionSupplier());
                    renderer.onRenderWorldPreWeather(fb, posMatrix, projMatrix, frustum, camera, buffers, profiler);
                    profiler.pop();
                }

//                if (!this.worldPreWeatherRenderers.isEmpty())
//                {
//                    fb.draw();
//                }

                RenderSystem.setShaderFog(fog);
            });

            if (!this.worldPreWeatherRenderers.isEmpty())
            {
                pass.disableCulling();
            }
        }

        profiler.pop();
    }

    @ApiStatus.Internal
    public void runRenderWorldLast(Matrix4f posMatrix, Matrix4f projMatrix, Minecraft mc,
                                   FrameGraphBuilder frameGraphBuilder, LevelTargetBundle fbSet,
                                   Frustum frustum, Camera camera, RenderBuffers buffers,
                                   ProfilerFiller profiler)
    {
        profiler.push(MaLiLibReference.MOD_ID+"_world_last");

        if (this.worldLastRenderers.isEmpty() == false)
        {
            FramePass pass = frameGraphBuilder.addPass(MaLiLibReference.MOD_ID+"_world_last");

//            if (fbSet.entityOutlineFramebuffer != null)
//            {
//                fbSet.entityOutlineFramebuffer = pass.transfer(fbSet.entityOutlineFramebuffer);
//                pass.dependsOn(fbSet.mainFramebuffer);
//            }
//            else
//            {
                fbSet.main = pass.readsAndWrites(fbSet.main);
//            }

            ResourceHandle<RenderTarget> handleMain = fbSet.main;
//            Handle<Framebuffer> handleOutlines = fbSet.entityOutlineFramebuffer;

            pass.executes(() ->
            {
                GpuBufferSlice fog = RenderSystem.getShaderFog();
//                RenderSystem.setShaderFog(Fog.DUMMY);

//                if (handleOutlines != null)
//                {
//                    handleOutlines.get().copyDepthFrom(handleMain.get());
//                }
//
//                Framebuffer fb = handleOutlines != null ? handleOutlines.get() : handleMain.get();
                //handleMain.get().beginWrite(false);
                //RenderUtils.fbStartDrawing();

                for (IRenderer renderer : this.worldLastRenderers)
                {
                    profiler.push(renderer.getProfilerSectionSupplier());
                    // This really should be used either or, and never both in the same mod.
                    renderer.onRenderWorldLastAdvanced(handleMain.get(), posMatrix, projMatrix, frustum, camera, buffers, profiler);
                    renderer.onRenderWorldLast(posMatrix, projMatrix);
                    profiler.pop();
                }

//                if (!this.worldLastRenderers.isEmpty())
//                {
//                    fb.blitToScreen();
//                }

                RenderSystem.setShaderFog(fog);
            });

            if (!this.worldLastRenderers.isEmpty())
            {
                pass.disableCulling();
            }
        }

        profiler.pop();
    }

    @ApiStatus.Internal
    public void onRegisterSpecialGuiRenderer(GuiRenderer guiRenderer, MultiBufferSource.BufferSource immediate, Minecraft mc, ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder)
    {
        MaLiLib.LOGGER.warn("onRegisterSpecialGuiRenderer():");

        if (this.specialGuiRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.specialGuiRenderers)
            {
                MaLiLib.LOGGER.warn("onRegisterSpecialGuiRenderer(): render for [{}]", renderer.getClass().getName());
                renderer.onRegisterSpecialGuiRenderer(guiRenderer, immediate, mc, builder);
            }
        }
    }
}
