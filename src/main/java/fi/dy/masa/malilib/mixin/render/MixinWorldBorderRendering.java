package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.render.WorldBorderRendering;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldBorderRendering.class)
public class MixinWorldBorderRendering
{
    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onPostWorldBorder(WorldBorder border, Vec3d cameraPos, double viewDistanceBlocks, double farPlaneDistance, CallbackInfo ci)
    {
    }
}
