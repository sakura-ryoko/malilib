package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Vector4f;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = LevelRenderer.class, priority = 800)
public abstract class MixinLevelRenderer
{
	@Shadow @Final private LevelTargetBundle targets;
	@Shadow @Final private RenderBuffers renderBuffers;
	@Shadow @Final private GameRenderer gameRenderer;

	@Unique private boolean cancelAlwaysOnTop;

	// Effected by Improved Transparency
	@Inject(method = "render",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addMainPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Z)V",
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onRenderWorldPreMain(GraphicsResourceAllocator resourceAllocator, boolean renderOutline,
	                                          CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor,
	                                          boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci,
	                                          @Local(name = "profiler") ProfilerFiller profiler,
	                                          @Local(name = "frame") FrameGraphBuilder frame,
	                                          @Local(name = "featureFrame") FeatureRenderDispatcher.PreparedFrame featureFrame)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreMain(Minecraft.getInstance(), (LevelRenderer) (Object) this,
		                                                                              frame, featureFrame, this.targets,
		                                                                              this.gameRenderer.mainCamera().getCullFrustum(), cameraState,
		                                                                              this.renderBuffers, consistentDepthRequired,
		                                                                              terrainFog, fogColor, profiler);

		this.cancelAlwaysOnTop = ((RenderEventHandler) RenderEventHandler.getInstance()).shouldCancelAlwaysOnTop((LevelRenderer) (Object) this);
	}

	// 'executeAlwaysOnTop' clears the Depth Texture --> Breaks "onWorldLast"
	@Inject(method = "executeAlwaysOnTop", at = @At("HEAD"), cancellable = true)
	private void malilib_cancelAlwaysOnTop(CallbackInfo ci)
	{
		if (this.cancelAlwaysOnTop && ((RenderEventHandler) RenderEventHandler.getInstance()).shouldCancelAlwaysOnTop())
		{
			ci.cancel();
		}
	}

	@Inject(method = "render",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
	                 ordinal = 3,
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator resourceAllocator, boolean renderOutline,
	                                       CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor,
	                                       boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci,
	                                       @Local(name = "profiler") ProfilerFiller profiler,
	                                       @Local(name = "frame") FrameGraphBuilder frame,
	                                       @Local(name = "featureFrame") FeatureRenderDispatcher.PreparedFrame featureFrame)
	{
		this.cancelAlwaysOnTop = false;
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(Minecraft.getInstance(), (LevelRenderer) (Object) this,
		                                                                           frame, featureFrame, this.targets,
		                                                                           this.gameRenderer.mainCamera().getCullFrustum(), cameraState,
		                                                                           this.renderBuffers, consistentDepthRequired,
		                                                                           terrainFog, fogColor, profiler);
	}
}
