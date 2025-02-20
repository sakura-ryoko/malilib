package fi.dy.masa.malilib.util.game;

import net.minecraft.entity.Entity;

public interface IEntityOwnedInventory
{
    Entity malilib$getEntityOwner();
    void malilib$setEntityOwner(Entity entity);
}
