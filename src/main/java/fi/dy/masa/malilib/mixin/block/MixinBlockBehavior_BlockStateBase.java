package fi.dy.masa.malilib.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockBehavior_BlockStateBase extends StateHolder<Block, BlockState>
{
	protected MixinBlockBehavior_BlockStateBase(Block owner, Property<?>[] propertyKeys, Comparable<?>[] propertyValues)
	{
		super(owner, propertyKeys, propertyValues);
	}

	@WrapOperation(method = "useWithoutItem",
	               at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/world/level/block/Block;useWithoutItem(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
	        )
	)
	private InteractionResult malilib_onUseWithoutItem(Block instance, BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult, Operation<InteractionResult> original)
	{
		if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
		{
			Minecraft.getInstance().execute(() -> Registry.ENTITY_DATA_REGISTRY.chestTracker().setLastInteractPos(blockPos));
		}

		return original.call(instance, blockState, level, blockPos, player, blockHitResult);
	}
}
