package fi.dy.masa.malilib.mixin.menu;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceScreenHandler.class)
public interface IMixinAbstractFurnaceScreenHandler
{
	@Accessor("inventory")
	Inventory malilib_getContainer();
}
