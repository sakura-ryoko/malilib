package fi.dy.masa.malilib.util.data_syncer;

import java.util.HashMap;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.WorldUtils;

public class EntityDataChestTracker
{
	private final HashMap<BlockPos, Pair<Integer, BlockEntity>> lastOpenedContainers;
	private BlockPos lastInteractPos;

	public EntityDataChestTracker()
	{
		this.lastOpenedContainers = new HashMap<>();
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
//		MaLiLib.LOGGER.warn("onContainerMenuOpened: id: {}, pos: [{}], te: [{}]", containerId, pos.toShortString(), BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(te.getType()));
		this.lastOpenedContainers.put(pos, Pair.of(containerId, te));
	}

	public void onContainerMenuClosed(AbstractContainerMenu menu)
	{
		Minecraft mc = Minecraft.getInstance();
		Level level = WorldUtils.getBestWorld(mc);

		if (level == null)
		{
			this.lastOpenedContainers.clear();
			this.lastInteractPos = null;
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
			this.lastInteractPos = null;
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
						if (mc.hasSingleplayerServer())
						{
							ItemStack stackA = cc.getItem(0);
							ItemStack stackB = inv.getItem(0);

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
					else if (inv.getContainerSize() > cc.getContainerSize() && !mc.hasSingleplayerServer())
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
							CompoundContainer combined = new CompoundContainer(tempInv, cc);

							if (combined.getContainerSize() == inv.getContainerSize())
							{
								ItemStack stackA = cc.getItem(0);
								ItemStack stackB = inv.getItem(0);

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
