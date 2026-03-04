package fi.dy.masa.malilib.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.math.Fraction;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.entity.IMixinPlayerEntity;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlayType;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.DataEntityUtils;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.log.AnsiLogger;
import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;
import fi.dy.masa.malilib.util.nbt.NbtInventory;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.nbt.NbtView;

public class InventoryUtils
{
    private static final AnsiLogger LOGGER = new AnsiLogger(InventoryUtils.class);
    public static final Pattern PATTERN_ITEM_BASE = Pattern.compile("^(?<name>(?:[a-z0-9\\._-]+:)[a-z0-9\\._-]+)$");

    /**
     * @return true if the stacks are identical, including their Components
     */
    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2)
    {
        return areStacksAndNbtEqual(stack1, stack2);
    }

    /**
     * @return true if the stacks are identical, including their Components
     */
    public static boolean areStacksAndNbtEqual(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.isSameItemSameComponents(stack1, stack2);
    }

    /**
     * @return true if the stacks are identical, but ignoring their Components
     */
    public static boolean areStacksEqualIgnoreNbt(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.isSameItem(stack1, stack2);
    }

    /**
     * @return true if the stacks are identical, but ignoring the stack size,
     * and if the item is damageable, then ignoring the damage too.
     */
    public static boolean areStacksEqualIgnoreDurability(ItemStack stack1, ItemStack stack2)
    {
        ItemStack ref = stack1.copy();
        ItemStack check = stack2.copy();

        // It's a little hacky, but it works.
        ref.setCount(1);
        check.setCount(1);

        if (ref.isDamageableItem() && ref.isDamaged())
        {
            ref.setDamageValue(0);
        }
        if (check.isDamageableItem() && check.isDamaged())
        {
            check.setDamageValue(0);
        }

        return ItemStack.isSameItemSameComponents(ref, check);
    }

    /**
     * Uses new ComponentMap to compare values
     *
     * @param tag1        (ComponentMap 1)
     * @param tag2        (ComponentMap 2)
     * @param type        (DataComponentType) [OPTIONAL]
     * @param ignoredKeys (keys to ignore) [OPTIONAL]
     * @param <T>         DataComponentType extendable
     * @return (return value)
     */
    public static <T> boolean areNbtEqualIgnoreKeys(@Nonnull DataComponentMap tag1, @Nonnull DataComponentMap tag2, @Nullable DataComponentType<T> type, @Nullable Set<DataComponentType<T>> ignoredKeys)
    {
        Set<DataComponentType<?>> keys1;
        Set<DataComponentType<?>> keys2;

        keys1 = tag1.keySet();
        keys2 = tag2.keySet();

        if (ignoredKeys != null)
        {
            keys1.removeAll(ignoredKeys);
            keys2.removeAll(ignoredKeys);
        }

        if (Objects.equals(keys1, keys2) == false)
        {
            return false;
        }

        if (type == null)
        {
            for (DataComponentType<?> key : keys1)
            {
                if (Objects.equals(tag1.get(key), tag2.get(key)) == false)
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return Objects.equals(tag1.get(type), tag2.get(type));
        }
    }

    /**
     * Same as above, but still intended to compare NbtCompounds
     *
     * @param tag1        (NbtCompound tag1)
     * @param tag2        (NbtCompound tag2)
     * @param ignoredKeys (Keys to ignore) [OPTIONAL]
     * @return (result)
     */
    @Deprecated
    public static boolean areNbtEqualIgnoreKeys(@Nonnull CompoundTag tag1, @Nonnull CompoundTag tag2, @Nullable Set<String> ignoredKeys)
    {
        Set<String> keys1;
        Set<String> keys2;

        keys1 = tag1.keySet();
        keys2 = tag2.keySet();

        if (ignoredKeys != null)
        {
            keys1.removeAll(ignoredKeys);
            keys2.removeAll(ignoredKeys);
        }

        if (Objects.equals(keys1, keys2) == false)
        {
            return false;
        }

        for (String key : keys1)
        {
            if (Objects.equals(tag1.get(key), tag2.get(key)) == false)
            {
                return false;
            }
        }

        return true;
    }

	/**
	 * Same as above, but still intended to compare CompoundData
	 *
	 * @param tag1        (CompoundData tag1)
	 * @param tag2        (CompoundData tag2)
	 * @param ignoredKeys (Keys to ignore) [OPTIONAL]
	 * @return (result)
	 */
	public static boolean areDataEqualIgnoreKeys(@Nonnull CompoundData tag1, @Nonnull CompoundData tag2, @Nullable Set<String> ignoredKeys)
	{
		Set<String> keys1;
		Set<String> keys2;

		keys1 = tag1.getKeys();
		keys2 = tag2.getKeys();

		if (ignoredKeys != null)
		{
			keys1.removeAll(ignoredKeys);
			keys2.removeAll(ignoredKeys);
		}

		if (!Objects.equals(keys1, keys2))
		{
			return false;
		}

		for (String key : keys1)
		{
			Optional<BaseData> data1 = tag1.getData(key);
			Optional<BaseData> data2 = tag2.getData(key);

			if (!data1.isPresent() || !data2.isPresent())
			{
				return false;
			}
			else if (!Objects.equals(data1.get(), data2.get()))
			{
				return false;
			}
		}

		return true;
	}

	/**
     * Swaps the stack from the slot <b>slotNum</b> to the given hotbar slot <b>hotbarSlot</b>
     *
     * @param container ()
     * @param slotNum ()
     * @param hotbarSlot ()
     */
    public static void swapSlots(AbstractContainerMenu container, int slotNum, int hotbarSlot)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.gameMode.handleInventoryMouseClick(container.containerId, slotNum, hotbarSlot, ClickType.SWAP, mc.player);
    }

    /**
     * Assuming that the slot is from the ContainerPlayer container,
     * returns whether the given slot number is one of the regular inventory slots.
     * This means that the crafting slots and armor slots are not valid.
     *
     * @param slotNumber ()
     * @param allowOffhand ()
     * @return ()
     */
    public static boolean isRegularInventorySlot(int slotNumber, boolean allowOffhand)
    {
        return slotNumber > 8 && (allowOffhand || slotNumber < 45);
    }

    /**
     * Finds an empty slot in the player inventory. Armor slots are not valid for the return value of this method.
     * Whether or not the offhand slot is valid, depends on the <b>allowOffhand</b> argument.
     *
     * @param containerPlayer ()
     * @param allowOffhand ()
     * @param reverse ()
     * @return the slot number, or -1 if none were found
     */
    public static int findEmptySlotInPlayerInventory(AbstractContainerMenu containerPlayer, boolean allowOffhand, boolean reverse)
    {
        final int startSlot = reverse ? containerPlayer.slots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : containerPlayer.slots.size();
        final int increment = reverse ? -1 : 1;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = containerPlayer.slots.get(slotNum);
            ItemStack stackSlot = slot.getItem();

            // Inventory crafting, armor and offhand slots are not valid
            if (stackSlot.isEmpty() && isRegularInventorySlot(slot.index, allowOffhand))
            {
                return slot.index;
            }
        }

        return -1;
    }

    /**
     * Finds a slot with an identical item than <b>stackReference</b>, ignoring the durability
     * of damageable items. Does not allow crafting or armor slots or the offhand slot
     * in the ContainerPlayer container.
     *
     * @param container ()
     * @param stackReference ()
     * @param reverse ()
     * @return the slot number, or -1 if none were found
     */
    public static int findSlotWithItem(AbstractContainerMenu container, ItemStack stackReference, boolean reverse)
    {
        final int startSlot = reverse ? container.slots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.slots.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof InventoryMenu;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.slots.get(slotNum);

            if ((isPlayerInv == false || isRegularInventorySlot(slot.index, false)) &&
                areStacksEqualIgnoreDurability(slot.getItem(), stackReference))
            {
                return slot.index;
            }
        }

        return -1;
    }

    /**
     * Swap the given item to the player's main hand, if that item is found
     * in the player's inventory.
     *
     * @param stackReference ()
     * @param mc ()
     * @return true if an item was swapped to the main hand, false if it was already in the hand, or was not found in the inventory
     */
    public static boolean swapItemToMainHand(ItemStack stackReference, Minecraft mc)
    {
        Player player = mc.player;
        boolean isCreative = player.hasInfiniteMaterials();

        // Already holding the requested item
        if (areStacksEqualIgnoreNbt(stackReference, player.getMainHandItem()))
        {
            return false;
        }

        if (isCreative)
        {
            player.getInventory().addAndPickItem(stackReference);
            mc.gameMode.handleCreativeModeItemAdd(player.getMainHandItem(), 36 + player.getInventory().getSelectedSlot()); // sendSlotPacket
            return true;
        }
        else
        {
            int slot = findSlotWithItem(player.inventoryMenu, stackReference, true);

            if (slot != -1)
            {
                int currentHotbarSlot = player.getInventory().getSelectedSlot();
                mc.gameMode.handleInventoryMouseClick(player.inventoryMenu.containerId, slot, currentHotbarSlot, ClickType.SWAP, mc.player);
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the inventory at the given position, if any.
     * Combines chest inventories into double chest inventories when applicable.
     *
     * @param world ()
     * @param pos ()
     * @return ()
     */
    @Nullable
    public static Container getInventory(Level world, BlockPos pos)
    {
        @SuppressWarnings("deprecation")
        boolean isLoaded = world.hasChunkAt(pos);

        if (isLoaded == false)
        {
            return null;
        }

        // The method in World now checks that the caller is from the same thread...
        BlockEntity te = world.getChunkAt(pos).getBlockEntity(pos);

        if (te instanceof Container inv)
        {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof ChestBlock && te instanceof ChestBlockEntity)
            {
                ChestType type = state.getValue(ChestBlock.TYPE);

                if (type != ChestType.SINGLE)
                {
                    BlockPos posAdj = pos.relative(ChestBlock.getConnectedDirection(state));
                    @SuppressWarnings("deprecation")
                    boolean isLoadedAdj = world.hasChunkAt(posAdj);

                    if (isLoadedAdj)
                    {
                        BlockState stateAdj = world.getBlockState(posAdj);
                        // The method in World now checks that the caller is from the same thread...
                        BlockEntity te2 = world.getChunkAt(posAdj).getBlockEntity(posAdj);

                        if (stateAdj.getBlock() == state.getBlock() &&
                            te2 instanceof ChestBlockEntity &&
                            stateAdj.getValue(ChestBlock.TYPE) != ChestType.SINGLE &&
                            stateAdj.getValue(ChestBlock.FACING) == state.getValue(ChestBlock.FACING))
                        {
                            Container invRight = type == ChestType.RIGHT ? inv : (Container) te2;
                            Container invLeft = type == ChestType.RIGHT ? (Container) te2 : inv;
                            inv = new CompoundContainer(invRight, invLeft);
                        }
                    }
                }
            }

            return inv;
        }

        return null;
    }

    /**
     * Checks if the given Shulker Box (or other storage item with the
     * same NBT data structure) currently contains any items.
     *
     * @param stack ()
     * @return ()
     */
    public static boolean shulkerBoxHasItems(ItemStack stack)
    {
        ItemContainerContents container = stack.getComponents().get(DataComponents.CONTAINER);

        if (container != null)
        {
            return container.nonEmptyItems().iterator().hasNext();
        }

        return false;
    }

    /**
     * Returns item represented as NBT if the ItemStack has NBT Items present.
     *
     * @param stack ()
     * @param registry ()
     * @return ()
     */
    @Deprecated
    public static @Nullable CompoundTag stackHasNbtItems(ItemStack stack, @Nonnull RegistryAccess registry)
    {
        if (stack.isEmpty() == false)
        {
            CompoundTag nbt = toNbtOrEmpty(stack, registry);

            if (hasNbtItems(nbt))
            {
                return nbt;
            }
        }

        return null;
    }

	/**
	 * Returns item represented as Data Tag if the ItemStack has NBT Items present.
	 *
	 * @param stack ()
	 * @param registry ()
	 * @return ()
	 */
	public static @Nullable CompoundData stackHasDataItems(ItemStack stack, @Nonnull RegistryAccess registry)
	{
		if (!stack.isEmpty())
		{
			CompoundData data = toDataOrEmpty(stack, registry);

			if (hasDataItems(data))
			{
				return data;
			}
		}

		return null;
	}

	/**
     * Checks if the given NBT currently contains any items.
     *
     * @param nbt ()
     * @return ()
     */
	@Deprecated
    public static boolean hasNbtItems(CompoundTag nbt)
    {
        if (nbt.contains(NbtKeys.ITEMS))
        {
            ListTag tagList = nbt.getListOrEmpty(NbtKeys.ITEMS);
            return !tagList.isEmpty();
        }
        else if (nbt.contains(NbtKeys.INVENTORY))
        {
            ListTag tagList = nbt.getListOrEmpty(NbtKeys.INVENTORY);
            return !tagList.isEmpty();
        }
        else if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            ListTag tagList = nbt.getListOrEmpty(NbtKeys.ENDER_ITEMS);
            return !tagList.isEmpty();
        }
        else if (nbt.contains(NbtKeys.ITEM))
        {
            return true;
        }
        else if (nbt.contains(NbtKeys.ITEM_2))
        {
            return true;
        }
        else if (nbt.contains(NbtKeys.BOOK))
        {
            return true;
        }
        else return nbt.contains(NbtKeys.RECORD);
    }

	/**
	 * Checks if the given Data Tag currently contains any items.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static boolean hasDataItems(CompoundData data)
	{
		if (data.contains(NbtKeys.ITEMS, Constants.NBT.TAG_LIST))
		{
			ListData tagList = data.getList(NbtKeys.ITEMS);
			return tagList != null && !tagList.isEmpty();
		}
		else if (data.contains(NbtKeys.INVENTORY, Constants.NBT.TAG_LIST))
		{
			ListData tagList = data.getList(NbtKeys.INVENTORY);
			return tagList != null && !tagList.isEmpty();
		}
		else if (data.contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
		{
			ListData tagList = data.getList(NbtKeys.ENDER_ITEMS);
			return tagList != null && !tagList.isEmpty();
		}
		else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			return true;
		}
		else if (data.contains(NbtKeys.ITEM_2, Constants.NBT.TAG_COMPOUND))
		{
			return true;
		}
		else if (data.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
		{
			return true;
		}
		else return data.contains(NbtKeys.RECORD, Constants.NBT.TAG_COMPOUND);
	}

	/**
     * Returns the list of items currently stored in the given NBT Items[] interface.
     * Does not keep empty slots.
     *
     * @param nbt The item holding the inventory contents
     * @return ()
     */
	@Deprecated
    public static NonNullList<ItemStack> getNbtItems(@Nonnull CompoundTag nbt)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null)
        {
            return NonNullList.create();
        }

        return getNbtItems(nbt, -1, mc.level.registryAccess());
    }

    /**
     * Returns the list of items currently stored in the given NBT Items[] interface.
     * Preserves empty slots, unless the "Inventory" interface is used.
     *
     * @param nbt       The tag holding the inventory contents
     * @param slotCount the maximum number of slots, and thus also the size of the list to create
     * @param registry  the Dynamic Registry object
     * @return ()
     */
	@Deprecated
    public static NonNullList<ItemStack> getNbtItems(@Nonnull CompoundTag nbt, int slotCount, @Nonnull RegistryAccess registry)
    {
        if (slotCount > NbtInventory.MAX_SIZE)
        {
            slotCount = NbtInventory.MAX_SIZE;
        }

        // Most Common Tag --> NbtElement.LIST_TYPE ???
        if (nbt.contains(NbtKeys.ITEMS))
        {
            ListTag list = nbt.getListOrEmpty(NbtKeys.ITEMS);

            if (slotCount < 0)
            {
                // Uses slots
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            NbtInventory nbtInv = NbtInventory.fromNbtList(list, false, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return NonNullList.create();
            }

            return nbtInv.sorted().toVanillaList(slotCount);
        }
        // A few Entities use this
        else if (nbt.contains(NbtKeys.INVENTORY))
        {
	        InventoryOverlayType type = InventoryOverlay.getInventoryType(DataConverterNbt.fromVanillaCompound(nbt));
	        boolean isPlayer = type == InventoryOverlayType.PLAYER;
            ListTag list = nbt.getListOrEmpty(NbtKeys.INVENTORY);
            boolean noSlotId = list.isEmpty() ? !isPlayer : !nbtInventoryHasSlots(list);

            if (slotCount < 0)
            {
                // Doesn't use slots
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            NbtInventory nbtInv = NbtInventory.fromNbtList(list, noSlotId, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return NonNullList.create();
            }
            
            return nbtInv.sorted().toVanillaList(slotCount);
        }
        // Ender Chest
        else if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            ListTag list = nbt.getListOrEmpty(NbtKeys.ENDER_ITEMS);

            if (slotCount < 0)
            {
                // Uses slots
                slotCount = Math.max(list.size(), NbtInventory.DEFAULT_SIZE);
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            NbtInventory nbtInv = NbtInventory.fromNbtList(list, false, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return NonNullList.create();
            }

            return nbtInv.sorted().toVanillaList(slotCount);
        }
        else if (nbt.contains(NbtKeys.ITEM))
        {
            // item (DecoratedPot, ItemEntity)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM));
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }
        else if (nbt.contains(NbtKeys.ITEM_2))
        {
            // Item (ItemFrame)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM_2));
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }
        else if (nbt.contains(NbtKeys.BOOK))
        {
            // Book (Lectern)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.BOOK));
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }
        else if (nbt.contains(NbtKeys.RECORD))
        {
            // RecordItem (Jukebox)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.RECORD));
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }

        return NonNullList.create();
    }

	/**
	 * Returns the list of items currently stored in the given NBT Items[] interface.
	 * Does not keep empty slots.
	 *
	 * @param data The item holding the inventory contents
	 * @return ()
	 */
	public static NonNullList<ItemStack> getDataItems(@Nonnull CompoundData data)
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.level == null)
		{
			return NonNullList.create();
		}

		return getDataItems(data, -1, mc.level.registryAccess());
	}

	/**
	 * Returns the list of items currently stored in the given NBT Items[] interface.
	 * Preserves empty slots, unless the "Inventory" interface is used.
	 *
	 * @param data       The tag holding the inventory contents
	 * @param slotCount the maximum number of slots, and thus also the size of the list to create
	 * @param registry  the Dynamic Registry object
	 * @return ()
	 */
	public static NonNullList<ItemStack> getDataItems(@Nonnull CompoundData data, int slotCount, @Nonnull RegistryAccess registry)
	{
		if (slotCount > NbtInventory.MAX_SIZE)
		{
			slotCount = NbtInventory.MAX_SIZE;
		}

		// Most Common Tag --> NbtElement.LIST_TYPE ???
		if (data.contains(NbtKeys.ITEMS, Constants.NBT.TAG_LIST))
		{
			ListData list = data.getList(NbtKeys.ITEMS);

			if (slotCount < 0)
			{
				// Uses slots
				slotCount = list.size();
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);

			NbtInventory nbtInv = NbtInventory.fromDataList(list, false, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return NonNullList.create();
			}

			return nbtInv.sorted().toVanillaList(slotCount);
		}
		// A few Entities use this
		else if (data.contains(NbtKeys.INVENTORY, Constants.NBT.TAG_LIST))
		{
			InventoryOverlayType type = InventoryOverlay.getInventoryType(data);
			boolean isPlayer = type == InventoryOverlayType.PLAYER;

			ListData list = data.getList(NbtKeys.INVENTORY);
			boolean noSlotId = list.isEmpty() ? !isPlayer : !dataInventoryHasSlots(list);

			if (slotCount < 0)
			{
				// Doesn't use slots
				slotCount = list.size();
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);

			NbtInventory nbtInv = NbtInventory.fromDataList(list, noSlotId, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return NonNullList.create();
			}

			return nbtInv.sorted().toVanillaList(slotCount);
		}
		// Ender Chest
		else if (data.contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
		{
			ListData list = data.getList(NbtKeys.ENDER_ITEMS);

			if (slotCount < 0)
			{
				// Uses slots
				slotCount = Math.max(list.size(), NbtInventory.DEFAULT_SIZE);
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);

			NbtInventory nbtInv = NbtInventory.fromDataList(list, false, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return NonNullList.create();
			}

			return nbtInv.sorted().toVanillaList(slotCount);
		}
		else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			// item (DecoratedPot, ItemEntity)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM));
			NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}
		else if (data.contains(NbtKeys.ITEM_2, Constants.NBT.TAG_COMPOUND))
		{
			// Item (ItemFrame)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM_2));
			NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}
		else if (data.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
		{
			// Book (Lectern)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.BOOK));
			NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}
		else if (data.contains(NbtKeys.RECORD, Constants.NBT.TAG_COMPOUND))
		{
			// RecordItem (Jukebox)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.RECORD));
			NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}

		return NonNullList.create();
	}

	/**
     * Returns Inventory of items currently stored in the given NBT Items[] interface.
     * Preserves empty slots, unless the "Inventory" interface is used.
     *
     * @param nbt     The tag holding the inventory contents
     * @return ()
     */
	@Deprecated
    public static Container getNbtInventory(@Nonnull CompoundTag nbt)
    {
        if (Minecraft.getInstance().level == null)
        {
            return null;
        }

        return getNbtInventory(nbt, -1, Minecraft.getInstance().level.registryAccess());
    }

	/**
	 * Returns Inventory of items currently stored in the given NBT Items[] interface.
	 * Preserves empty slots, unless the "Inventory" interface is used.
	 *
	 * @param data     The tag holding the inventory contents
	 * @return ()
	 */
	public static Container getDataInventory(@Nonnull CompoundData data)
	{
		if (Minecraft.getInstance().level == null)
		{
			return null;
		}

		return getDataInventory(data, -1, Minecraft.getInstance().level.registryAccess());
	}

	/**
     * Returns Inventory of items currently stored in the given NBT Items[] interface.
     * Preserves empty slots, unless the "Inventory" interface is used.
     *
     * @param nbt       The tag holding the inventory contents
     * @param slotCount the maximum number of slots, and thus also the size of the list to create
     * @param registry  The Dynamic Registry object
     * @return ()
     */
	@Deprecated
    public static Container getNbtInventory(@Nonnull CompoundTag nbt, int slotCount, @Nonnull RegistryAccess registry)
    {
        if (slotCount > NbtInventory.MAX_SIZE)
        {
            slotCount = NbtInventory.MAX_SIZE;
        }

        if (nbt.contains(NbtKeys.ITEMS))
        {
            // Standard 'Items' tag for most Block Entities --
            // -- Furnace, Brewing Stand, Shulker Box, Crafter, Barrel, Chest, Dispenser, Hopper, Bookshelf, Campfire
            if (slotCount < 0)
            {
                // Uses slots
                ListTag list = nbt.getListOrEmpty(NbtKeys.ITEMS);
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

			NbtInventory nbtInv = NbtInventory.fromNbt(nbt, NbtKeys.ITEMS, false, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return null;
            }

            return nbtInv.sorted().toInventory(slotCount);
        }
        else if (nbt.contains(NbtKeys.INVENTORY))
        {
	        InventoryOverlayType type = InventoryOverlay.getInventoryType(DataConverterNbt.fromVanillaCompound(nbt));
	        boolean isPlayer = type == InventoryOverlayType.PLAYER;
            ListTag list = nbt.getListOrEmpty(NbtKeys.INVENTORY);
            boolean noSlotId = list.isEmpty() ? !isPlayer : !nbtInventoryHasSlots(list);

            // Entities use this (Piglin, Villager, a few others)
            if (slotCount < 0)
            {
                // Doesn't use slots
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            // "Inventory" tags might not include Slot ID's, but a Player will.
            NbtInventory nbtInv = NbtInventory.fromNbt(nbt, NbtKeys.INVENTORY, noSlotId, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return null;
            }

            return nbtInv.sorted().toInventory(slotCount);
        }
        else if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            // Ender Chest
            ListTag list = nbt.getListOrEmpty(NbtKeys.ENDER_ITEMS);

            if (slotCount < 0)
            {
                // Uses slots
                slotCount = Math.max(list.size(), NbtInventory.DEFAULT_SIZE);
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);
            NbtInventory nbtInv = NbtInventory.fromNbtList(list, false, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return null;
            }

            return nbtInv.sorted().toInventory(Math.max(slotCount, NbtInventory.DEFAULT_SIZE));
        }
        else if (nbt.contains(NbtKeys.ITEM))
        {
            // item (DecoratedPot, ItemEntity)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM));
            SimpleContainer inv = new SimpleContainer(1);
            inv.setItem(0, entry.copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.ITEM_2))
        {
            // Item (Item Frame)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM_2));
            SimpleContainer inv = new SimpleContainer(1);
            inv.setItem(0, entry.copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.BOOK))
        {
            // Book (Lectern)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.BOOK));
            SimpleContainer inv = new SimpleContainer(1);
            inv.setItem(0, entry.copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.RECORD))
        {
            // RecordItem (Jukebox)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.RECORD));
            SimpleContainer inv = new SimpleContainer(1);
            inv.setItem(0, entry.copy());

            return inv;
        }

        return null;
    }

	/**
	 * Returns Inventory of items currently stored in the given NBT Items[] interface.
	 * Preserves empty slots, unless the "Inventory" interface is used.
	 *
	 * @param data       The tag holding the inventory contents
	 * @param slotCount the maximum number of slots, and thus also the size of the list to create
	 * @param registry  The Dynamic Registry object
	 * @return ()
	 */
	public static Container getDataInventory(@Nonnull CompoundData data, int slotCount, @Nonnull RegistryAccess registry)
	{
		if (slotCount > NbtInventory.MAX_SIZE)
		{
			slotCount = NbtInventory.MAX_SIZE;
		}

		if (data.contains(NbtKeys.ITEMS, Constants.NBT.TAG_LIST))
		{
			// Standard 'Items' tag for most Block Entities --
			// -- Furnace, Brewing Stand, Shulker Box, Crafter, Barrel, Chest, Dispenser, Hopper, Bookshelf, Campfire
			if (slotCount < 0)
			{
				// Uses slots
				ListData list = data.getList(NbtKeys.ITEMS);
				slotCount = list.size();
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);

			NbtInventory nbtInv = NbtInventory.fromData(data, NbtKeys.ITEMS, false, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return null;
			}

			return nbtInv.sorted().toInventory(slotCount);
		}
		else if (data.contains(NbtKeys.INVENTORY, Constants.NBT.TAG_LIST))
		{
			InventoryOverlayType type = InventoryOverlay.getInventoryType(data);
			boolean isPlayer = type == InventoryOverlayType.PLAYER;
			ListData list = data.getList(NbtKeys.INVENTORY);
			boolean noSlotId = list.isEmpty() ? !isPlayer : !dataInventoryHasSlots(list);

			// Entities use this (Piglin, Villager, a few others)
			if (slotCount < 0)
			{
				// Doesn't use slots
				slotCount = list.size();
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);

			// "Inventory" tags might not include Slot ID's, but a Player will.
			NbtInventory nbtInv = NbtInventory.fromData(data, NbtKeys.INVENTORY, noSlotId, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return null;
			}

			return nbtInv.sorted().toInventory(slotCount);
		}
		else if (data.contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
		{
			// Ender Chest
			ListData list = data.getList(NbtKeys.ENDER_ITEMS);

			if (slotCount < 0)
			{
				// Uses slots
				slotCount = Math.max(list.size(), NbtInventory.DEFAULT_SIZE);
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);
			NbtInventory nbtInv = NbtInventory.fromDataList(list, false, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return null;
			}

			return nbtInv.sorted().toInventory(Math.max(slotCount, NbtInventory.DEFAULT_SIZE));
		}
		else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			// item (DecoratedPot, ItemEntity)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM));
			SimpleContainer inv = new SimpleContainer(1);
			inv.setItem(0, entry.copy());

			return inv;
		}
		else if (data.contains(NbtKeys.ITEM_2, Constants.NBT.TAG_COMPOUND))
		{
			// Item (Item Frame)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM_2));
			SimpleContainer inv = new SimpleContainer(1);
			inv.setItem(0, entry.copy());

			return inv;
		}
		else if (data.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
		{
			// Book (Lectern)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.BOOK));
			SimpleContainer inv = new SimpleContainer(1);
			inv.setItem(0, entry.copy());

			return inv;
		}
		else if (data.contains(NbtKeys.RECORD, Constants.NBT.TAG_COMPOUND))
		{
			// RecordItem (Jukebox)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.RECORD));
			SimpleContainer inv = new SimpleContainer(1);
			inv.setItem(0, entry.copy());

			return inv;
		}

		return null;
	}

    private static boolean nbtInventoryHasSlots(@Nonnull ListTag list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            CompoundTag entry = list.getCompoundOrEmpty(i);
            if (entry.contains(NbtKeys.SLOT)) { return true; }
        }

        return false;
    }

    private static boolean dataInventoryHasSlots(@Nonnull ListData list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            CompoundData entry = list.getCompoundAt(i);
            if (entry.contains(NbtKeys.SLOT, Constants.NBT.TAG_BYTE)) { return true; }
        }

        return false;
    }

    /**
     * Executes the "Inventory Display Horse Fix" (Saddle Offset) for NBT-based Displays.
     *
     * @param nbt ()
     * @param slotCount ()
     * @param registry ()
     * @return ()
     */
	@Deprecated
	public static Container getNbtInventoryHorseFix(@Nonnull CompoundTag nbt, int slotCount, @Nonnull RegistryAccess registry)
    {
        NonNullList<ItemStack> horseEquipment = NbtEntityUtils.getHorseEquipmentFromNbt(nbt, registry);

        if (slotCount > NbtInventory.MAX_SIZE)
        {
            slotCount = NbtInventory.MAX_SIZE;
        }

        // Shift inv ahead by 1 slot for horses (1.21 only)
        if (nbt.contains(NbtKeys.ITEMS))
        {
            if (slotCount < 0)
            {
                ListTag list = nbt.getListOrEmpty(NbtKeys.ITEMS);
                slotCount = list.size();
            }

            SimpleContainer inv = new SimpleContainer(slotCount + 1);

            inv.setItem(0, horseEquipment.getLast());

            NbtInventory nbtInv = NbtInventory.fromNbt(nbt, NbtKeys.ITEMS, false, registry);

            // Chested Horse
            if (nbtInv != null && !nbtInv.isEmpty())
            {
                NonNullList<ItemStack> items = nbtInv.sorted().toVanillaList(slotCount + 1);

                for (int i = 0; i < slotCount; i++)
                {
                    inv.setItem(i + 1, items.get(i));
                }
            }

            return inv;
        }
        // Saddled only fix
        else if (!horseEquipment.getLast().isEmpty())
        {
            SimpleContainer inv = new SimpleContainer(1);
            inv.setItem(0, horseEquipment.getLast().copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.ITEM))
        {
            // item (DecoratedPot, ItemEntity)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM));
            SimpleContainer inv = new SimpleContainer(1);
            inv.setItem(0, entry.copy());

            return inv;
        }

        return null;
    }

	/**
	 * Executes the "Inventory Display Horse Fix" (Saddle Offset) for NBT-based Displays.
	 *
	 * @param data ()
	 * @param slotCount ()
	 * @param registry ()
	 * @return ()
	 */
	public static Container getDataInventoryHorseFix(@Nonnull CompoundData data, int slotCount, @Nonnull RegistryAccess registry)
	{
		NonNullList<ItemStack> horseEquipment = DataEntityUtils.getHorseEquipment(data, registry);

		if (slotCount > NbtInventory.MAX_SIZE)
		{
			slotCount = NbtInventory.MAX_SIZE;
		}

		// Shift inv ahead by 1 slot for horses (1.21 only)
		if (data.contains(NbtKeys.ITEMS, Constants.NBT.TAG_LIST))
		{
			if (slotCount < 0)
			{
				ListData list = data.getList(NbtKeys.ITEMS);
				slotCount = list.size();
			}

			SimpleContainer inv = new SimpleContainer(slotCount + 1);

			inv.setItem(0, horseEquipment.getLast());

			NbtInventory nbtInv = NbtInventory.fromData(data, NbtKeys.ITEMS, false, registry);

			// Chested Horse
			if (nbtInv != null && !nbtInv.isEmpty())
			{
				NonNullList<ItemStack> items = nbtInv.sorted().toVanillaList(slotCount + 1);

				for (int i = 0; i < slotCount; i++)
				{
					inv.setItem(i + 1, items.get(i));
				}
			}

			return inv;
		}
		// Saddled only fix
		else if (!horseEquipment.getLast().isEmpty())
		{
			SimpleContainer inv = new SimpleContainer(1);
			inv.setItem(0, horseEquipment.getLast().copy());

			return inv;
		}
		else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			// item (DecoratedPot, ItemEntity)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM));
			SimpleContainer inv = new SimpleContainer(1);
			inv.setItem(0, entry.copy());

			return inv;
		}

		return null;
	}

    @Nullable
    public static PlayerEnderChestContainer getPlayerEnderItems(Player player)
    {
        if (player != null)
        {
            return ((IMixinPlayerEntity) player).malilib_getEnderItems();
        }

        return null;
    }

    @Nullable
    @Deprecated
    public static PlayerEnderChestContainer getPlayerEnderItemsFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            PlayerEnderChestContainer inv = new PlayerEnderChestContainer();
            NbtView view = NbtView.getReader(nbt, registry);

            inv.fromSlots(view.getReader().listOrEmpty(NbtKeys.ENDER_ITEMS, ItemStackWithSlot.CODEC));

            return inv;
        }

        return null;
    }

	@Nullable
	public static PlayerEnderChestContainer getPlayerEnderItemsFromData(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
//		LOGGER.debug("getPlayerEnderItemsFromData: containsList({}) : containsLeinient({})", data.contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST), data.containsLenient(NbtKeys.ENDER_ITEMS));
		if (data.contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
		{
			PlayerEnderChestContainer inv = new PlayerEnderChestContainer();
			NbtView view = NbtView.getReader(data, registry);

			inv.fromSlots(view.getReader().listOrEmpty(NbtKeys.ENDER_ITEMS, ItemStackWithSlot.CODEC));

//			LOGGER.debug("getPlayerEnderItemsFromData: inv [{}]", inv.size());
			return inv;
		}

//		LOGGER.debug("getPlayerEnderItemsFromData: inv: [NULL]");
		return null;
	}

	@Deprecated
    public static NonNullList<ItemStack> getSellingItemsFromNbt(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry)
    {
        MerchantOffers offers = NbtEntityUtils.getTradeOffersFromNbt(nbt, registry);

        if (offers != null)
        {
            return getSellingItems(offers);
        }

        return NonNullList.create();
    }

	public static NonNullList<ItemStack> getSellingItemsFromData(@Nonnull CompoundData data, @Nonnull RegistryAccess registry)
	{
		MerchantOffers offers = DataEntityUtils.getTradeOffers(data, registry);

		if (offers != null)
		{
			return getSellingItems(offers);
		}

		return NonNullList.create();
	}

	public static NonNullList<ItemStack> getSellingItems(@Nonnull MerchantOffers offers)
    {
        if (!offers.isEmpty())
        {
            NonNullList<ItemStack> result = NonNullList.create();

			for (MerchantOffer entry : offers)
			{
				if (entry != null)
				{
					ItemStack sellItem = entry.getResult();
					result.add(sellItem.copy());
				}
			}

            return result;
        }

        return NonNullList.create();
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Does not keep empty slots.
     *
     * @param stackIn The item holding the inventory contents
     * @return ()
     */
    public static NonNullList<ItemStack> getStoredItems(ItemStack stackIn)
    {
        ItemContainerContents container = stackIn.getComponents().get(DataComponents.CONTAINER);

        if (container != null)
        {
            Iterator<ItemStack> iter = container.nonEmptyStream().iterator();
            NonNullList<ItemStack> items = NonNullList.createWithCapacity((int) container.nonEmptyStream().count());
            int i = 0;

            // Using 'container.copyTo(items)' will break Litematica's Material List
            while (iter.hasNext())
            {
                items.add(iter.next().copy());
                i++;
            }

            return items;
        }

        return NonNullList.create();
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Preserves empty slots.
     *
     * @param stackIn   The item holding the inventory contents
     * @param slotCount the maximum number of slots, and thus also the size of the list to create
     * @return ()
     */
    public static NonNullList<ItemStack> getStoredItems(ItemStack stackIn, int slotCount)
    {
        ItemContainerContents itemContainer = stackIn.getComponents().get(DataComponents.CONTAINER);

        // Using itemContainer.copyTo() does not preserve empty stacks.
        if (itemContainer != null)
        {
            long defSlotCount = itemContainer.stream().count();

            // ContainerComponent.MAX_SLOTS = 256
            if (slotCount < 1)
            {
                slotCount = defSlotCount < 256 ? (int) defSlotCount : 256;
            }
            else
            {
                slotCount = Math.min(slotCount, 256);
            }

            NonNullList<ItemStack> items = NonNullList.createWithCapacity(slotCount);
            Iterator<ItemStack> iter = itemContainer.stream().iterator();

            for (int i = 0; i < slotCount; i++)
            {
                ItemStack entry;

                if (iter.hasNext())
                {
                    entry = iter.next();
                }
                else
                {
                    entry = ItemStack.EMPTY;
                }

                items.add(entry.copy());
//                LOGGER.debug("getStoredItems()[{}] entry [{}], items [{}]", i, entry.toString(), items.get(i).toString());
            }

            return items;
        }
        else
        {
            return NonNullList.create();
        }
    }

	/**
	 * Returns whether a bundle item stack has Items
	 * @param stack ()
	 * @return ()
	 */
    public static boolean bundleHasItems(ItemStack stack)
    {
        BundleContents bundleContainer = stack.getComponents().get(DataComponents.BUNDLE_CONTENTS);

        if (bundleContainer != null)
        {
            return bundleContainer.isEmpty() == false;
        }

        return false;
    }

    /**
     * Returns a Fraction value, probably indicating fill % value, rather than an actual item count.
     *
     * @param stack ()
     * @return ()
     */
    public static Fraction bundleOccupancy(ItemStack stack)
    {
        BundleContents bundleContainer = stack.getComponents().get(DataComponents.BUNDLE_CONTENTS);

        if (bundleContainer != null)
        {
            return bundleContainer.weight();
        }

        return Fraction.ZERO;
    }

    /**
     * Returns the "slot count" (Item Stacks) in the Bundle.
     *
     * @param stack ()
     * @return ()
     */
    public static int bundleCountItems(ItemStack stack)
    {
        BundleContents bundleContainer = stack.getComponents().get(DataComponents.BUNDLE_CONTENTS);

        if (bundleContainer != null)
        {
            return bundleContainer.size();
        }

        return -1;
    }

    /**
     * Returns a list of ItemStacks from the Bundle.  Does not preserve Empty Stacks.
     *
     * @param stackIn ()
     * @return ()
     */
    public static NonNullList<ItemStack> getBundleItems(ItemStack stackIn)
    {
        BundleContents bundleContainer = stackIn.getComponents().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);

        if (bundleContainer != null && bundleContainer.equals(BundleContents.EMPTY) == false)
        {
            int maxSlots = bundleContainer.size();
            NonNullList<ItemStack> items = NonNullList.createWithCapacity(maxSlots);
            Iterator<ItemStack> iter = bundleContainer.itemCopyStream().iterator();

            while (iter.hasNext())
            {
                ItemStack slot = iter.next();

                if (slot.isEmpty() == false)
                {
                    items.add(slot.copy());
                }
            }

            return items;
        }

        return NonNullList.create();
    }

    /**
     * Returns a list of ItemStacks from the Bundle.  Preserves Empty Stacks up to maxSlots.
     *
     * @param stackIn ()
     * @param maxSlots ()
     * @return ()
     */
    public static NonNullList<ItemStack> getBundleItems(ItemStack stackIn, int maxSlots)
    {
        BundleContents bundleContainer = stackIn.getComponents().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);

        if (bundleContainer != null && bundleContainer.equals(BundleContents.EMPTY) == false)
        {
            int defMaxSlots = bundleContainer.size();

            if (maxSlots < 1)
            {
                maxSlots = defMaxSlots;
            }
            else
            {
                maxSlots = maxSlots < 64 ? maxSlots : defMaxSlots;
            }

            NonNullList<ItemStack> items = NonNullList.createWithCapacity(maxSlots);
            Iterator<ItemStack> iter = bundleContainer.itemCopyStream().iterator();
            int limit = 0;

            while (iter.hasNext() && limit < maxSlots)
            {
                items.add(iter.next().copy());
                limit++;
            }

            return items;
        }

        return NonNullList.create();
    }

    /**
     * Returns a map of the stored item counts in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     *
     * @param stackShulkerBox ()
     * @return ()
     */
    public static Object2IntOpenHashMap<ItemType> getStoredItemCounts(ItemStack stackShulkerBox)
    {
        Object2IntOpenHashMap<ItemType> map = new Object2IntOpenHashMap<>();
        NonNullList<ItemStack> items = getStoredItems(stackShulkerBox);

        for (ItemStack stack : items)
        {
            if (stack.isEmpty() == false)
            {
                map.addTo(new ItemType(stack), stack.getCount());
            }
        }

        return map;
    }

    /**
     * Returns a map of the stored item counts in the given inventory.
     * This also counts the contents of any Shulker Boxes
     * (or other storage item with the same NBT data structure).
     *
     * @param inv ()
     * @return ()
     */
    public static Object2IntOpenHashMap<ItemType> getInventoryItemCounts(Container inv)
    {
        Object2IntOpenHashMap<ItemType> map = new Object2IntOpenHashMap<>();
        final int slots = inv.getContainerSize();

        for (int slot = 0; slot < slots; ++slot)
        {
            ItemStack stack = inv.getItem(slot);

            if (stack.isEmpty() == false)
            {
                map.addTo(new ItemType(stack, false, true), stack.getCount());

                if (stack.getItem() instanceof BlockItem &&
                    ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
                {
                    Object2IntOpenHashMap<ItemType> boxCounts = getStoredItemCounts(stack);

                    for (ItemType type : boxCounts.keySet())
                    {
                        map.addTo(type, boxCounts.getInt(type));
                    }
                }
            }
        }

        return map;
    }

    /**
     * Returns the given Inventory wrapped as a list of items
     *
     * @param inv ()
     * @return ()
     */
    public static NonNullList<ItemStack> getAsItemList(Container inv)
    {
        if (inv == null || inv.isEmpty())
        {
            return NonNullList.create();
        }

        int size = inv.getContainerSize();
        NonNullList<ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);

        for (int i = 0; i < size; i++)
        {
            ItemStack entry = inv.getItem(i);

            if (!entry.isEmpty())
            {
                list.set(i, entry.copy());
            }
        }

        return list;
    }

    /**
     * Returns the given list of items wrapped as an InventoryBasic
     *
     * @param items ()
     * @return ()
     */
    public static @Nullable Container getAsInventory(NonNullList<ItemStack> items)
    {
        if (items == null || items.isEmpty())
        {
            return null;
        }

        SimpleContainer inv = new SimpleContainer(items.size());

        for (int slot = 0; slot < items.size(); ++slot)
        {
            inv.setItem(slot, items.get(slot).copy());
//            LOGGER.debug("getAsInventory()[{}] inv [{}], items [{}]", slot, inv.getStack(slot).toString(), items.get(slot).toString());
        }

        return inv;
    }

    /**
     * Creates an ItemStack via a String
     *
     * @param itemNameIn (String containing the item name)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn)
    {
        return getItemStackFromString(itemNameIn, -1, DataComponentMap.EMPTY);
    }

    /**
     * Creates an ItemStack via a String
     *
     * @param itemNameIn (String containing the item name)
     * @param data       (ComponentMap data to import)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn, DataComponentMap data)
    {
        return getItemStackFromString(itemNameIn, -1, data);
    }

    /**
     * Creates an ItemStack via a String
     *
     * @param itemNameIn (String containing the item name)
     * @param count      (How many in this stack)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn, int count)
    {
        return getItemStackFromString(itemNameIn, count, DataComponentMap.EMPTY);
    }

    /**
     * Creates an ItemStack via a String
     *
     * @param itemNameIn (String containing the item name)
     * @param count      (How many in this stack)
     * @param data       (ComponentMap data to import)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn, int count, @Nonnull DataComponentMap data)
    {
        if (itemNameIn.isEmpty() || itemNameIn.equals("empty") || itemNameIn.equals("minecraft:air") || itemNameIn.equals("air"))
        {
            return ItemStack.EMPTY;
        }
        Matcher matcherBase = PATTERN_ITEM_BASE.matcher(itemNameIn);
        String itemName;
        ItemStack stackOut;

        if (matcherBase.matches())
        {
            itemName = matcherBase.group("name");

            if (itemName != null)
            {
                Identifier itemId = Identifier.tryParse(itemName);
                Holder<Item> itemEntry = BuiltInRegistries.ITEM.get(itemId).orElse(null);

                if (itemEntry != null && itemEntry.isBound())
                {
                    if (count < 0)
                    {
                        stackOut = new ItemStack(itemEntry);
                    }
                    else
                    {
                        stackOut = new ItemStack(itemEntry, count);
                    }
                    if (data.isEmpty() == false && data.equals(DataComponentMap.EMPTY) == false)
                    {
                        stackOut.applyComponents(data);
                    }

                    return stackOut;
                }
                else
                {
                    MaLiLib.LOGGER.warn(StringUtils.translate("malilib.error.invalid_item_stack_entry.string", itemName));
                }
            }
        }

        return null;
    }

    /**
     * Create ItemStack from a string, using a Data Components aware method, wrapping the Vanilla ItemStringReader method
     *
     * @param stringIn (The string to parse)
     * @param registry (Dynamic Registry)
     * @return (The item stack with components, or null)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String stringIn, @Nonnull RegistryAccess registry)
    {
        ItemParser itemStringReader = new ItemParser(registry);
        ItemParser.ItemResult results;

        try
        {
            results = itemStringReader.parse(new StringReader(stringIn));
        }
        catch (CommandSyntaxException e)
        {
            MaLiLib.LOGGER.warn(StringUtils.translate("malilib.error.invalid_item_stack_entry.nbt_syntax", stringIn));
            return null;
        }

        ItemStack stackOut = new ItemStack(results.item());
        stackOut.applyComponentsAndValidate(results.components());

        return stackOut;
    }

    /**
     * Get an Item's Registry Entry.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static Holder<Item> getItemEntry(Identifier id, @Nonnull RegistryAccess registry)
    {
        try
        {
            return registry.lookupOrThrow(BuiltInRegistries.ITEM.key()).get(id).orElseThrow();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Return whether the stack has Block Entity Nbt Data
     *
     * @param stack ()
     * @return ()
     */
    public static boolean hasStoredBlockEntityData(ItemStack stack)
    {
        return stack.has(DataComponents.BLOCK_ENTITY_DATA);
    }

    /**
     * Get the NBT Data out of a Stored Block Entity contained within an Item Stack.
     *
     * @param stack ()
     * @return ()
     */
    @Deprecated
    public static CompoundTag getStoredBlockEntityNbt(ItemStack stack)
    {
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA))
        {
            TypedEntityData<BlockEntityType<?>> component = stack.get(DataComponents.BLOCK_ENTITY_DATA);

            if (component != null)
            {
                return component.copyTagWithoutId();
            }
        }

        return new CompoundTag();
    }

	/**
	 * Get the Data Tag out of a Stored Block Entity contained within an Item Stack.
	 *
	 * @param stack ()
	 * @return ()
	 */
	public static CompoundData getStoredBlockEntityDataTag(ItemStack stack)
	{
		if (stack.has(DataComponents.BLOCK_ENTITY_DATA))
		{
			TypedEntityData<BlockEntityType<?>> component = stack.get(DataComponents.BLOCK_ENTITY_DATA);

			if (component != null)
			{
				return DataConverterNbt.fromVanillaCompound(component.copyTagWithoutId());
			}
		}

		return new CompoundData();
	}

	/**
	 * Return an item stack from NBT, or Empty
	 * @param tag ()
	 * @return ()
	 */
	@Deprecated
    public static ItemStack fromNbtOrEmpty(@Nullable Tag tag)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null)
        {
            return fromNbtOrEmpty(mc.level.registryAccess(), tag);
        }

        return ItemStack.EMPTY;
    }

	/**
	 * Return an item stack from Data Tag, or Empty
	 * @param data ()
	 * @return ()
	 */
	public static ItemStack fromDataOrEmpty(@Nullable CompoundData data)
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.level != null)
		{
			return fromDataOrEmpty(mc.level.registryAccess(), data);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Return an item stack from an NBT key
	 * @param nbt ()
	 * @param key ()
	 * @return ()
	 */
	@Deprecated
    public static ItemStack getStackCodec(@Nonnull CompoundTag nbt, String key)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null)
        {
            return getStackCodec(nbt, mc.level.registryAccess(), key);
        }

        return ItemStack.EMPTY;
    }

	/**
	 * Return an item stack from a Data Tag key
	 * @param data ()
	 * @param key ()
	 * @return ()
	 */
	public static ItemStack getStackCodec(@Nonnull CompoundData data, String key)
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.level != null)
		{
			return getStackCodec(data, mc.level.registryAccess(), key);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Insert an item stack into NBT using a key
	 * @param nbt ()
	 * @param stack ()
	 * @param key ()
	 * @return ()
	 */
	@Deprecated
    public static CompoundTag putStackCodec(@Nonnull CompoundTag nbt, @Nonnull ItemStack stack, String key)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null)
        {
            return putStackCodec(nbt, mc.level.registryAccess(), stack, key);
        }

        return new CompoundTag();
    }

	/**
	 * Insert an item stack into Data Tag using a key
	 * @param data ()
	 * @param stack ()
	 * @param key ()
	 * @return ()
	 */
	public static CompoundData putStackCodec(@Nonnull CompoundData data, @Nonnull ItemStack stack, String key)
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.level != null)
		{
			return putStackCodec(data, mc.level.registryAccess(), stack, key);
		}

		return new CompoundData();
	}

	/**
	 * Return an item stack from NBT, or Empty
	 * @param tag ()
	 * @param registry ()
	 * @return ()
	 */
	@Deprecated
    public static ItemStack fromNbtOrEmpty(@Nonnull RegistryAccess registry, @Nullable Tag tag)
    {
        if (tag == null)
        {
            return ItemStack.EMPTY;
        }

        return ItemStack.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), tag).resultOrPartial().orElse(ItemStack.EMPTY);
    }

	/**
	 * Return an item stack from Data Tag, or Empty
	 * @param tag ()
	 * @param registry ()
	 * @return ()
	 */
	public static ItemStack fromDataOrEmpty(@Nonnull RegistryAccess registry, @Nullable CompoundData tag)
	{
		if (tag == null)
		{
			return ItemStack.EMPTY;
		}

		return ItemStack.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(tag)).resultOrPartial().orElse(ItemStack.EMPTY);
	}

	/**
	 * Return a new NBT from an item stack
	 * @param stack ()
	 * @param registry ()
	 * @return ()
	 */
	public static CompoundTag toNbtOrEmpty(@Nonnull ItemStack stack, @Nonnull RegistryAccess registry)
	{
		return (CompoundTag) ItemStack.CODEC.encodeStart(registry.createSerializationContext(NbtOps.INSTANCE), stack).resultOrPartial().orElse(new CompoundTag());
	}

	/**
	 * Return a new Data Tag from an item stack
	 * @param stack ()
	 * @param registry ()
	 * @return ()
	 */
	public static CompoundData toDataOrEmpty(@Nonnull ItemStack stack, @Nonnull RegistryAccess registry)
	{
		return DataConverterNbt.fromVanillaCompound((CompoundTag) ItemStack.CODEC.encodeStart(registry.createSerializationContext(NbtOps.INSTANCE), stack).resultOrPartial().orElse(new CompoundTag()));
	}

	/**
	 * Return an item stack from an NBT key
	 * @param nbt ()
	 * @param registry ()
	 * @param key ()
	 * @return ()
	 */
	@Deprecated
    public static ItemStack getStackCodec(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry, String key)
    {
        return nbt.read(key, ItemStack.CODEC, registry.createSerializationContext(NbtOps.INSTANCE)).orElse(ItemStack.EMPTY);
    }

	/**
	 * Return an item stack from a Data Tag key
	 * @param data ()
	 * @param registry ()
	 * @param key ()
	 * @return ()
	 */
	public static ItemStack getStackCodec(@Nonnull CompoundData data, @Nonnull RegistryAccess registry, String key)
	{
		return data.getCodec(key, ItemStack.CODEC, registry.createSerializationContext(DataOps.INSTANCE)).orElse(ItemStack.EMPTY);
	}

	/**
	 * Insert an item stack into NBT using a key
	 * @param nbt ()
	 * @param registry ()
	 * @param stack ()
	 * @param key ()
	 * @return ()
	 */
	@Deprecated
    public static CompoundTag putStackCodec(@Nonnull CompoundTag nbt, @Nonnull RegistryAccess registry, @Nonnull ItemStack stack, String key)
    {
        nbt.store(key, ItemStack.CODEC, registry.createSerializationContext(NbtOps.INSTANCE), stack);
        return nbt;
    }

	/**
	 * Insert an item stack into Data Tag using a key
	 * @param data ()
	 * @param registry ()
	 * @param stack ()
	 * @param key ()
	 * @return ()
	 */
	public static CompoundData putStackCodec(@Nonnull CompoundData data, @Nonnull RegistryAccess registry, @Nonnull ItemStack stack, String key)
	{
		return data.putCodec(key, ItemStack.CODEC, registry.createSerializationContext(DataOps.INSTANCE), stack);
	}

	/**
	 * Return the {@link InteractionHand} defined by the Slot config.
	 * If the config is set to 'ANY', then pick whichever hand is empty first; such that:<br>
	 * - If Both hands are Empty, return the Main Hand.<br>
	 * - If One Hand is Empty and not the other; use that Hand.<br>
	 * - If both hands are full, then return the Main Hand.
	 *
	 * @param slot      The {@link HandSlot} configuration.  If this is null, then the "Any" Hand Slot logic applies.
	 * @return          The {@link InteractionHand} associated with the {@link HandSlot} value, or the Main Hand.
	 */
	public static InteractionHand getHandSlot(@Nullable HandSlot slot)
	{
		if (slot == null)
		{
			slot = HandSlot.ANY;
		}

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null) return slot.getHand();

		if (slot.getHand() == null)
		{
			return player.getOffhandItem().isEmpty()
			       ? (player.getMainHandItem().isEmpty() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
			       : InteractionHand.MAIN_HAND;
		}

		return slot.getHand();
	}
}
