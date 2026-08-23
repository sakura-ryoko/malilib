package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HopperScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperScreenHandler.class)
public interface IMixinHopperScreenHandler
{
	@Accessor("inventory")
	Inventory malilib_getHopper();
}
