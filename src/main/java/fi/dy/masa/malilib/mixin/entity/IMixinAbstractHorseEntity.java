package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractHorse.class)
public interface IMixinAbstractHorseEntity
{
    @Accessor("inventory")
    SimpleContainer malilib_getHorseInventory();
}