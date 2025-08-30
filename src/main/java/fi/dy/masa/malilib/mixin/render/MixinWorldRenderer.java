package fi.dy.masa.malilib.mixin.render;

import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render",
            at = @At(value = "INVOKE", ordinal = 1,
                     target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V"))
    private void onRenderWorldLastNormal(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(matrix4f, matrix4f2, this.client);
    }

    @Inject(method = "render",
            slice = @Slice(from = @At(value = "FIELD", ordinal = 1, // start from the endDrawing() call
                                      target = "Lnet/minecraft/client/render/RenderPhase;WEATHER_TARGET:Lnet/minecraft/client/render/RenderPhase$Target;", opcode = Opcodes.GETSTATIC),
                            to = @At(value = "INVOKE", ordinal = 1, // end at the second renderWeather call
                                     target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V"))
    private void onRenderWorldLastFabulous(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldLast(matrix4f, matrix4f2, this.client);
    }

    @Inject(method = "reload()V", at = @At("HEAD"))
    private void malilib_verifyRenderTransparencyFix(CallbackInfo ci)
    {
        if (MaLiLibConfigs.Generic.RENDER_TRANSPARENCY_FIX.getBooleanValue() &&
            MinecraftClient.isFabulousGraphicsOrBetter())
        {
            this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
        }
    }
}
