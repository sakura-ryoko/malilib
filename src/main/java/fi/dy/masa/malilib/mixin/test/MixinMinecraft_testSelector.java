package fi.dy.masa.malilib.mixin.test;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.test.config.ConfigTestEnum;
import fi.dy.masa.malilib.test.misc.TestSelector;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft_testSelector
{
	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void malilib_onLeftClickMouse(CallbackInfoReturnable<Boolean> cir)
	{
		if (MaLiLibReference.DEBUG_MODE &&
			MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
			ConfigTestEnum.TEST_SELECTOR_HOTKEY.getBooleanValue())
		{
			TestSelector.INSTANCE.select(false);
			cir.cancel();
			return;
		}
	}

	@Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
	private void malilib_onRightClickMouse(CallbackInfo ci)
	{
		if (MaLiLibReference.DEBUG_MODE &&
			MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
			ConfigTestEnum.TEST_SELECTOR_HOTKEY.getBooleanValue())
		{
			TestSelector.INSTANCE.select(true);
			ci.cancel();
			return;
		}
	}
}
