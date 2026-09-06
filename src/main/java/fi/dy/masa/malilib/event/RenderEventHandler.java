package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
    private final List<IRenderer> worldLastRenderers = new ArrayList<>();
    private boolean shouldCancelAlwaysOnTop;

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
    public void registerWorldLastRenderer(IRenderer renderer)
    {
        if (this.worldLastRenderers.contains(renderer) == false)
        {
            this.worldLastRenderers.add(renderer);
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
    public boolean shouldCancelAlwaysOnTop()
    {
        this.shouldCancelAlwaysOnTop = !this.worldLastRenderers.isEmpty();
        return this.shouldCancelAlwaysOnTop;
    }

    @ApiStatus.Internal
    private void runAlwaysOnTop(FeatureRenderDispatcher.PreparedFrame featureFrame,
                                LevelTargetBundle targets,
                                RenderTarget mainTarget, boolean consistentDepthRequired)
    {
        // See LevelRenderer.executeAlwaysOnTop()
        if (this.shouldCancelAlwaysOnTop)   // ConfigTestEnum.TEST_ENUM_CONFIG.getBooleanValue()
        {
            GpuTextureView depthTextureView = consistentDepthRequired ? targets.alwaysOnTopDepth.get().getDepthTextureView() : mainTarget.getDepthTextureView();

            try (RenderPass renderPass = RenderSystem.getDevice()
                                                     .createCommandEncoder()
                                                     .createRenderPass(() -> "Always on top features", mainTarget.getColorTextureView(), Optional.empty(), depthTextureView, OptionalDouble.of(0.0))) {
                RenderSystem.bindDefaultUniforms(renderPass);
                featureFrame.executeAlwaysOnTop(renderPass);
            }

            if (consistentDepthRequired)
            {
                RenderPassDescriptor integrateDepthDescriptor = RenderPassDescriptor.builder(() -> "Integrate always on top depth")
                                                                                    .withDepthAttachment(mainTarget.getDepthTextureView())
                                                                                    .build();
                GpuSampler nearestSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);

                try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(integrateDepthDescriptor))
                {
                    renderPass.setUniform("InSampler", depthTextureView, nearestSampler);
                    renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.INTEGRATE_DEPTH));
                    renderPass.draw(3, 1, 0, 0);
                }
            }
        }

        this.shouldCancelAlwaysOnTop = false;
    }

    @ApiStatus.Internal
    public void runRenderWorldLast(Minecraft mc, LevelRenderer vanilla,
                                   FrameGraphBuilder frameGraphBuilder, FeatureRenderDispatcher.PreparedFrame preparedFrame,
                                   LevelTargetBundle targets,
                                   Frustum cullFrustum, CameraRenderState cameraState,
                                   RenderBuffers buffers, boolean consistentDepthRequired,
                                   GpuBufferSlice terrainFog, Vector4f fogColor,
                                   ProfilerFiller profiler)
    {
        if (this.worldLastRenderers.isEmpty() == false)
        {
            profiler.push(MaLiLibReference.MOD_ID + "_render_world_last");
            FramePass pass = frameGraphBuilder.addPass(MaLiLibReference.MOD_ID + "_world_last");

            targets.main = pass.readsAndWrites(targets.main);

            ResourceHandle<@NotNull RenderTarget> handleMain = targets.main;

            pass.executes(() ->
                          {
                              GpuBufferSlice fog = RenderSystem.getShaderFog();

                              for (IRenderer renderer : this.worldLastRenderers)
                              {
                                  profiler.push(renderer.getProfilerSectionSupplier());
                                  renderer.onRenderWorldLast(
                                          handleMain.get(),
                                          cameraState, cullFrustum, buffers, terrainFog, fogColor, profiler);
                                  profiler.pop();
                              }

                              if (fog != null)
                              {
                                  RenderSystem.setShaderFog(fog);
                              }
                          });

            if (!this.worldLastRenderers.isEmpty())
            {
                pass.disableCulling();
            }

            profiler.pop();
        }
    }

    @ApiStatus.Internal
    public void runRenderWorldAlwaysOnTop(Minecraft mc, LevelRenderer vanilla,
                                          FrameGraphBuilder frameGraphBuilder, FeatureRenderDispatcher.PreparedFrame preparedFrame,
                                          LevelTargetBundle targets,
                                          Frustum cullFrustum, CameraRenderState cameraState,
                                          RenderBuffers buffers, boolean consistentDepthRequired,
                                          GpuBufferSlice terrainFog, Vector4f fogColor,
                                          ProfilerFiller profiler)
    {
        boolean hasAlwaysOnTopGizmos = vanilla.frameHasAlwaysOnTopGizmos();

        if (hasAlwaysOnTopGizmos)
        {
            profiler.push(MaLiLibReference.MOD_ID + "_render_gizmos_on_top");
            FramePass pass = frameGraphBuilder.addPass(MaLiLibReference.MOD_ID + "_gizmos_on_top");

            targets.main = pass.readsAndWrites(targets.main);

            if (consistentDepthRequired)
            {
                targets.alwaysOnTopDepth = pass.readsAndWrites(targets.alwaysOnTopDepth);
            }

            ResourceHandle<@NotNull RenderTarget> handleMain = targets.main;

            pass.executes(() ->
                          {
                              GpuBufferSlice fog = RenderSystem.getShaderFog();

                              if (fog != null)
                              {
                                  RenderSystem.setShaderFog(fog);
                              }

                              // Hack Fix for "Always On Top Gizmos" to render after we do;
                              // because it breaks our Pass's Depth Texture.
                              this.runAlwaysOnTop(preparedFrame, targets, handleMain.get(), consistentDepthRequired);
                          });

            profiler.pop();
        }
    }
}
