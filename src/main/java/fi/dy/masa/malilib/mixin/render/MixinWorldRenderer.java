package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;

@Mixin(value = LevelRenderer.class, priority = 990)
public abstract class MixinWorldRenderer
{
	@Shadow @Final private Minecraft minecraft;
	@Shadow @Final private LevelTargetBundle targets;
	@Shadow @Final private RenderBuffers renderBuffers;

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
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(matrix4f, projectionMatrix, this.minecraft, frameGraphBuilder, this.targets, frustum, camera, this.renderBuffers, profiler);
	}

	@Inject(method = "renderLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
	        ))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera,
	                                       Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix,
	                                       GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci,
	                                       @Local ProfilerFiller profiler,
	                                       @Local Frustum frustum,
	                                       @Local FrameGraphBuilder frameGraphBuilder)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(matrix4f, projectionMatrix, this.minecraft, frameGraphBuilder, this.targets, frustum, camera, this.renderBuffers, profiler);
	}

	@Inject(method = "allChanged", at = @At("HEAD"))
	private void malilib_verifyRenderTransparencyFix(CallbackInfo ci)
	{
		if (MaLiLibConfigs.Generic.RENDER_TRANSPARENCY_FIX.getBooleanValue() &&
				this.minecraft.options.improvedTransparency().get())
		{
			this.minecraft.options.improvedTransparency().set(false);
		}
	}
}
