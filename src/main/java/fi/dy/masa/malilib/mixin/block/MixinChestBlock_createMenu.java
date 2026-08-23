package fi.dy.masa.malilib.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(targets = "net.minecraft.block.ChestBlock$2$1", priority = 1001)
public class MixinChestBlock_createMenu
{
	@Shadow @Final ChestBlockEntity field_17358;    // val$first
	@Shadow @Final ChestBlockEntity field_17359;    // val$second

	@WrapOperation(
			method = "createMenu(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/screen/ScreenHandler;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/GenericContainerScreenHandler;createGeneric9x6(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)Lnet/minecraft/screen/GenericContainerScreenHandler;"
			)
	)
	private GenericContainerScreenHandler malilib_onOpenMenu3Post(int containerId, PlayerInventory playerInventory, Inventory container, Operation<GenericContainerScreenHandler> original)
	{
		if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
		{
			MinecraftClient.getInstance()
			               .execute(() ->
			                  {
				                  if (container instanceof DoubleInventory)
				                  {
					                  Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuOpened(containerId, this.field_17358.getPos(), this.field_17358);
					                  Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuOpened(containerId, this.field_17359.getPos(), this.field_17359);
				                  }
				                  else
				                  {
					                  Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuOpened(containerId, this.field_17358.getPos(), this.field_17358);
				                  }
			                  });
		}

		return original.call(containerId, playerInventory, container);
	}
}
