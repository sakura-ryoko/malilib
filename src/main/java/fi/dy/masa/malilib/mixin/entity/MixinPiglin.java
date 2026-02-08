package fi.dy.masa.malilib.mixin.entity;

import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Piglin.class)
public abstract class MixinPiglin extends Entity
{
    @Shadow @Final private SimpleContainer inventory;

    public MixinPiglin(EntityType<?> type, Level world)
    {
        super(type, world);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void malilib$onNewInventory(EntityType<?> entityType, Level world, CallbackInfo ci)
    {
        ((IEntityOwnedInventory) this.inventory).malilib$setEntityOwner(this);
    }
}
