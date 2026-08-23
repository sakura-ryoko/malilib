package fi.dy.masa.malilib.mixin.item;

import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface IMixinItem
{
	@Accessor("registryEntry")
	RegistryEntry.Reference<Item> malilib_getBuiltInRegistryHolder();
}
