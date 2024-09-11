package fi.dy.masa.malilib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private DefaultFramebufferSet framebufferSet;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderMain(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/render/Fog;ZZLnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/util/profiler/Profiler;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldPreMain(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                              Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                              Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                              @Local FrameGraphBuilder frameGraphBuilder,
                                              @Local Frustum frustum,
                                              @Local Profiler profiler)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreMain(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, profiler);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderParticles(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/LightmapTextureManager;FLnet/minecraft/client/render/Fog;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldPreParticles(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                                   Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                                   Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                                   @Local FrameGraphBuilder frameGraphBuilder,
                                                   @Local Frustum frustum,
                                                   @Local Profiler profiler)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreParticles(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, profiler);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Vec3d;FLnet/minecraft/client/render/Fog;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldPreWeather(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                                 Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                                 Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                                 @Local FrameGraphBuilder frameGraphBuilder,
                                                 @Local Frustum frustum,
                                                 @Local Profiler profiler)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, profiler);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/Fog;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldLast(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                           Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                           Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                           @Local FrameGraphBuilder frameGraphBuilder,
                                           @Local Frustum frustum,
                                           @Local Profiler profiler)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldLast(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, profiler);
    }
}
