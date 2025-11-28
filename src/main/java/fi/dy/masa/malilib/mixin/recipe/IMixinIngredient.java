package fi.dy.masa.malilib.mixin.recipe;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IMixinIngredient
{
    @Accessor("values")
    HolderSet<Item> malilib_getEntries();
}
