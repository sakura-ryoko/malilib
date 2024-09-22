package fi.dy.masa.malilib.util;

import net.minecraft.entity.Entity;

import fi.dy.masa.malilib.sync.data.SyncEquipment;

public interface IEntityOwnedInventory
{
    Entity malilib$getEntityOwner();
    void malilib$setEntityOwner(Entity entity);

    <T extends SyncEquipment> T malilib$getSyncOwner();
    <T extends SyncEquipment> void malilib$setSyncOwner(T entity);
}
