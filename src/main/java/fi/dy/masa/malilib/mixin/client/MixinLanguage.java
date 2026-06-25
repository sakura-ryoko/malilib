package fi.dy.masa.malilib.mixin.client;

import java.util.Map;
import java.util.Set;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import fi.dy.masa.malilib.config.ConfigManager;

/**
 * This Mixin fixes all %f and %d String.format() params for
 * any mod with a registered Config Handler; for the i18n translations
 */
@Mixin(value = Language.class, priority = 900)
public class MixinLanguage
{
	@ModifyArgs(
			method = "loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"
			)
	)
	private static void malilib_onLoadCustomText(Args args, @Local(name = "entry") Map.Entry<String, JsonElement> entry)
	{
		final String id = args.get(0);
		final boolean fix = malilib$checkModIds(id);

		if (fix && entry.getValue() instanceof JsonPrimitive obj)
		{
			args.set(1, obj.getAsString());
		}
	}

	@Unique
	private static boolean malilib$checkModIds(String id)
	{
		Set<String> modIdSet = ((ConfigManager) ConfigManager.getInstance()).modIdSet();

		for (String modId : modIdSet)
		{
			if (id.startsWith(modId+"."))
			{
				return true;
			}
		}

		return false;
	}
}
