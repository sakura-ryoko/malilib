package fi.dy.masa.malilib.mixin.inventoryOwner;

import fi.dy.masa.malilib.util.IEntityOwnedInventory;
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
public abstract class MixinHorseEntity extends Entity
{
    @Shadow protected SimpleInventory items;

    public MixinHorseEntity(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(
            method = "onChestedStatusChanged",
            at = @At("RETURN")
    )
    private void onNewInventory(CallbackInfo ci)
    {
        ((IEntityOwnedInventory) items).malilib$setEntityOwner(this);
    }
}
