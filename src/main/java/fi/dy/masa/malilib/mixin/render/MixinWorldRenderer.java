package fi.dy.masa.malilib.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.profiler.Profiler;

@Mixin(value = WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private DefaultFramebufferSet framebufferSet;
    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
                     shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldPreWeather(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
												 Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix,
												 GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci,
												 @Local Profiler profiler,
												 @Local Frustum frustum,
												 @Local FrameGraphBuilder frameGraphBuilder)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(matrix4f, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, this.bufferBuilders, profiler);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldLast(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
										   Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix,
										   GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci,
										   @Local Profiler profiler,
										   @Local Frustum frustum,
										   @Local FrameGraphBuilder frameGraphBuilder)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(matrix4f, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, this.bufferBuilders, profiler);
    }
}
