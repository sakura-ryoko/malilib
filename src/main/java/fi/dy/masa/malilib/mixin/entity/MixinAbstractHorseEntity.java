package fi.dy.masa.malilib.mixin.entity;

import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntity.class)
public abstract class MixinAbstractHorseEntity extends Entity
{
    @Shadow protected SimpleInventory items;

    public MixinAbstractHorseEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(
            method = "onChestedStatusChanged",
            at = @At("RETURN")
    )
    private void malilib$onNewInventory(CallbackInfo ci)
    {
        ((IEntityOwnedInventory) this.items).malilib$setEntityOwner(this);
    }
}
