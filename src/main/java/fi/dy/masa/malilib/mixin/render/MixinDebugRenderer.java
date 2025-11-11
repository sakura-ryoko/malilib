package fi.dy.masa.malilib.mixin.render;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer
{
    // This injection draws on the same layer as all the other debug rendering, during the Main Phase; at the proper rendering order.
    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onDebugRender(MatrixStack matrices, Frustum frustum,
                                       VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY,
                                       double cameraZ, boolean bl, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPostDebug(matrices, frustum, vertexConsumers, new Vec3d(cameraX, cameraY, cameraZ));
    }
}
