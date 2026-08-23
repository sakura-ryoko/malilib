package fi.dy.masa.malilib.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(LockableContainerBlockEntity.class)
public abstract class MixinLockableContainerBlockEntity extends BlockEntity
{
	public MixinLockableContainerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState)
	{
		super(type, worldPosition, blockState);
	}

	@Inject(method = "createMenu(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/screen/ScreenHandler;",
	        at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/block/entity/LockableContainerBlockEntity;createScreenHandler(ILnet/minecraft/entity/player/PlayerInventory;)Lnet/minecraft/screen/ScreenHandler;"
	        )
	)
	private void malilib_onOpenContainer1(int containerId, PlayerInventory playerInventory, PlayerEntity playerEntity, CallbackInfoReturnable<ScreenHandler> cir)
	{
		if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
		{
			MinecraftClient.getInstance().execute(() -> Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuOpened(containerId, this.getPos(), this));
		}
	}
}
