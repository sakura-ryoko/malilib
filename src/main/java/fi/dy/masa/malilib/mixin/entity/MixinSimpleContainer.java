package fi.dy.masa.malilib.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;

@Mixin(SimpleContainer.class)
public abstract class MixinSimpleContainer implements IEntityOwnedInventory, Container
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
