package fi.dy.masa.malilib.util.data_syncer;

import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.nbt.NbtInventory;

public class EntityDataChestTracker
{
	private final HashMap<BlockPos, Pair<Integer, BlockEntity>> lastOpenedContainers;
	private BlockPos lastInteractPos;
	private NbtInventory enderCache;

	public EntityDataChestTracker()
	{
		this.lastOpenedContainers = new HashMap<>();
		this.enderCache = null;
	}

	public void setLastInteractPos(BlockPos pos)
	{
		this.lastInteractPos = pos;
	}

	public @Nullable BlockPos getLastInteractPos()
	{
		return this.lastInteractPos;
	}

	public void onContainerMenuOpened(int containerId, BlockPos pos, BlockEntity te)
	{
		if (te == null) { return; }
		if (MaLiLibReference.DEBUG_MODE)
		{
			MaLiLib.LOGGER.warn("onContainerMenuOpened: id: {}, pos: [{}], te: [{}]", containerId, pos.toShortString(), Registries.BLOCK_ENTITY_TYPE.getId(te.getType()));
		}
		this.lastOpenedContainers.put(pos, Pair.of(containerId, te));
	}

	public @Nullable NbtInventory getEnderCache()
	{
		return this.enderCache;
	}

	public void onCloseEnderChest(@Nonnull Inventory inv)
	{
		if (this.enderCache != null)
		{
			try
			{
				this.enderCache.close();
				this.enderCache = null;
			}
			catch (Exception ignored) {}
		}

		if (MaLiLibReference.DEBUG_MODE)
		{
			MaLiLib.LOGGER.warn("onCloseEnderChest(): size: [{}]", inv.size());
		}

		this.enderCache = NbtInventory.fromInventory(inv);
	}

	public void onContainerMenuClosed(ScreenHandler menu)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		World level = WorldUtils.getBestWorld(mc);

		if (level == null)
		{
			this.lastOpenedContainers.clear();
			this.lastInteractPos = null;
			return;
		}

		DefaultedList<Slot> list = DefaultedList.of();

		for (Slot slot : menu.slots)
		{
			if (slot.hasStack() && !slot.disablesDynamicDisplay())
			{
				list.add(slot);
			}
		}

		if (list.isEmpty())
		{
			this.lastOpenedContainers.clear();
			this.lastInteractPos = null;
			return;
		}

		final int containerId = menu.syncId;
		Inventory inv = list.getFirst().inventory;

		if (!inv.isEmpty())
		{
			Inventory tempInv = null;
			BlockEntity tempTE = null;

			for (BlockPos key : this.lastOpenedContainers.keySet())
			{
				Pair<Integer, BlockEntity> pair = this.lastOpenedContainers.get(key);

				if (pair != null && pair.getLeft() == containerId && pair.getRight() instanceof EnderChestBlockEntity)
				{
					this.onCloseEnderChest(inv);
				}
				else if (pair != null && pair.getLeft() == containerId && pair.getRight() instanceof Inventory cc)
				{
					if (cc.size() == inv.size())
					{
						if (mc.isIntegratedServerRunning())
						{
							ItemStack stackA = cc.getStack(0);
							ItemStack stackB = inv.getStack(0);

							if (InventoryUtils.areStacksAndNbtEqual(stackA, stackB))
							{
								Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), inv);
							}
						}
						else
						{
							Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), inv);     // On a Server; we cannot verify the slots
						}
					}
					else if (inv.size() > cc.size() && !mc.isIntegratedServerRunning())
					{
						Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), inv);        // On a Server; we cannot verify the slots
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
							DoubleInventory combined = new DoubleInventory(tempInv, cc);

							if (combined.size() == inv.size())
							{
								ItemStack stackA = cc.getStack(0);
								ItemStack stackB = inv.getStack(0);

								if (InventoryUtils.areStacksAndNbtEqual(stackA, stackB))
								{
									Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, tempTE, tempInv);
									Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), cc);
								}
							}
						}
					}
				}
			}
		}

		this.lastOpenedContainers.clear();
		this.lastInteractPos = null;
	}
}
