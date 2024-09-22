package fi.dy.masa.malilib.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fi.dy.masa.malilib.sync.data.SyncEquipment;
import fi.dy.masa.malilib.util.IEntityOwnedInventory;

@Mixin(SimpleInventory.class)
public abstract class MixinSimpleInventory implements IEntityOwnedInventory, Inventory
{
    @Unique Entity entityOwner;
    @Unique
    SyncEquipment fakeEntityOwner;

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

    @Override
    @SuppressWarnings("unchecked")
    public <T extends SyncEquipment> T malilib$getSyncOwner()
    {
        return (T) this.fakeEntityOwner;
    }

    @Override
    public <T extends SyncEquipment> void malilib$setSyncOwner(T entity)
    {
        this.fakeEntityOwner = entity;
    }
}
