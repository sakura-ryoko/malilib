package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
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
    @Shadow @Nullable private PostEffectProcessor transparencyPostProcessor;
    //@Shadow @Final private SimpleFramebuffer framebufferSet;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/FrameGraphBuilder;createStageNode(Ljava/lang/String;)Lnet/minecraft/class_9916;"))
    private void malilib_onRenderWorldPre(ObjectAllocator objectAllocator, RenderTickCounter tickCounter, boolean bl,
                                          Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager,
                                          Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPre(positionMatrix, projectionMatrix, this.client, this.transparencyPostProcessor != null);
    }
}
