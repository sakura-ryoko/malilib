package fi.dy.masa.malilib.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.registry.Registry;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlock_AbstractBlockState extends State<Block, BlockState>
{
	protected MixinAbstractBlock_AbstractBlockState(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> codec)
	{
		super(owner, propertyMap, codec);
	}

	@WrapOperation(method = "onUse",
	               at = @At(value = "INVOKE",
	                 target = "Lnet/minecraft/block/Block;onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"
	        )
	)
	private ActionResult malilib_onUseWithoutItem(Block instance, BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, BlockHitResult blockHitResult, Operation<ActionResult> original)
	{
		if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
		{
			MinecraftClient.getInstance().execute(() -> Registry.ENTITY_DATA_REGISTRY.chestTracker().setLastInteractPos(blockPos));
		}

		return original.call(instance, blockState, world, blockPos, playerEntity, blockHitResult);
	}
}
