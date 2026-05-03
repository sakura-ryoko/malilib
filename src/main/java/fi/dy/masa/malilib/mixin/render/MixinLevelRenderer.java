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
	@Shadow @Final private LevelTargetBundle targets;
	@Shadow @Final private RenderBuffers renderBuffers;

	/*
	 * 26.2 moved the old extractLevel/renderLevel flow into LevelRenderer#render(...).
	 * Closest equivalent for pre-weather extract/render hooks is immediately before addWeatherPass(...).
	 */
	@Inject(method = "render",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
	        ))
	private void malilib_onRenderWorldPreWeather(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                             boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                             GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                             CallbackInfo ci,
	                                             @Local(ordinal = 0) ProfilerFiller profiler,
	                                             @Local(ordinal = 0) FrameGraphBuilder frameGraphBuilder)
	{
		Minecraft mc = Minecraft.getInstance();
		Camera camera = mc.gameRenderer.mainCamera();
		float deltaPartialTick = deltaTracker.getGameTimeDeltaPartialTick(false);

		((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldPreWeather(deltaTracker, camera, deltaPartialTick, profiler);
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(modelViewMatrix, mc, frameGraphBuilder, this.targets, cameraState.cullFrustum, cameraState, this.renderBuffers, terrainFog, fogColor, profiler);
	}

	/*
	 * 26.2 removed addLateDebugPass(...). addAlwaysOnTopPass(...) is now the last world pass insertion
	 * before frame graph execution, so it is the closest equivalent for the old world-last extract/render hooks.
	 */
	@Inject(method = "render",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addAlwaysOnTopPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
	                 shift = At.Shift.AFTER
	        ))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                       boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                       GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                       CallbackInfo ci,
	                                       @Local(ordinal = 0) ProfilerFiller profiler,
	                                       @Local(ordinal = 0) FrameGraphBuilder frameGraphBuilder)
	{
		Minecraft mc = Minecraft.getInstance();
		Camera camera = mc.gameRenderer.mainCamera();
		float deltaPartialTick = deltaTracker.getGameTimeDeltaPartialTick(false);

		((RenderEventHandler) RenderEventHandler.getInstance()).runExtractWorldLast(deltaTracker, camera, deltaPartialTick, profiler);
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(modelViewMatrix, mc, frameGraphBuilder, this.targets, cameraState.cullFrustum, cameraState, this.renderBuffers, terrainFog, fogColor, profiler);
	}
}
