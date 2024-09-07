package fi.dy.masa.malilib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
                    target = "Lnet/minecraft/client/render/FrameGraphBuilder;createPass(Ljava/lang/String;)Lnet/minecraft/client/render/RenderPass;"))
    private void malilib_onRenderWorldPre(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                          Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                          Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPre(positionMatrix, projectionMatrix, this.client);
    }

    /*
    @Inject(method = "renderLayer", at = @At("TAIL"))
    private void malilib_onRenderWorldPost(RenderLayer renderLayer, double x, double y, double z, Matrix4f viewMatrix, Matrix4f positionMatrix, CallbackInfo ci)
    {
        if (renderLayer.equals(RenderLayer.getTripwire()))
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPost(x, y, z);
        }
    }
     */

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/Fog;)V",
                    shift = At.Shift.BEFORE))
    private void malilib_onRenderWorldPost(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                          Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                          Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                          @Local FrameGraphBuilder frameGraphBuilder,
                                          @Local Frustum frustum)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldCreatePass(frameGraphBuilder);
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldRunPass(this.framebufferSet, frustum, camera);
    }

    @Inject(method = "render",
            at = @At(value = "TAIL"))
    private void malilib_onRenderWorldEnd(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldEnd();
    }
}
