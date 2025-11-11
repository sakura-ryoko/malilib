package fi.dy.masa.malilib.mixin.recipe;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IMixinIngredient
{
    @Accessor("entries")
    RegistryEntryList<Item> malilib_getEntries();
}
