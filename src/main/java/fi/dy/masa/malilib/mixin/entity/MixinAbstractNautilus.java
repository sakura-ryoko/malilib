package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.util.game.IEntityOwnedInventory;

@Mixin(AbstractNautilus.class)
public abstract class MixinAbstractNautilus extends Entity
{
	@Shadow protected SimpleContainer inventory;

	public MixinAbstractNautilus(EntityType<?> entityType, Level level)
	{
		super(entityType, level);
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
