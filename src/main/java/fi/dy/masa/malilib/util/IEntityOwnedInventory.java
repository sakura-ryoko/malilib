package fi.dy.masa.malilib.util;

import net.minecraft.entity.Entity;

import fi.dy.masa.malilib.sync.fe.FakeEntity;

public interface IEntityOwnedInventory
{
    Entity malilib$getEntityOwner();
    void malilib$setEntityOwner(Entity entity);

    FakeEntity malilib$getFakeEntityOwner();
    void malilib$setFakeEntityOwner(FakeEntity entity);
}
