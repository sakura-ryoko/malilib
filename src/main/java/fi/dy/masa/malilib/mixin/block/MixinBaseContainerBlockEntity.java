package fi.dy.masa.malilib.mixin.block;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(BaseContainerBlockEntity.class)
public abstract class MixinBaseContainerBlockEntity extends BlockEntity
{
	public MixinBaseContainerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState)
	{
		super(type, worldPosition, blockState);
	}

	@Inject(method = "createMenu(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/AbstractContainerMenu;",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/world/level/block/entity/BaseContainerBlockEntity;createMenu(ILnet/minecraft/world/entity/player/Inventory;)Lnet/minecraft/world/inventory/AbstractContainerMenu;"))
	private void malilib_onOpenContainer1(int containerId, Inventory inventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> cir)
	{
		if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
		{
			Minecraft.getInstance().execute(() -> Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuOpened(containerId, this.getBlockPos(), this));
		}
	}
}
