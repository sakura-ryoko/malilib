package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.DispenserMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DispenserMenu.class)
public interface IMixinDispenserMenu
{
	@Accessor("dispenser")
	Container malilib_getDispenser();
}
