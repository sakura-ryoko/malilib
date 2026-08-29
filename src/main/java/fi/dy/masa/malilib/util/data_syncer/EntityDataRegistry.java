package fi.dy.masa.malilib.util.data_syncer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.util.data.DataBlockUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;

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

	public record ScanResult(EntityDataEntry entry, long timeout)
	{
	}
}
