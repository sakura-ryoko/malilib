package fi.dy.masa.malilib.mixin.test;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibReference;

@Mixin(SharedConstants.class)
public class MixinSharedConstants
{
    @Shadow public static boolean isDevelopment;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void malilib_sharedConstants(CallbackInfo ci)
    {
        isDevelopment = MaLiLibReference.DEBUG_MODE;
    }
}
