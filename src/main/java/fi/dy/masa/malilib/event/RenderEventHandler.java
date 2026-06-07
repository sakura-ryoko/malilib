package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IRenderDispatcher;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.InfoUtils;

public class RenderEventHandler implements IRenderDispatcher
{
    private static final RenderEventHandler INSTANCE = new RenderEventHandler();

    private final List<IRenderer> inGameGuiRenderers = new ArrayList<>();
    private final List<IRenderer> tooltipLastRenderers = new ArrayList<>();
    private final List<IRenderer> worldPreWeatherRenderers = new ArrayList<>();
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();
    private final List<IRenderer> specialGuiRenderers = new ArrayList<>();

    public static IRenderDispatcher getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void registerInGameGuiRenderer(IRenderer renderer)
    {
        if (this.inGameGuiRenderers.contains(renderer) == false)
        {
            this.inGameGuiRenderers.add(renderer);
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

    @ApiStatus.Internal
    public void runExtractGuiOverlayPost(GuiContext ctx, float partialTicks)
    {
        ProfilerFiller profiler = Profiler.get();

        if (this.inGameGuiRenderers.isEmpty() == false)
        {
            profiler.push(MaLiLibReference.MOD_ID+"_extract_gui_overlay_post");

            for (IRenderer renderer : this.inGameGuiRenderers)
            {
                profiler.push(renderer.getProfilerSectionSupplier());
                renderer.onExtractGuiOverlayPost(ctx, partialTicks, profiler);
                profiler.pop();
            }

            profiler.popPush(MaLiLibReference.MOD_ID+"_in_game_messages");
        }
        else
        {
            profiler.push(MaLiLibReference.MOD_ID+"_in_game_messages");
        }

        InfoUtils.renderInGameMessages(ctx);
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
    public void onRenderTooltipLast(GuiContext ctx, ItemStack stack, int x, int y)
    {
        if (this.tooltipLastRenderers.isEmpty() == false)
        {
            ProfilerFiller profiler = Profiler.get();
            profiler.push(MaLiLibReference.MOD_ID+"_tooltip");

            for (IRenderer renderer : this.tooltipLastRenderers)
            {
                profiler.popPush(renderer.getProfilerSectionSupplier());
                renderer.onRenderTooltipLast(ctx ,stack, x, y);
            }

            profiler.pop();
        }
    }

    @ApiStatus.Internal
    public void runExtractWorldPreWeather(DeltaTracker deltaTracker, Camera camera, float ticks, ProfilerFiller profiler)
    {
        if (this.worldPreWeatherRenderers.isEmpty() == false)
        {
            profiler.push(MaLiLibReference.MOD_ID+"_extract_pre_weather");

            for (IRenderer renderer : this.worldPreWeatherRenderers)
            {
                renderer.onExtractWorldPreWeather(deltaTracker, camera, ticks, profiler);
            }

            profiler.pop();
        }
    }

    @ApiStatus.Internal
    public void runRenderWorldPreWeather(Matrix4fc modelViewMatrix, Minecraft mc,
                                         FrameGraphBuilder frameGraphBuilder, LevelTargetBundle fbSet,
                                         Frustum cullFrustum, CameraRenderState cameraState, RenderBuffers buffers,
                                         GpuBufferSlice terrainFog, Vector4f fogColor,
                                         ProfilerFiller profiler)
    {
        if (this.worldPreWeatherRenderers.isEmpty() == false)
        {
            profiler.push(MaLiLibReference.MOD_ID+"_render_pre_weather");
            FramePass pass = frameGraphBuilder.addPass(MaLiLibReference.MOD_ID+"_pre_weather");

            fbSet.main = pass.readsAndWrites(fbSet.main);

//            if (fbSet.translucent != null)
//            {
//                fbSet.translucent = pass.readsAndWrites(fbSet.translucent);
//            }

            ResourceHandle<@NotNull RenderTarget> handleMain = fbSet.main;
//            ResourceHandle<RenderTarget> handleTranslucent = fbSet.translucent;

            pass.executes(() ->
            {
                GpuBufferSlice fog = RenderSystem.getShaderFog();

//                if (handleTranslucent != null)
//                {
//                    handleTranslucent.get().copyDepthFrom(handleMain.get());
//                }

                for (IRenderer renderer : this.worldPreWeatherRenderers)
                {
                    profiler.push(renderer.getProfilerSectionSupplier());
                    renderer.onRenderWorldPreWeather(
//                            handleTranslucent != null ? handleTranslucent.get() : handleMain.get(),
                            handleMain.get(),
                            modelViewMatrix, cameraState, cullFrustum, buffers, terrainFog, fogColor, profiler);
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

            profiler.pop();
        }
    }

    @ApiStatus.Internal
    public void runExtractWorldLast(DeltaTracker deltaTracker, Camera camera, float ticks, ProfilerFiller profiler)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            profiler.push(MaLiLibReference.MOD_ID+"_extract_world_last");

            for (IRenderer renderer : this.worldLastRenderers)
            {
                renderer.onExtractWorldLast(deltaTracker, camera, ticks, profiler);
            }

            profiler.pop();
        }
    }

    @ApiStatus.Internal
    public void runRenderWorldLast(Matrix4fc modelViewMatrix, Minecraft mc,
                                   FrameGraphBuilder frameGraphBuilder, LevelTargetBundle fbSet,
                                   Frustum cullFrustum, CameraRenderState cameraState, RenderBuffers buffers,
                                   GpuBufferSlice terrainFog, Vector4f fogColor,
                                   ProfilerFiller profiler)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            profiler.push(MaLiLibReference.MOD_ID+"_render_world_last");
            FramePass pass = frameGraphBuilder.addPass(MaLiLibReference.MOD_ID+"_world_last");

            fbSet.main = pass.readsAndWrites(fbSet.main);

//            if (fbSet.translucent != null)
//            {
//                fbSet.translucent = pass.readsAndWrites(fbSet.translucent);
//            }

            ResourceHandle<@NotNull RenderTarget> handleMain = fbSet.main;
//            ResourceHandle<RenderTarget> handleTranslucent = fbSet.translucent;

            pass.executes(() ->
            {
                GpuBufferSlice fog = RenderSystem.getShaderFog();

//                if (handleTranslucent != null)
//                {
//                    handleTranslucent.get().copyDepthFrom(handleMain.get());
//                }

                for (IRenderer renderer : this.worldLastRenderers)
                {
                    profiler.push(renderer.getProfilerSectionSupplier());
                    renderer.onRenderWorldLast(
//                            handleTranslucent != null ? handleTranslucent.get() : handleMain.get(),
                            handleMain.get(),
                            modelViewMatrix, cameraState, cullFrustum, buffers, terrainFog, fogColor, profiler);
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

            profiler.pop();
        }
    }

    @ApiStatus.Internal
    @ApiStatus.Experimental
    public void onRegisterSpecialGuiRenderer(GuiRenderer guiRenderer, Minecraft mc, ImmutableMap.Builder<@NotNull Class<? extends PictureInPictureRenderState>, @NotNull PictureInPictureRenderer<?>> builder)
    {
//        MaLiLib.LOGGER.warn("onRegisterSpecialGuiRenderer():");

        if (this.specialGuiRenderers.isEmpty() == false)
        {
            for (IRenderer renderer : this.specialGuiRenderers)
            {
                MaLiLib.LOGGER.warn("onRegisterSpecialGuiRenderer(): render for [{}]", renderer.getClass().getName());
                renderer.onRegisterSpecialGuiRenderer(guiRenderer, mc, builder);
            }
        }
    }
}
