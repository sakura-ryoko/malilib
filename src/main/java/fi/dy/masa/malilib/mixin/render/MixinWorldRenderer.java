package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = LevelRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private LevelTargetBundle targets;
    @Shadow @Final private RenderBuffers renderBuffers;

//    @Inject(method = "render",
//            at = @At(value = "INVOKE",
//                     target = "Lnet/minecraft/client/render/WorldRenderer;renderParticles(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/render/Fog;)V",
//                     shift = At.Shift.BEFORE))
//    private void malilib_onRenderWorldPreParticles(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline,
//                                                 Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci,
//                                                 @Local ProfilerFiller profiler,
//                                                 @Local Frustum frustum,
//                                                 @Local FrameGraphBuilder frameGraphBuilder)
//    //@Local(ordinal = 0) int i, @Local(ordinal = 1) int j, @Local PostEffectProcessor postEffectProcessor)
//    {
//        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreParticles(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, this.bufferBuilders, profiler);
//    }

    @Inject(method = "renderLevel",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/LevelRenderer;addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/world/phys/Vec3;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
                     shift = At.Shift.BEFORE))
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
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;addLateDebugPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/world/phys/Vec3;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/culling/Frustum;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldLast(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera,
										   Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix,
										   GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci,
										   @Local ProfilerFiller profiler,
										   @Local Frustum frustum,
										   @Local FrameGraphBuilder frameGraphBuilder)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(matrix4f, projectionMatrix, this.minecraft, frameGraphBuilder, this.targets, frustum, camera, this.renderBuffers, profiler);
    }
}
