package fi.dy.masa.malilib.mixin.render;

import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.compat.lwgl.GpuCompat;

@Mixin(GlDevice.class)
public class MixinGlDevice
{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void malilib_onGlBackendInit(long contextId, int debugVerbosity, boolean sync, ShaderSource defaultShaderSourceGetter, boolean renderDebugLabels, CallbackInfo ci)
    {
        GpuCompat.init((GpuDevice) this);
    }
}
