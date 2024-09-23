package fi.dy.masa.malilib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fi.dy.masa.malilib.util.IEntityOwnedInventory;

@Mixin(SimpleInventory.class)
public abstract class MixinSimpleInventory implements IEntityOwnedInventory, Inventory
{
    @Unique Entity entityOwner;

    @Override
    public Entity malilib$getEntityOwner()
    {
        return entityOwner;
    }

    @Override
    public void malilib$setEntityOwner(Entity entityOwner)
    {
        this.entityOwner = entityOwner;
    }
}
