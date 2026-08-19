package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerBoxMenu.class)
public interface IMixinShulkerBoxMenu
{
	@Accessor("container")
	Container malilib_getContainer();
}
