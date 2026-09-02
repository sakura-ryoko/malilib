package fi.dy.masa.malilib.util.data_syncer;

import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
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
			MaLiLib.LOGGER.warn("onContainerMenuOpened: id: {}, pos: [{}], te: [{}]", containerId, pos.toShortString(), BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(te.getType()));
		}
		this.lastOpenedContainers.put(pos, Pair.of(containerId, te));
	}

	public @Nullable NbtInventory getEnderCache()
	{
		return this.enderCache;
	}

	protected void onRegister()
	{
		this.onReset();
	}

	protected void onReset()
	{
		if (this.enderCache != null)
		{
			try
			{
				this.enderCache.close();
			}
			catch (Exception ignored) {}
		}

		this.enderCache = null;
	}

	public void onCloseEnderChest(@Nonnull Container inv)
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
			MaLiLib.LOGGER.warn("onCloseEnderChest(): size: [{}]", inv.getContainerSize());
		}

		this.enderCache = NbtInventory.fromInventory(inv);
	}

	public void onContainerMenuClosed(AbstractContainerMenu menu)
	{
		if (!MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue()) { return; }
		Minecraft mc = Minecraft.getInstance();
		Level level = WorldUtils.getBestWorld(mc);

		if (level == null)
		{
			this.lastOpenedContainers.clear();
			this.lastInteractPos = null;
			return;
		}

		NonNullList<ItemStack> list = InventoryUtils.getItemsFromSlots(menu.slots);

		if (list.isEmpty())
		{
			this.lastOpenedContainers.clear();
			this.lastInteractPos = null;
			return;
		}

		final int containerId = menu.containerId;
		Container inv = InventoryUtils.getAsInventory(list);

		if (inv != null)        // Empty is a valid Inventory.
		{
			Container tempInv = null;
			BlockEntity tempTE = null;

			for (BlockPos key : this.lastOpenedContainers.keySet())
			{
				Pair<Integer, BlockEntity> pair = this.lastOpenedContainers.get(key);

				if (pair != null && pair.getLeft() == containerId && pair.getRight() instanceof EnderChestBlockEntity)
				{
					this.onCloseEnderChest(inv);
				}
				else if (pair != null && pair.getLeft() == containerId && pair.getRight() instanceof Container cc)
				{
					if (cc.getContainerSize() == inv.getContainerSize())
					{
						if (mc.hasSingleplayerServer())
						{
							ItemStack stackA = cc.getItem(0);
							ItemStack stackB = inv.getItem(0);

							if (InventoryUtils.areStacksAndNbtEqual(stackA, stackB))
							{
								Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), inv, false);
							}
						}
						else
						{
							Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), inv, false);     // On a Server; we cannot verify the slots
						}
					}
					else if (inv.getContainerSize() > cc.getContainerSize() && !mc.hasSingleplayerServer())
					{
						Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), inv, false);        // On a Server; we cannot verify the slots
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
									Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, tempTE, tempInv, false);
									Registry.ENTITY_DATA_REGISTRY.addTEToCache(level, pair.getRight(), cc, false);
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
