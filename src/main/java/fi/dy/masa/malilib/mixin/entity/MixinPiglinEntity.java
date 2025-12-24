package fi.dy.masa.malilib.mixin.entity;

import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinEntity.class)
public abstract class MixinPiglinEntity extends Entity
{
    @Shadow @Final private SimpleInventory inventory;

    public MixinPiglinEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void malilib$onNewInventory(EntityType<?> entityType, World world, CallbackInfo ci)
    {
        ((IEntityOwnedInventory) this.inventory).malilib$setEntityOwner(this);
    }
}
