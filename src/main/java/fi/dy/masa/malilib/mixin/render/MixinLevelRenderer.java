package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer
{
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private LevelTargetBundle targets;
	@Shadow @Final private RenderBuffers renderBuffers;

	@Inject(method = "extractLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
	                 ordinal = 5
	        ))
	private void malilib_onExtractWorldPreWeather(DeltaTracker deltaTracker, Camera camera,
	                                              float deltaPartialTick, CallbackInfo ci,
	                                              @Local(name = "profiler") ProfilerFiller profiler)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldPreWeather(deltaTracker, camera, deltaPartialTick, profiler);
	}

	@Inject(method = "extractLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
	                 ordinal = 10,
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onExtractWorldLast(DeltaTracker deltaTracker, Camera camera,
	                                        float deltaPartialTick, CallbackInfo ci,
	                                        @Local(name = "profiler") ProfilerFiller profiler)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldLast(deltaTracker, camera, deltaPartialTick, profiler);
	}

	// Effected by Improved Transparency
	@Inject(method = "renderLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
	        ))
	private void malilib_onRenderWorldPreWeather(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                             boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                             GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                             ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci,
	                                             @Local(name = "profiler") ProfilerFiller profiler,
	                                             @Local(name = "cullFrustum") Frustum cullFrustum,
	                                             @Local(name = "frame") FrameGraphBuilder frame)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(modelViewMatrix, this.minecraft, frame, this.targets, cullFrustum, cameraState, this.renderBuffers, terrainFog, fogColor, profiler);
	}

	// 'addLateDebugPass' clears the Depth Texture
	@Inject(method = "renderLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addLateDebugPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4fc;)V",
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                       boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                       GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                       ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci,
	                                       @Local(name = "profiler") ProfilerFiller profiler,
	                                       @Local(name = "cullFrustum") Frustum cullFrustum,
	                                       @Local(name = "frame") FrameGraphBuilder frame)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(modelViewMatrix, this.minecraft, frame, this.targets, cullFrustum, cameraState, this.renderBuffers, terrainFog, fogColor, profiler);
	}
}
