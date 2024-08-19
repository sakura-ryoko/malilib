package fi.dy.masa.malilib.mixin;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

/**
 * This is required as a new hook into the WorldRendering, as a work-around for the new Runnable methods used.
 * This is called after the renderMain(), which culls translucent layers out of the DebugRenderer.
 * I tried hooking there also, but it was culling the Overlays.
 * The Camera information is just an optional bonus data point.
 */
@Mixin(ParticleManager.class)
public class MixinParticleManager
{
    @Inject(method = "renderParticles", at = @At("TAIL"))
    private void onRenderParticles(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci)
    {
        Vec3d vec3d = camera.getPos();
        double x = vec3d.getX();
        double y = vec3d.getY();
        double z = vec3d.getZ();

        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderWorldPost(x, y, z);
    }
}
