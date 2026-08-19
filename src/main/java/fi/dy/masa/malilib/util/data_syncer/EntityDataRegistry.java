package fi.dy.masa.malilib.util.data_syncer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.util.data.DataBlockUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;
import fi.dy.masa.malilib.util.nbt.NbtInventory;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

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

	protected void addTEToCache(Level level, BlockEntity te, Container slotInv)
	{
		if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
		{
			if (te != null && slotInv != null && !slotInv.isEmpty())
			{
				NbtInventory nbtInv = NbtInventory.fromInventory(slotInv);

				if (!nbtInv.isEmpty())
				{
					CompoundData data = DataBlockUtils.setBlockEntityType(te.getType(), null);
					BlockPos pos = te.getBlockPos();

					DataTypeUtils.writeBlockPos(pos, data);
					data.put(NbtKeys.ITEMS, nbtInv.toDataList(level.registryAccess()));

					if (MaLiLibReference.DEBUG_MODE)
					{
						MaLiLib.LOGGER.warn("addTEToCache: pos: [{}] -> {}", pos.toShortString(), data.toString());
					}

					this.entityDataCaches.forEach(entry -> entry.addToCache(te.getBlockPos(), te, data));
				}
			}
		}
	}

	public record ScanResult(EntityDataEntry entry, long timeout)
	{
	}
}
