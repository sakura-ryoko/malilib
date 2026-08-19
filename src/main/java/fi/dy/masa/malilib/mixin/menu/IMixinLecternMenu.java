package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.LecternMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LecternMenu.class)
public interface IMixinLecternMenu
{
	@Accessor("lectern")
	Container malilib_getLectern();
}
