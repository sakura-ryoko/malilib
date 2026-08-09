package fi.dy.masa.malilib.mixin.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface IMixinItem
{
	@Accessor("builtInRegistryHolder")
	Holder.Reference<Item> malilib_getBuiltInRegistryHolder();
}
