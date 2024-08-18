package fi.dy.masa.malilib.mixin;

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

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Vec3d;FLnet/minecraft/client/render/Fog;)V"))
    private void onRenderWorldLastNormal(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                         Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                         Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(positionMatrix, projectionMatrix, this.client);
    }

    // Was used for switching between regular and 'Fabulous' graphics, but we no longer need a second hook.
    /*
    @Inject(method = "render",
            slice = @Slice(from = @At(value = "FIELD", ordinal = 1, // start from the endDrawing() call
                    target = "Lnet/minecraft/client/render/RenderPhase;WEATHER_TARGET:Lnet/minecraft/client/render/RenderPhase$Target;"),
                    to = @At(value = "INVOKE", ordinal = 1, // end at the second renderWeather call
                            target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V"))
    private void onRenderWorldLastFabulous(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                           Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                           Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(positionMatrix, projectionMatrix, this.client);
    }
     */
}
