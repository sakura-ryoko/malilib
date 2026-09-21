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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = LevelRenderer.class)
public abstract class MixinLevelRenderer_runRenderWorldLast
{
	@Shadow @Final private LevelTargetBundle targets;
	@Shadow @Final private RenderBuffers renderBuffers;
	@Shadow @Final private GameRenderer gameRenderer;

//	@Inject(method = "render",
//	        at = @At(value = "INVOKE",
//	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
//	                 ordinal = 3,
//	                 shift = At.Shift.BEFORE
//	        ))
//	private void malilib_onRenderWorldLast(GraphicsResourceAllocator resourceAllocator, boolean renderOutline,
//	                                       CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor,
//	                                       boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci,
//	                                       @Local(name = "profiler") ProfilerFiller profiler,
//	                                       @Local(name = "frame") FrameGraphBuilder frame,
//	                                       @Local(name = "featureFrame") FeatureRenderDispatcher.PreparedFrame featureFrame)
//	{
////		if (!IrisCompat.isShadowPassActive())
////		{
//			((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(Minecraft.getInstance(), (LevelRenderer) (Object) this,
//			                                                                           frame, featureFrame, this.targets,
//			                                                                           this.gameRenderer.mainCamera().getCullFrustum(), cameraState,
//			                                                                           this.renderBuffers, consistentDepthRequired,
//			                                                                           terrainFog, fogColor, profiler);
////		}
//	}

	@Inject(method = "render", at = @At(value = "RETURN"))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator resourceAllocator, boolean renderOutline,
	                                       CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor,
	                                       boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci,
	                                       @Local(name = "profiler") ProfilerFiller profiler,
	                                       @Local(name = "frame") FrameGraphBuilder frame,
	                                       @Local(name = "featureFrame") FeatureRenderDispatcher.PreparedFrame featureFrame)
	{
//		if (!IrisCompat.isShadowPassActive())
//		{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(Minecraft.getInstance(), (LevelRenderer) (Object) this,
		                                                                           frame, featureFrame, this.targets,
		                                                                           this.gameRenderer.mainCamera().getCullFrustum(), cameraState,
		                                                                           this.renderBuffers, consistentDepthRequired,
		                                                                           terrainFog, fogColor, profiler);
//		}
	}
}
