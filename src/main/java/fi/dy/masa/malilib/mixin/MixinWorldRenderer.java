package fi.dy.masa.malilib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.profiler.Profiler;
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
    //@Unique private PostEffectProcessor postEffects = null;
    //@Unique private int width;
    //@Unique private int height;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Vec3d;FLnet/minecraft/client/render/Fog;)V",
                     shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldPreWeather(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean bl,
                                                 Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                                 Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                                 @Local Profiler profiler, @Local Frustum frustum,
                                                 @Local FrameGraphBuilder frameGraphBuilder,
                                                 @Local(ordinal = 0) int i, @Local(ordinal = 1) int j,
                                                 @Local PostEffectProcessor postEffectProcessor)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPreWeather(positionMatrix, projectionMatrix, this.client, frameGraphBuilder, this.framebufferSet, frustum, camera, profiler);

        /*
        if (postEffectProcessor != null)
        {
            this.width = i;
            this.height = j;
            this.postEffects = postEffectProcessor;
            this.postEffects.render(frameGraphBuilder, this.width, this.height, this.framebufferSet);
        }
         */
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

        /*
        if (this.postEffects != null)
        {
            this.postEffects.render(frameGraphBuilder, this.width, this.height, this.framebufferSet);
        }
         */
    }
}
