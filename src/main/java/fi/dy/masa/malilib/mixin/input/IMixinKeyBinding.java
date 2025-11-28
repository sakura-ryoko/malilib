package fi.dy.masa.malilib.mixin.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface IMixinKeyBinding
{
	@Accessor("defaultKey")
	InputConstants.Key malilib$getDefaultKey();

	@Accessor("key")
	InputConstants.Key malilib$getBoundKey();

	@Accessor("category")
	KeyMapping.Category malilib$getCategory();
}
