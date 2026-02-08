package fi.dy.masa.malilib.mixin.entity;

import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse extends Entity
{
    @Shadow protected SimpleContainer inventory;

    public MixinAbstractHorse(EntityType<?> type, Level world)
    {
        super(type, world);
    }

    @Inject(
            method = "createInventory",
            at = @At("RETURN")
    )
    private void malilib$onNewInventory(CallbackInfo ci)
    {
        ((IEntityOwnedInventory) this.inventory).malilib$setEntityOwner(this);
    }
}
