package fi.dy.masa.malilib.mixin.test;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.test.ConfigTestEnum;
import fi.dy.masa.malilib.test.TestSelector;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient_testSelector
{
	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void malilib_onLeftClickMouse(CallbackInfoReturnable<Boolean> cir)
	{
		if (MaLiLibReference.DEBUG_MODE &&
			MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
			ConfigTestEnum.TEST_WALLS_HOTKEY.getBooleanValue())
		{
			TestSelector.INSTANCE.select(false);
			cir.cancel();
			return;
		}
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void malilib_onRightClickMouse(CallbackInfo ci)
	{
		if (MaLiLibReference.DEBUG_MODE &&
			MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
			ConfigTestEnum.TEST_WALLS_HOTKEY.getBooleanValue())
		{
			TestSelector.INSTANCE.select(true);
			ci.cancel();
			return;
		}
	}
}
