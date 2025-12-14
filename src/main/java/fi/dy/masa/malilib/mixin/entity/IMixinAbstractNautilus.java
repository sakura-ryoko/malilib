package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractNautilus.class)
public interface IMixinAbstractNautilus
{
	@Accessor("inventory")
	SimpleContainer malilib_getNautilusInventory();
}
