package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Generic3x3ContainerScreenHandler.class)
public interface IMixinGeneric3x3ContainerScreenHandler
{
	@Accessor("inventory")
	Inventory malilib_getDispenser();
}
