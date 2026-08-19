package fi.dy.masa.malilib.mixin.item;

import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CompoundContainer.class)
public interface IMixinCompoundContainer
{
	@Accessor("container1")
	Container malilib_getContainer1();

	@Accessor("container2")
	Container malilib_getContainer2();
}
