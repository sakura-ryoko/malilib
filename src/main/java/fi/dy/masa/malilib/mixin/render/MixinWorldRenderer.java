package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
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
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
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
	private void malilib_onRenderWorldPreWeather(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                             boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                             GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                             ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci,
	                                             @Local(name = "profiler") ProfilerFiller profiler,
	                                             @Local(name = "cullFrustum") Frustum frustum,
	                                             @Local(name = "frame") FrameGraphBuilder frameGraphBuilder)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(modelViewMatrix, projectionMatrix, this.minecraft, frameGraphBuilder, this.targets, frustum, camera, this.renderBuffers, profiler);
	}

	@Inject(method = "renderLevel",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
	        ))
	private void malilib_onRenderWorldLast(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker,
	                                       boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix,
	                                       GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky,
	                                       ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci,
	                                       @Local(name = "profiler") ProfilerFiller profiler,
	                                       @Local(name = "cullFrustum") Frustum frustum,
	                                       @Local(name = "frame") FrameGraphBuilder frameGraphBuilder)
	{
		((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(modelViewMatrix, projectionMatrix, this.minecraft, frameGraphBuilder, this.targets, frustum, camera, this.renderBuffers, profiler);
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
