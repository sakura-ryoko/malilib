package fi.dy.masa.malilib.mixin;

import net.minecraft.client.render.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = DebugRenderer.class)
public class MixinDebugRenderer
{
    /*
    @Inject(method = "method_62351", at = @At("HEAD"))
    private void malilib_onDebugRenderDraw(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, double d, double e, double f, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPost(d, e, f);
    }
     */
}
