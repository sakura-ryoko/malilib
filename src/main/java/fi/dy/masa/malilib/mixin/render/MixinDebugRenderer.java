package fi.dy.masa.malilib.mixin.render;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer
{
//    // This injection draws on the same layer as all the other debug rendering, during the Main Phase; at the proper rendering order.
//    @Inject(method = "render", at = @At("TAIL"))
//    private void malilib_onDebugRender(Frustum frustum, double cameraX, double cameraY, double cameraZ, float tickProgress, CallbackInfo ci)
//    {
//        ((RenderEventHandler) RenderEventHandler.getInstance()).runRenderWorldPostDebug(frustum, new Vec3d(cameraX, cameraY, cameraZ), tickProgress);
//    }
}
