package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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
	@Shadow @Final private GameRenderer gameRenderer;

	// Effected by Improved Transparency
	@Inject(method = "render",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onRenderWorldPreWeather(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                             boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                             GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                             CallbackInfo ci,
	                                             @Local(name = "profiler") ProfilerFiller profiler,
	                                             @Local(name = "frame") FrameGraphBuilder frame)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(modelViewMatrix, Minecraft.getInstance(), frame, this.targets, this.gameRenderer.mainCamera().getCullFrustum(), cameraState, this.renderBuffers, terrainFog, fogColor, profiler);
	}

	// 'addLateDebugPass' clears the Depth Texture
	@Inject(method = "render",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addAlwaysOnTopPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                       boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                       GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                       CallbackInfo ci,
	                                       @Local(name = "profiler") ProfilerFiller profiler,
	                                       @Local(name = "frame") FrameGraphBuilder frame)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(modelViewMatrix, Minecraft.getInstance(), frame, this.targets, this.gameRenderer.mainCamera().getCullFrustum(), cameraState, this.renderBuffers, terrainFog, fogColor, profiler);
	}
}
