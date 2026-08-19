package fi.dy.masa.malilib.util.data_syncer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.item.IMixinCompoundContainer;
import fi.dy.masa.malilib.mixin.menu.*;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.DataBlockUtils;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;
import fi.dy.masa.malilib.util.nbt.NbtInventory;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

public class EntityDataRegistry
{
	private final List<EntityDataCache> entityDataCaches;
	private final HashMap<BlockPos, Pair<Integer, BlockEntity>> lastOpenedContainers;

	public EntityDataRegistry()
	{
		this.entityDataCaches = new ArrayList<>();
		this.lastOpenedContainers = new HashMap<>();
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

	public void onContainerMenuOpened(int containerId, BlockPos pos, BlockEntity te)
	{
		if (te == null) { return; }
		this.lastOpenedContainers.put(pos, Pair.of(containerId, te));
	}

	public void onContainerMenuClosed(AbstractContainerMenu menu)
	{
		Level level = Minecraft.getInstance().level;

		if (level == null)
		{
			this.lastOpenedContainers.clear();
			return;
		}

		NonNullList<Slot> list = NonNullList.create();

		for (Slot slot : menu.slots)
		{
			if (slot.hasItem() && !slot.isFake())
			{
				list.add(slot);
			}
		}

		if (list.isEmpty())
		{
			this.lastOpenedContainers.clear();
			return;
		}

		final int containerId = menu.containerId;
		Container inv = list.getFirst().container;

		if (!inv.isEmpty())
		{
			Container tempInv = null;
			BlockEntity tempTE = null;

			for (BlockPos key : this.lastOpenedContainers.keySet())
			{
				Pair<Integer, BlockEntity> pair = this.lastOpenedContainers.get(key);

				if (pair != null && pair.getLeft() == containerId && pair.getRight() instanceof Container cc)
				{
					if (cc.getContainerSize() == inv.getContainerSize())
					{
						ItemStack stackA = cc.getItem(0);
						ItemStack stackB = inv.getItem(0);

						if (InventoryUtils.areStacksAndNbtEqual(stackA, stackB))
						{
							this.addTEToCache(level, pair.getRight(), inv);
						}
					}
					else
					{
						if (tempInv == null)
						{
							tempInv = cc;
							tempTE = pair.getRight();
						}
						else if (tempTE != null)
						{
							// Combined Inv
							CompoundContainer combined = new CompoundContainer(tempInv, cc);

							if (combined.getContainerSize() == inv.getContainerSize())
							{
								ItemStack stackA = cc.getItem(0);
								ItemStack stackB = inv.getItem(0);

								if (InventoryUtils.areStacksAndNbtEqual(stackA, stackB))
								{
									this.addTEToCache(level, tempTE, tempInv);
									this.addTEToCache(level, pair.getRight(), cc);
								}
							}
						}
					}
				}
			}
		}

		this.lastOpenedContainers.clear();
	}

	private void addTEToCache(Level level, BlockEntity te, Container slotInv)
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

	public record ScanResult(EntityDataEntry entry, long timeout)
	{
	}
}
