package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractHorseEntity.class)
public interface IMixinAbstractHorseEntity
{
    @Accessor("items")
    SimpleInventory malilib_getHorseInventory();
}