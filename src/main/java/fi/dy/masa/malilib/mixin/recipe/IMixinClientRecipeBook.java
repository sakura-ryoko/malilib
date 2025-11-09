package fi.dy.masa.malilib.mixin.recipe;

import java.util.Map;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientRecipeBook.class)
public interface IMixinClientRecipeBook
{
    @Accessor("known")
    Map<RecipeDisplayId, RecipeDisplayEntry> malilib_getRecipeMap();
}
