package fi.dy.masa.malilib.mixin;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(value = ParticleManager.class)
public class MixinParticleManager
{
    @Inject(method = "renderParticles", at = @At("HEAD"))
    private void malilib_onParticleRenderDraw(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPost(camera.getPos().getX(), camera.getPos().getY(), camera.getPos().getZ());
    }
}
