package fi.dy.masa.malilib.mixin.input;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface IMixinKeyBinding
{
	@Accessor("defaultKey")
	InputUtil.Key malilib$getDefaultKey();

	@Accessor("boundKey")
	InputUtil.Key malilib$getBoundKey();

	@Accessor("category")
	KeyBinding.Category malilib$getCategory();
}
