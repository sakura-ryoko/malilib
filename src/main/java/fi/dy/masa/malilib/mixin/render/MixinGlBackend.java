package fi.dy.masa.malilib.mixin.render;

import java.util.function.BiFunction;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.compat.lwgl.GpuCompat;

@Mixin(GlBackend.class)
public class MixinGlBackend
{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void malilib_onGlBackendInit(long contextId, int debugVerbosity, boolean sync,
                                         BiFunction<Identifier, ShaderType, String> shaderSourceGetter, boolean renderDebugLabels,
                                         CallbackInfo ci)
    {
        GpuCompat.init((GpuDevice) this);
    }
}
