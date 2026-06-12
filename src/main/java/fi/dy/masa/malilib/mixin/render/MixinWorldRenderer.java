package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.util.IWorldRenderer;
import org.joml.Matrix4f;
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
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = LevelRenderer.class, priority = 990)
public abstract class MixinWorldRenderer implements IWorldRenderer
{
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private LevelTargetBundle targets;
	@Shadow @Final private RenderBuffers renderBuffers;

	@Shadow
	public abstract void tick(Camera camera);

	@Unique private @Nullable DeltaTracker tracker;

	@Inject(method = "renderLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
	        ))
	private void malilib_onRenderWorldPreWeather(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera,
	                                             Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix,
	                                             GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci,
	                                             @Local ProfilerFiller profiler,
	                                             @Local Frustum frustum,
	                                             @Local FrameGraphBuilder frameGraphBuilder)
	{
		this.tracker = tickCounter;
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(matrix4f, projectionMatrix, this.minecraft, frameGraphBuilder, this.targets, frustum, camera, this.renderBuffers, profiler);
	}

	// Inject before lateDebug
	@Inject(method = "renderLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addLateDebugPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/state/CameraRenderState;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4f;)V",
	                 shift = At.Shift.BEFORE
	        ))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera,
	                                       Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix,
	                                       GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci,
	                                       @Local ProfilerFiller profiler,
	                                       @Local Frustum frustum,
	                                       @Local FrameGraphBuilder frameGraphBuilder)
	{
		this.tracker = tickCounter;
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(matrix4f, projectionMatrix, this.minecraft, frameGraphBuilder, this.targets, frustum, camera, this.renderBuffers, profiler);
	}

	// Compat for OnDemandRenderer
	@Override
	public @Nullable DeltaTracker malilib_getDeltaTracker()
	{
		return this.tracker;
	}

//	@Inject(method = "allChanged", at = @At("HEAD"))
//	private void malilib_verifyRenderTransparencyFix(CallbackInfo ci)
//	{
//		if (MaLiLibConfigs.Generic.RENDER_TRANSPARENCY_FIX.getBooleanValue() &&
//			this.minecraft.options.improvedTransparency().get())
//		{
//			this.minecraft.options.improvedTransparency().set(false);
//		}
//	}
}
