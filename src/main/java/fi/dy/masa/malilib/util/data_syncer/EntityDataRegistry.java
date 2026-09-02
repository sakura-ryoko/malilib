package fi.dy.masa.malilib.util.data_syncer;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.data.DataBlockUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;
import fi.dy.masa.malilib.util.nbt.NbtInventory;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.nbt.NbtView;

public class EntityDataRegistry
{
	private final List<EntityDataCache> entityDataCaches;
	private final EntityDataChestTracker chestTracker;

	public EntityDataRegistry()
	{
		this.entityDataCaches = new ArrayList<>();
		this.chestTracker = new EntityDataChestTracker();
	}

	public void registerEntityDataCache(EntityDataCache cache)
	{
		this.entityDataCaches.add(cache);
	}

	public int size()
	{
		return this.entityDataCaches.size();
	}

	public boolean isEmpty()
	{
		return this.entityDataCaches.isEmpty();
	}

	public CompoundData scanForBlockEntityData(BlockPos pos, List<String> ignoredIds)
	{
		List<ScanResult> list = new ArrayList<>();

		this.entityDataCaches.forEach((entry) ->
		                              {
										  if (!ignoredIds.contains(entry.getId()))
										  {
											  EntityDataEntry tryData = entry.getBlockEntityDataEntryFromCache(pos);

											  if (tryData != null)
											  {
												  list.add(new ScanResult(tryData, entry.getTimeout()));
											  }
										  }
		                              });

		if (list.isEmpty())
		{
			return new CompoundData();
		}

		list.sort((a, b) ->
		          {
			          long timeDelta = Math.abs(a.entry().time() - b.entry().time());
			          long timeoutDelta = Math.max(a.timeout(), b.timeout());

			          if (timeDelta <= timeoutDelta)
			          {
				          int sizeCompare = Integer.compare(b.entry().data().size(), a.entry().data().size());

				          if (sizeCompare != 0)
				          {
					          return sizeCompare;
				          }
			          }

			          return Long.compare(b.entry().time(), a.entry().time());
		          });

		return list.getFirst().entry().data();
	}

	public CompoundData scanForEntityData(int entityId, List<String> ignoredIds)
	{
		List<ScanResult> list = new ArrayList<>();

		this.entityDataCaches.forEach((entry) ->
		                              {
			                              if (!ignoredIds.contains(entry.getId()))
			                              {
				                              EntityDataEntry tryData = entry.getEntityDataEntryFromCache(entityId);

				                              if (tryData != null)
				                              {
					                              list.add(new ScanResult(tryData, entry.getTimeout()));
				                              }
			                              }
		                              });

		if (list.isEmpty())
		{
			return new CompoundData();
		}

		list.sort((a, b) ->
		          {
			          long timeDelta = Math.abs(a.entry().time() - b.entry().time());
			          long timeoutDelta = Math.max(a.timeout(), b.timeout());

			          if (timeDelta <= timeoutDelta)
			          {
				          int sizeCompare = Integer.compare(b.entry().data().size(), a.entry().data().size());

				          if (sizeCompare != 0)
				          {
					          return sizeCompare;
				          }
			          }

			          return Long.compare(b.entry().time(), a.entry().time());
		          });

		return list.getFirst().entry().data();
	}

	public EntityDataChestTracker chestTracker()
	{
		return this.chestTracker;
	}

	public void register()
	{
		this.chestTracker().onRegister();
	}

	public void reset()
	{
		this.chestTracker().onReset();
	}

