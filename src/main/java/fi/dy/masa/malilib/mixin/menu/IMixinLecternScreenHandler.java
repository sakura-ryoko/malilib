package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.LecternScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LecternScreenHandler.class)
public interface IMixinLecternScreenHandler
{
	@Accessor("inventory")
	Inventory malilib_getLectern();
}
