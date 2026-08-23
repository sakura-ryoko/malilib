package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandScreenHandler.class)
public interface IMixinBrewingStandScreenHandler
{
	@Accessor("inventory")
	Inventory malilib_getBrewingStand();
}
