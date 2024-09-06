package fi.dy.masa.malilib.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DebugRenderer.class)
public class MixinDebugRenderer
{
    // Doesn't work on Fabulous! Graphics
    @Inject(method = "render", at = @At("HEAD"))
    private void malilib_onDebugRenderDraw(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci)
    {
        //((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPost(cameraX, cameraY, cameraZ);
    }
}