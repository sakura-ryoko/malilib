package fi.dy.masa.malilib.mixin.test;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.test.ConfigTestEnum;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayInteractionManager_testSelector
{
    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void malilib_handleBreakingRestriction1(BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir)
    {
        if (MaLiLibReference.DEBUG_MODE &&
            MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
            ConfigTestEnum.TEST_SELECTOR_HOTKEY.getBooleanValue())
        {
            cir.setReturnValue(false);
        }
    }
}
