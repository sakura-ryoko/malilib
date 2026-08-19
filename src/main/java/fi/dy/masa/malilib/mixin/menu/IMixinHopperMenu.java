package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.HopperMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperMenu.class)
public interface IMixinHopperMenu
{
	@Accessor("hopper")
	Container malilib_getHopper();
}
