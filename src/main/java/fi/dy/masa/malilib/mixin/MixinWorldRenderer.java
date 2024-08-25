package fi.dy.masa.malilib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/FrameGraphBuilder;createStageNode(Ljava/lang/String;)Lnet/minecraft/class_9916;"))
    private void malilib_onRenderWorldPre(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                          Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                          Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
                                          @Local FrameGraphBuilder frameGraphBuilder,
                                          @Local PostEffectProcessor postEffectProcessor)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPre(positionMatrix, projectionMatrix, this.client, postEffectProcessor != null);
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
}
