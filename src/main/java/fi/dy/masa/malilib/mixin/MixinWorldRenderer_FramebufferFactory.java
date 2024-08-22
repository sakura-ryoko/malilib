package fi.dy.masa.malilib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.FramebufferHandler;
import fi.dy.masa.malilib.interfaces.IFramebufferSetRemap;

@Mixin(value = WorldRenderer.class)
public abstract class MixinWorldRenderer_FramebufferFactory
{
    @Shadow @Final private MinecraftClient client;
    //@Shadow @Nullable private PostEffectProcessor transparencyPostProcessor;
    @Shadow @Final private DefaultFramebufferSet framebufferSet;
    @Unique private SimpleFramebufferFactory factory = null;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/FrameGraphBuilder;createStageNode(Ljava/lang/String;)Lnet/minecraft/class_9916;"))
    private void malilib_onRenderWorldPre(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                          Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                          Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                          @Local FrameGraphBuilder frameGraphBuilder,
                                          @Local PostEffectProcessor postEffectProcessor)
    {
        if (this.factory != null)
        {
            ((FramebufferHandler) FramebufferHandler.getInstance()).onFramebufferTranslucentFactorySetup(frameGraphBuilder, this.factory, this.client);
        }

        ((FramebufferHandler) FramebufferHandler.getInstance()).onFramebufferSetup(
                positionMatrix, projectionMatrix,
                this.client, postEffectProcessor,
                this.framebufferSet,
                frameGraphBuilder);
    }

    @Inject(method = "reload(Lnet/minecraft/resource/ResourceManager;)V", at = @At("TAIL"))
    private void malilib_onRenderWorldReload(ResourceManager manager, CallbackInfo ci)
    {
        IFramebufferSetRemap.malilib_remapStages();
        ((FramebufferHandler) FramebufferHandler.getInstance()).onReload(this.client);
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void malilib_onRenderWorldClose(CallbackInfo ci)
    {
        ((FramebufferHandler) FramebufferHandler.getInstance()).onClose();
    }

    @Inject(method = "onResized", at = @At("TAIL"))
    private void malilib_onRenderWorldResized(int width, int height, CallbackInfo ci)
    {
        ((FramebufferHandler) FramebufferHandler.getInstance()).onResized(width, height);
    }
    @Inject(method = "renderMain",
            at = @At(value = "HEAD"))
    private void malilib_onRenderWorldMain(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera,
                                           Matrix4f matrix4f, Matrix4f matrix4f2, Fog fog, boolean bl, boolean bl2,
                                           RenderTickCounter renderTickCounter, Profiler profiler, CallbackInfo ci)
    {
        ((FramebufferHandler) FramebufferHandler.getInstance()).onRenderMainCaptureLocals(this.client, camera, fog, renderTickCounter, profiler);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/FrameGraphBuilder;method_61912(Ljava/lang/String;Lnet/minecraft/client/util/ClosableFactory;)Lnet/minecraft/client/util/Handle;",
                    ordinal = 4), index = 1)
    private <T> ClosableFactory<T> malilib_onRenderWorldTranclucentFactory(ClosableFactory<T> factory, @Local FrameGraphBuilder frameGraphBuilder)
    {
        this.factory = (SimpleFramebufferFactory) factory;
        return factory;
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/Fog;)V"))
    private void malilib_onRenderWorldNode(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                           Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                           Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                           @Local FrameGraphBuilder frameGraphBuilder)
    {
        ((FramebufferHandler) FramebufferHandler.getInstance()).onRenderNode(frameGraphBuilder, positionMatrix, projectionMatrix, this.client, camera, this.framebufferSet);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onRenderWorldFinished(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                               Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                               Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        ((FramebufferHandler) FramebufferHandler.getInstance()).onRenderFinished();
    }
}