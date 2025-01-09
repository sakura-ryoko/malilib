package fi.dy.masa.malilib.mixin;

import net.minecraft.item.AnimalArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimalArmorItem.class)
public interface IMixinAnimalArmorItem
{
	@Accessor("type")
	AnimalArmorItem.Type malilib_getAnimalArmorType();
}
