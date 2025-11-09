package fi.dy.masa.malilib.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer
{
    // This injection draws on the same layer as all the other debug rendering, during the Main Phase; at the proper rendering order.
    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onDebugRender(PoseStack matrices, Frustum frustum,
                                       MultiBufferSource.BufferSource vertexConsumers, double cameraX, double cameraY,
                                       double cameraZ, boolean bl, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPostDebug(matrices, frustum, vertexConsumers, new Vec3(cameraX, cameraY, cameraZ));
    }
}