	protected void addTEToCache(Level level, BlockEntity te, Container slotInv, boolean loadNbt)
	{
		if (!MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue()) { return; }

		if (te != null && slotInv != null)      // Empty can be a valid inventory
		{
			NbtInventory nbtInv = NbtInventory.fromInventory(slotInv);
			BlockPos pos = te.getBlockPos();
			BlockState state = te.getBlockState();
			this.entityDataCaches.forEach(e -> e.removeFromCache(pos));

			// De-Duplicate Double Chests / Barrels when on a Server
			if (slotInv.getContainerSize() > NbtInventory.DEFAULT_SIZE && !Minecraft.getInstance().hasSingleplayerServer())
			{
				Pair<BlockPos, BlockState> barrelAdj = InventoryUtils.getCarpetTISLargeBarrel(level, pos, state);
				BlockPos posAdj = null;
				BlockState stateAdj = null;
				boolean isLeftSide = false;

				if (barrelAdj != null)
				{
					posAdj = barrelAdj.getLeft();
					stateAdj = barrelAdj.getRight();
					final BlockPos finalPos = posAdj;
					this.entityDataCaches.forEach(e -> e.removeFromCache(finalPos));
					// Just recycling "ChestType" here.  Negative Axis Direction == First Side.
					ChestType type = state.getValue(BarrelBlock.FACING).getAxisDirection() == net.minecraft.core.Direction.AxisDirection.NEGATIVE ? ChestType.RIGHT : ChestType.LEFT;

					if (type == ChestType.RIGHT)
					{
						isLeftSide = true;
					}
				}
				else if (state.hasProperty(BlockStateProperties.CHEST_TYPE) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
				{
					ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);

					if (type != ChestType.SINGLE)
					{
						Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
						Direction offsetDir = type == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();

						posAdj = pos.relative(offsetDir);
						stateAdj = level.getBlockState(posAdj);
						final BlockPos finalPos = posAdj;
						this.entityDataCaches.forEach(e -> e.removeFromCache(finalPos));

						if (type == ChestType.RIGHT)
						{
							isLeftSide = true;
						}
					}
				}

				if (posAdj != null && stateAdj != null)
				{
					try
					{
						Pair<Container, Container> invPair = nbtInv.splitInventory();
						Container inv1 = isLeftSide ? invPair.getLeft() : invPair.getRight();
						Container inv2 = isLeftSide ? invPair.getRight() : invPair.getLeft();
						CompoundData data1 = DataBlockUtils.setBlockEntityType(te.getType(), null);
						CompoundData data2 = DataBlockUtils.setBlockEntityType(te.getType(), null);
						final BlockPos pos1 = pos;
						final BlockPos pos2 = posAdj;

						DataTypeUtils.writeBlockPos(pos1, data1);
						DataTypeUtils.writeBlockPos(pos2, data2);

						NbtInventory invLeft = NbtInventory.fromInventory(inv1);
						NbtInventory invRight = NbtInventory.fromInventory(inv2);

						data1.put(NbtKeys.ITEMS, invLeft.toDataList(level.registryAccess()));
						data2.put(NbtKeys.ITEMS, invRight.toDataList(level.registryAccess()));

						if (MaLiLibReference.DEBUG_MODE)
						{
							MaLiLib.LOGGER.warn("addTEToCache: pos1: [{}] -> {}", pos1.toShortString(), data1.toString());
							MaLiLib.LOGGER.warn("addTEToCache: pos2: [{}] -> {}", pos2.toShortString(), data2.toString());
						}

						if (loadNbt)
						{
							BlockEntity te1 = te.getType().create(pos1, state);
							BlockEntity te2 = te.getType().create(pos2, stateAdj);
							NbtView view1 = NbtView.getReader(data1, level.registryAccess());
							NbtView view2 = NbtView.getReader(data2, level.registryAccess());

							te1.loadWithComponents(view1.getReader());
							te2.loadWithComponents(view2.getReader());

							this.entityDataCaches.forEach(e -> e.addToCache(pos1, te1, data1));
							this.entityDataCaches.forEach(e -> e.addToCache(pos2, te2, data2));
						}
						else
						{
							this.entityDataCaches.forEach(e -> e.addToCache(pos1, te, data1));
							this.entityDataCaches.forEach(e -> e.addToCache(pos2, te, data2));
						}
					}
					catch (Exception e)
					{
						MaLiLib.LOGGER.warn("addTEToCache: Exception: {}", e.getLocalizedMessage());
					}
				}
			}
			else
			{
				CompoundData data = DataBlockUtils.setBlockEntityType(te.getType(), null);
				final BlockPos finalPos = pos;

				DataTypeUtils.writeBlockPos(pos, data);
				data.put(NbtKeys.ITEMS, nbtInv.toDataList(level.registryAccess()));

				if (MaLiLibReference.DEBUG_MODE)
				{
					MaLiLib.LOGGER.warn("addTEToCache: pos: [{}] -> {}", pos.toShortString(), data.toString());
				}

				if (loadNbt)
				{
					BlockEntity te1 = te.getType().create(pos, state);
					NbtView view = NbtView.getReader(data, level.registryAccess());

					te1.loadWithComponents(view.getReader());
					this.entityDataCaches.forEach(e -> e.addToCache(finalPos, te1, data));
				}
				else
				{
					this.entityDataCaches.forEach(e -> e.addToCache(finalPos, te, data));
				}
			}
		}
	}

	public record ScanResult(EntityDataEntry entry, long timeout)
	{
	}
}
