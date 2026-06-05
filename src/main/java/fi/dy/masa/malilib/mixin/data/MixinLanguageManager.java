package fi.dy.masa.malilib.mixin.data;

import net.minecraft.client.resources.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.config.ConfigManager;

@Mixin(LanguageManager.class)
public class MixinLanguageManager
{
	@Inject(method = "setSelected", at = @At("RETURN"))
	private void malilib_onSetLanguage(String code, CallbackInfo ci)
	{
		((ConfigManager) ConfigManager.getInstance()).onVanillaSetLanguage(code);
	}
}
