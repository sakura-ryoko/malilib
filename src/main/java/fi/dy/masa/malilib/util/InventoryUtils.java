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
import org.jetbrains.annotations.ApiStatus;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

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
        return ItemStack.areItemsAndComponentsEqual(stack1, stack2);
    }

    /**
     * @return true if the stacks are identical, but ignoring their Components
     */
    public static boolean areStacksEqualIgnoreNbt(ItemStack stack1, ItemStack stack2)
    {
        return ItemStack.areItemsEqual(stack1, stack2);
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

        if (ref.isDamageable() && ref.isDamaged())
        {
            ref.setDamage(0);
        }
        if (check.isDamageable() && check.isDamaged())
        {
            check.setDamage(0);
        }

        return ItemStack.areItemsAndComponentsEqual(ref, check);
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
    public static <T> boolean areNbtEqualIgnoreKeys(@Nonnull ComponentMap tag1, @Nonnull ComponentMap tag2, @Nullable ComponentType<T> type, @Nullable Set<ComponentType<T>> ignoredKeys)
    {
        Set<ComponentType<?>> keys1;
        Set<ComponentType<?>> keys2;

        keys1 = tag1.getTypes();
        keys2 = tag2.getTypes();

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
            for (ComponentType<?> key : keys1)
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
    public static boolean areNbtEqualIgnoreKeys(@Nonnull NbtCompound tag1, @Nonnull NbtCompound tag2, @Nullable Set<String> ignoredKeys)
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
	@ApiStatus.Experimental
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
    public static void swapSlots(ScreenHandler container, int slotNum, int hotbarSlot)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.interactionManager.clickSlot(container.syncId, slotNum, hotbarSlot, SlotActionType.SWAP, mc.player);
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
    public static int findEmptySlotInPlayerInventory(ScreenHandler containerPlayer, boolean allowOffhand, boolean reverse)
    {
        final int startSlot = reverse ? containerPlayer.slots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : containerPlayer.slots.size();
        final int increment = reverse ? -1 : 1;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = containerPlayer.slots.get(slotNum);
            ItemStack stackSlot = slot.getStack();

            // Inventory crafting, armor and offhand slots are not valid
            if (stackSlot.isEmpty() && isRegularInventorySlot(slot.id, allowOffhand))
            {
                return slot.id;
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
    public static int findSlotWithItem(ScreenHandler container, ItemStack stackReference, boolean reverse)
    {
        final int startSlot = reverse ? container.slots.size() - 1 : 0;
        final int endSlot = reverse ? -1 : container.slots.size();
        final int increment = reverse ? -1 : 1;
        final boolean isPlayerInv = container instanceof PlayerScreenHandler;

        for (int slotNum = startSlot; slotNum != endSlot; slotNum += increment)
        {
            Slot slot = container.slots.get(slotNum);

            if ((isPlayerInv == false || isRegularInventorySlot(slot.id, false)) &&
                areStacksEqualIgnoreDurability(slot.getStack(), stackReference))
            {
                return slot.id;
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
    public static boolean swapItemToMainHand(ItemStack stackReference, MinecraftClient mc)
    {
        PlayerEntity player = mc.player;
        boolean isCreative = player.isInCreativeMode();

        // Already holding the requested item
        if (areStacksEqualIgnoreNbt(stackReference, player.getMainHandStack()))
        {
            return false;
        }

        if (isCreative)
        {
            player.getInventory().swapStackWithHotbar(stackReference);
            mc.interactionManager.clickCreativeStack(player.getMainHandStack(), 36 + player.getInventory().getSelectedSlot()); // sendSlotPacket
            return true;
        }
        else
        {
            int slot = findSlotWithItem(player.playerScreenHandler, stackReference, true);

            if (slot != -1)
            {
                int currentHotbarSlot = player.getInventory().getSelectedSlot();
                mc.interactionManager.clickSlot(player.playerScreenHandler.syncId, slot, currentHotbarSlot, SlotActionType.SWAP, mc.player);
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
    public static Inventory getInventory(World world, BlockPos pos)
    {
        @SuppressWarnings("deprecation")
        boolean isLoaded = world.isChunkLoaded(pos);

        if (isLoaded == false)
        {
            return null;
        }

        // The method in World now checks that the caller is from the same thread...
        BlockEntity te = world.getWorldChunk(pos).getBlockEntity(pos);

        if (te instanceof Inventory inv)
        {
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof ChestBlock && te instanceof ChestBlockEntity)
            {
                ChestType type = state.get(ChestBlock.CHEST_TYPE);

                if (type != ChestType.SINGLE)
                {
                    BlockPos posAdj = pos.offset(ChestBlock.getFacing(state));
                    @SuppressWarnings("deprecation")
                    boolean isLoadedAdj = world.isChunkLoaded(posAdj);

                    if (isLoadedAdj)
                    {
                        BlockState stateAdj = world.getBlockState(posAdj);
                        // The method in World now checks that the caller is from the same thread...
                        BlockEntity te2 = world.getWorldChunk(posAdj).getBlockEntity(posAdj);

                        if (stateAdj.getBlock() == state.getBlock() &&
                            te2 instanceof ChestBlockEntity &&
                            stateAdj.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE &&
                            stateAdj.get(ChestBlock.FACING) == state.get(ChestBlock.FACING))
                        {
                            Inventory invRight = type == ChestType.RIGHT ? inv : (Inventory) te2;
                            Inventory invLeft = type == ChestType.RIGHT ? (Inventory) te2 : inv;
                            inv = new DoubleInventory(invRight, invLeft);
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
        ContainerComponent container = stack.getComponents().get(DataComponentTypes.CONTAINER);

        if (container != null)
        {
            return container.iterateNonEmpty().iterator().hasNext();
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
    public static @Nullable NbtCompound stackHasNbtItems(ItemStack stack, @Nonnull DynamicRegistryManager registry)
    {
        if (stack.isEmpty() == false)
        {
            NbtCompound nbt = (NbtCompound) ItemStack.CODEC.encodeStart(registry.getOps(NbtOps.INSTANCE), stack).getOrThrow();

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
	public static @Nullable CompoundData stackHasDataItems(ItemStack stack, @Nonnull DynamicRegistryManager registry)
	{
		if (!stack.isEmpty())
		{
			CompoundData data = DataConverterNbt.fromVanillaCompound((NbtCompound) ItemStack.CODEC.encodeStart(registry.getOps(NbtOps.INSTANCE), stack).getOrThrow());

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
    public static boolean hasNbtItems(NbtCompound nbt)
    {
        if (nbt.contains(NbtKeys.ITEMS))
        {
            NbtList tagList = nbt.getListOrEmpty(NbtKeys.ITEMS);
            return !tagList.isEmpty();
        }
        else if (nbt.contains(NbtKeys.INVENTORY))
        {
            NbtList tagList = nbt.getListOrEmpty(NbtKeys.INVENTORY);
            return !tagList.isEmpty();
        }
        else if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            NbtList tagList = nbt.getListOrEmpty(NbtKeys.ENDER_ITEMS);
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
	@ApiStatus.Experimental
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
    public static DefaultedList<ItemStack> getNbtItems(@Nonnull NbtCompound nbt)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null)
        {
            return DefaultedList.of();
        }

        return getNbtItems(nbt, -1, mc.world.getRegistryManager());
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
    public static DefaultedList<ItemStack> getNbtItems(@Nonnull NbtCompound nbt, int slotCount, @Nonnull DynamicRegistryManager registry)
    {
        if (slotCount > NbtInventory.MAX_SIZE)
        {
            slotCount = NbtInventory.MAX_SIZE;
        }

        // Most Common Tag --> NbtElement.LIST_TYPE ???
        if (nbt.contains(NbtKeys.ITEMS))
        {
            NbtList list = nbt.getListOrEmpty(NbtKeys.ITEMS);

            if (slotCount < 0)
            {
                // Uses slots
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            NbtInventory nbtInv = NbtInventory.fromNbtList(list, false, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return DefaultedList.of();
            }

            return nbtInv.toVanillaList(slotCount);
        }
        // A few Entities use this
        else if (nbt.contains(NbtKeys.INVENTORY))
        {
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.getInventoryType(nbt);
            boolean isPlayer = type == InventoryOverlay.InventoryRenderType.PLAYER;

            NbtList list = nbt.getListOrEmpty(NbtKeys.INVENTORY);
            if (slotCount < 0)
            {
                // Doesn't use slots
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            NbtInventory nbtInv = NbtInventory.fromNbtList(list, !isPlayer, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return DefaultedList.of();
            }
            
            return nbtInv.toVanillaList(slotCount);
        }
        // Ender Chest
        else if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            NbtList list = nbt.getListOrEmpty(NbtKeys.ENDER_ITEMS);

            if (slotCount < 0)
            {
                // Uses slots
                slotCount = Math.max(list.size(), NbtInventory.DEFAULT_SIZE);
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            NbtInventory nbtInv = NbtInventory.fromNbtList(list, false, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return DefaultedList.of();
            }

            return nbtInv.toVanillaList(slotCount);
        }
        else if (nbt.contains(NbtKeys.ITEM))
        {
            // item (DecoratedPot, ItemEntity)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM));
            DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }
        else if (nbt.contains(NbtKeys.ITEM_2))
        {
            // Item (ItemFrame)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM_2));
            DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }
        else if (nbt.contains(NbtKeys.BOOK))
        {
            // Book (Lectern)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.BOOK));
            DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }
        else if (nbt.contains(NbtKeys.RECORD))
        {
            // RecordItem (Jukebox)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.RECORD));
            DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
            items.addFirst(entry);

            return items;
        }

        return DefaultedList.of();
    }

	/**
	 * Returns the list of items currently stored in the given NBT Items[] interface.
	 * Does not keep empty slots.
	 *
	 * @param data The item holding the inventory contents
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static DefaultedList<ItemStack> getDataItems(@Nonnull CompoundData data)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.world == null)
		{
			return DefaultedList.of();
		}

		return getDataItems(data, -1, mc.world.getRegistryManager());
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
	@ApiStatus.Experimental
	public static DefaultedList<ItemStack> getDataItems(@Nonnull CompoundData data, int slotCount, @Nonnull DynamicRegistryManager registry)
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
				return DefaultedList.of();
			}

			return nbtInv.toVanillaList(slotCount);
		}
		// A few Entities use this
		else if (data.contains(NbtKeys.INVENTORY, Constants.NBT.TAG_LIST))
		{
			InventoryOverlayType type = InventoryOverlay.getInventoryTypeNew(data);
			boolean isPlayer = type == InventoryOverlayType.PLAYER;

			ListData list = data.getList(NbtKeys.INVENTORY);
			if (slotCount < 0)
			{
				// Doesn't use slots
				slotCount = list.size();
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);

			NbtInventory nbtInv = NbtInventory.fromDataList(list, !isPlayer, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return DefaultedList.of();
			}

			return nbtInv.toVanillaList(slotCount);
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
				return DefaultedList.of();
			}

			return nbtInv.toVanillaList(slotCount);
		}
		else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			// item (DecoratedPot, ItemEntity)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM));
			DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}
		else if (data.contains(NbtKeys.ITEM_2, Constants.NBT.TAG_COMPOUND))
		{
			// Item (ItemFrame)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM_2));
			DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}
		else if (data.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
		{
			// Book (Lectern)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.BOOK));
			DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}
		else if (data.contains(NbtKeys.RECORD, Constants.NBT.TAG_COMPOUND))
		{
			// RecordItem (Jukebox)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.RECORD));
			DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
			items.addFirst(entry);

			return items;
		}

		return DefaultedList.of();
	}

	/**
     * Returns Inventory of items currently stored in the given NBT Items[] interface.
     * Preserves empty slots, unless the "Inventory" interface is used.
     *
     * @param nbt     The tag holding the inventory contents
     * @return ()
     */
    public static Inventory getNbtInventory(@Nonnull NbtCompound nbt)
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return null;
        }

        return getNbtInventory(nbt, -1, MinecraftClient.getInstance().world.getRegistryManager());
    }

	/**
	 * Returns Inventory of items currently stored in the given NBT Items[] interface.
	 * Preserves empty slots, unless the "Inventory" interface is used.
	 *
	 * @param data     The tag holding the inventory contents
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static Inventory getDataInventory(@Nonnull CompoundData data)
	{
		if (MinecraftClient.getInstance().world == null)
		{
			return null;
		}

		return getDataInventory(data, -1, MinecraftClient.getInstance().world.getRegistryManager());
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
    public static Inventory getNbtInventory(@Nonnull NbtCompound nbt, int slotCount, @Nonnull DynamicRegistryManager registry)
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
                NbtList list = nbt.getListOrEmpty(NbtKeys.ITEMS);
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

			NbtInventory nbtInv = NbtInventory.fromNbt(nbt, NbtKeys.ITEMS, false, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return null;
            }

            return nbtInv.toInventory(slotCount);
        }
        else if (nbt.contains(NbtKeys.INVENTORY))
        {
            InventoryOverlay.InventoryRenderType type = InventoryOverlay.getInventoryType(nbt);
            boolean isPlayer = type == InventoryOverlay.InventoryRenderType.PLAYER;

            // Entities use this (Piglin, Villager, a few others)
            if (slotCount < 0)
            {
                NbtList list = nbt.getListOrEmpty(NbtKeys.INVENTORY);
                // Doesn't use slots
                slotCount = list.size();
            }

            slotCount = NbtInventory.getAdjustedSize(slotCount);

            // "Inventory" tags might not include Slot ID's, but a Player will.
            NbtInventory nbtInv = NbtInventory.fromNbt(nbt, NbtKeys.INVENTORY, !isPlayer, registry);

            if (nbtInv == null || nbtInv.isEmpty())
            {
                return null;
            }

            return nbtInv.toInventory(slotCount);
        }
        else if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            // Ender Chest
            NbtList list = nbt.getListOrEmpty(NbtKeys.ENDER_ITEMS);

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

            return nbtInv.toInventory(Math.max(slotCount, NbtInventory.DEFAULT_SIZE));
        }
        else if (nbt.contains(NbtKeys.ITEM))
        {
            // item (DecoratedPot, ItemEntity)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM));
            SimpleInventory inv = new SimpleInventory(1);
            inv.setStack(0, entry.copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.ITEM_2))
        {
            // Item (Item Frame)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM_2));
            SimpleInventory inv = new SimpleInventory(1);
            inv.setStack(0, entry.copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.BOOK))
        {
            // Book (Lectern)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.BOOK));
            SimpleInventory inv = new SimpleInventory(1);
            inv.setStack(0, entry.copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.RECORD))
        {
            // RecordItem (Jukebox)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.RECORD));
            SimpleInventory inv = new SimpleInventory(1);
            inv.setStack(0, entry.copy());

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
	@ApiStatus.Experimental
	public static Inventory getDataInventory(@Nonnull CompoundData data, int slotCount, @Nonnull DynamicRegistryManager registry)
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

			return nbtInv.toInventory(slotCount);
		}
		else if (data.contains(NbtKeys.INVENTORY, Constants.NBT.TAG_LIST))
		{
			InventoryOverlayType type = InventoryOverlay.getInventoryTypeNew(data);
			boolean isPlayer = type == InventoryOverlayType.PLAYER;

			// Entities use this (Piglin, Villager, a few others)
			if (slotCount < 0)
			{
				ListData list = data.getList(NbtKeys.INVENTORY);
				// Doesn't use slots
				slotCount = list.size();
			}

			slotCount = NbtInventory.getAdjustedSize(slotCount);

			// "Inventory" tags might not include Slot ID's, but a Player will.
			NbtInventory nbtInv = NbtInventory.fromData(data, NbtKeys.INVENTORY, !isPlayer, registry);

			if (nbtInv == null || nbtInv.isEmpty())
			{
				return null;
			}

			return nbtInv.toInventory(slotCount);
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

			return nbtInv.toInventory(Math.max(slotCount, NbtInventory.DEFAULT_SIZE));
		}
		else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			// item (DecoratedPot, ItemEntity)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM));
			SimpleInventory inv = new SimpleInventory(1);
			inv.setStack(0, entry.copy());

			return inv;
		}
		else if (data.contains(NbtKeys.ITEM_2, Constants.NBT.TAG_COMPOUND))
		{
			// Item (Item Frame)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM_2));
			SimpleInventory inv = new SimpleInventory(1);
			inv.setStack(0, entry.copy());

			return inv;
		}
		else if (data.contains(NbtKeys.BOOK, Constants.NBT.TAG_COMPOUND))
		{
			// Book (Lectern)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.BOOK));
			SimpleInventory inv = new SimpleInventory(1);
			inv.setStack(0, entry.copy());

			return inv;
		}
		else if (data.contains(NbtKeys.RECORD, Constants.NBT.TAG_COMPOUND))
		{
			// RecordItem (Jukebox)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.RECORD));
			SimpleInventory inv = new SimpleInventory(1);
			inv.setStack(0, entry.copy());

			return inv;
		}

		return null;
	}

	/**
     * Executes the "Inventory Display Horse Fix" (Saddle Offset) for NBT-based Displays.
     *
     * @param nbt ()
     * @param slotCount ()
     * @param registry ()
     * @return ()
     */
    public static Inventory getNbtInventoryHorseFix(@Nonnull NbtCompound nbt, int slotCount, @Nonnull DynamicRegistryManager registry)
    {
        DefaultedList<ItemStack> horseEquipment = NbtEntityUtils.getHorseEquipmentFromNbt(nbt, registry);

        if (slotCount > NbtInventory.MAX_SIZE)
        {
            slotCount = NbtInventory.MAX_SIZE;
        }

        // Shift inv ahead by 1 slot for horses (1.21 only)
        if (nbt.contains(NbtKeys.ITEMS))
        {
            if (slotCount < 0)
            {
                NbtList list = nbt.getListOrEmpty(NbtKeys.ITEMS);
                slotCount = list.size();
            }

            SimpleInventory inv = new SimpleInventory(slotCount + 1);

            inv.setStack(0, horseEquipment.getLast());

            NbtInventory nbtInv = NbtInventory.fromNbt(nbt, NbtKeys.ITEMS, false, registry);

            // Chested Horse
            if (nbtInv != null && !nbtInv.isEmpty())
            {
                DefaultedList<ItemStack> items = nbtInv.toVanillaList(slotCount + 1);

                for (int i = 0; i < slotCount; i++)
                {
                    inv.setStack(i + 1, items.get(i));
                }
            }

            return inv;
        }
        // Saddled only fix
        else if (!horseEquipment.getLast().isEmpty())
        {
            SimpleInventory inv = new SimpleInventory(1);
            inv.setStack(0, horseEquipment.getLast().copy());

            return inv;
        }
        else if (nbt.contains(NbtKeys.ITEM))
        {
            // item (DecoratedPot, ItemEntity)
            ItemStack entry = fromNbtOrEmpty(registry, nbt.get(NbtKeys.ITEM));
            SimpleInventory inv = new SimpleInventory(1);
            inv.setStack(0, entry.copy());

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
	@ApiStatus.Experimental
	public static Inventory getDataInventoryHorseFix(@Nonnull CompoundData data, int slotCount, @Nonnull DynamicRegistryManager registry)
	{
		DefaultedList<ItemStack> horseEquipment = DataEntityUtils.getHorseEquipment(data, registry);

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

			SimpleInventory inv = new SimpleInventory(slotCount + 1);

			inv.setStack(0, horseEquipment.getLast());

			NbtInventory nbtInv = NbtInventory.fromData(data, NbtKeys.ITEMS, false, registry);

			// Chested Horse
			if (nbtInv != null && !nbtInv.isEmpty())
			{
				DefaultedList<ItemStack> items = nbtInv.toVanillaList(slotCount + 1);

				for (int i = 0; i < slotCount; i++)
				{
					inv.setStack(i + 1, items.get(i));
				}
			}

			return inv;
		}
		// Saddled only fix
		else if (!horseEquipment.getLast().isEmpty())
		{
			SimpleInventory inv = new SimpleInventory(1);
			inv.setStack(0, horseEquipment.getLast().copy());

			return inv;
		}
		else if (data.contains(NbtKeys.ITEM, Constants.NBT.TAG_COMPOUND))
		{
			// item (DecoratedPot, ItemEntity)
			ItemStack entry = fromDataOrEmpty(registry, data.getCompound(NbtKeys.ITEM));
			SimpleInventory inv = new SimpleInventory(1);
			inv.setStack(0, entry.copy());

			return inv;
		}

		return null;
	}

    @Nullable
    public static EnderChestInventory getPlayerEnderItems(PlayerEntity player)
    {
        if (player != null)
        {
            return ((IMixinPlayerEntity) player).malilib_getEnderItems();
        }

        return null;
    }

    @Nullable
    public static EnderChestInventory getPlayerEnderItemsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        if (nbt.contains(NbtKeys.ENDER_ITEMS))
        {
            EnderChestInventory inv = new EnderChestInventory();
            NbtView view = NbtView.getReader(nbt, registry);

            inv.readData(view.getReader().getTypedListView(NbtKeys.ENDER_ITEMS, StackWithSlot.CODEC));

            return inv;
        }

        return null;
    }

	@Nullable
	@ApiStatus.Experimental
	public static EnderChestInventory getPlayerEnderItemsFromData(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		LOGGER.debug("getPlayerEnderItemsFromData: containsList({}) : containsLeinient({})", data.contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST), data.containsLenient(NbtKeys.ENDER_ITEMS));
		if (data.contains(NbtKeys.ENDER_ITEMS, Constants.NBT.TAG_LIST))
		{
			EnderChestInventory inv = new EnderChestInventory();
			NbtView view = NbtView.getReader(data, registry);

			inv.readData(view.getReader().getTypedListView(NbtKeys.ENDER_ITEMS, StackWithSlot.CODEC));

			LOGGER.debug("getPlayerEnderItemsFromData: inv [{}]", inv.size());
			return inv;
		}

		LOGGER.debug("getPlayerEnderItemsFromData: inv: [NULL]");
		return null;
	}

    public static DefaultedList<ItemStack> getSellingItemsFromNbt(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        TradeOfferList offers = NbtEntityUtils.getTradeOffersFromNbt(nbt, registry);

        if (offers != null)
        {
            return getSellingItems(offers);
        }

        return DefaultedList.of();
    }

	@ApiStatus.Experimental
	public static DefaultedList<ItemStack> getSellingItemsFromData(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		TradeOfferList offers = DataEntityUtils.getTradeOffers(data, registry);

		if (offers != null)
		{
			return getSellingItems(offers);
		}

		return DefaultedList.of();
	}

	public static DefaultedList<ItemStack> getSellingItems(@Nonnull TradeOfferList offers)
    {
        if (!offers.isEmpty())
        {
            DefaultedList<ItemStack> result = DefaultedList.of();

			for (TradeOffer entry : offers)
			{
				if (entry != null)
				{
					ItemStack sellItem = entry.getSellItem();
					result.add(sellItem.copy());
				}
			}

            return result;
        }

        return DefaultedList.of();
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Does not keep empty slots.
     *
     * @param stackIn The item holding the inventory contents
     * @return ()
     */
    public static DefaultedList<ItemStack> getStoredItems(ItemStack stackIn)
    {
        ContainerComponent container = stackIn.getComponents().get(DataComponentTypes.CONTAINER);

        if (container != null)
        {
            Iterator<ItemStack> iter = container.streamNonEmpty().iterator();
            DefaultedList<ItemStack> items = DefaultedList.ofSize((int) container.streamNonEmpty().count());
            int i = 0;

            // Using 'container.copyTo(items)' will break Litematica's Material List
            while (iter.hasNext())
            {
                items.add(iter.next().copy());
                i++;
            }

            return items;
        }

        return DefaultedList.of();
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
    public static DefaultedList<ItemStack> getStoredItems(ItemStack stackIn, int slotCount)
    {
        ContainerComponent itemContainer = stackIn.getComponents().get(DataComponentTypes.CONTAINER);

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

            DefaultedList<ItemStack> items = DefaultedList.ofSize(slotCount);
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
            return DefaultedList.of();
        }
    }

	/**
	 * Returns whether a bundle item stack has Items
	 * @param stack ()
	 * @return ()
	 */
    public static boolean bundleHasItems(ItemStack stack)
    {
        BundleContentsComponent bundleContainer = stack.getComponents().get(DataComponentTypes.BUNDLE_CONTENTS);

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
        BundleContentsComponent bundleContainer = stack.getComponents().get(DataComponentTypes.BUNDLE_CONTENTS);

        if (bundleContainer != null)
        {
            return bundleContainer.getOccupancy();
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
        BundleContentsComponent bundleContainer = stack.getComponents().get(DataComponentTypes.BUNDLE_CONTENTS);

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
    public static DefaultedList<ItemStack> getBundleItems(ItemStack stackIn)
    {
        BundleContentsComponent bundleContainer = stackIn.getComponents().getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);

        if (bundleContainer != null && bundleContainer.equals(BundleContentsComponent.DEFAULT) == false)
        {
            int maxSlots = bundleContainer.size();
            DefaultedList<ItemStack> items = DefaultedList.ofSize(maxSlots);
            Iterator<ItemStack> iter = bundleContainer.stream().iterator();

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

        return DefaultedList.of();
    }

    /**
     * Returns a list of ItemStacks from the Bundle.  Preserves Empty Stacks up to maxSlots.
     *
     * @param stackIn ()
     * @param maxSlots ()
     * @return ()
     */
    public static DefaultedList<ItemStack> getBundleItems(ItemStack stackIn, int maxSlots)
    {
        BundleContentsComponent bundleContainer = stackIn.getComponents().getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);

        if (bundleContainer != null && bundleContainer.equals(BundleContentsComponent.DEFAULT) == false)
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

            DefaultedList<ItemStack> items = DefaultedList.ofSize(maxSlots);
            Iterator<ItemStack> iter = bundleContainer.stream().iterator();
            int limit = 0;

            while (iter.hasNext() && limit < maxSlots)
            {
                items.add(iter.next().copy());
                limit++;
            }

            return items;
        }

        return DefaultedList.of();
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
        DefaultedList<ItemStack> items = getStoredItems(stackShulkerBox);

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
    public static Object2IntOpenHashMap<ItemType> getInventoryItemCounts(Inventory inv)
    {
        Object2IntOpenHashMap<ItemType> map = new Object2IntOpenHashMap<>();
        final int slots = inv.size();

        for (int slot = 0; slot < slots; ++slot)
        {
            ItemStack stack = inv.getStack(slot);

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
    public static DefaultedList<ItemStack> getAsItemList(Inventory inv)
    {
        if (inv == null || inv.isEmpty())
        {
            return DefaultedList.of();
        }

        int size = inv.size();
        DefaultedList<ItemStack> list = DefaultedList.ofSize(size, ItemStack.EMPTY);

        for (int i = 0; i < size; i++)
        {
            ItemStack entry = inv.getStack(i);

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
    public static @Nullable Inventory getAsInventory(DefaultedList<ItemStack> items)
    {
        if (items == null || items.isEmpty())
        {
            return null;
        }

        SimpleInventory inv = new SimpleInventory(items.size());

        for (int slot = 0; slot < items.size(); ++slot)
        {
            inv.setStack(slot, items.get(slot).copy());
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
        return getItemStackFromString(itemNameIn, -1, ComponentMap.EMPTY);
    }

    /**
     * Creates an ItemStack via a String
     *
     * @param itemNameIn (String containing the item name)
     * @param data       (ComponentMap data to import)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn, ComponentMap data)
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
        return getItemStackFromString(itemNameIn, count, ComponentMap.EMPTY);
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
    public static ItemStack getItemStackFromString(String itemNameIn, int count, @Nonnull ComponentMap data)
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
                RegistryEntry<Item> itemEntry = Registries.ITEM.getEntry(itemId).orElse(null);

                if (itemEntry != null && itemEntry.hasKeyAndValue())
                {
                    if (count < 0)
                    {
                        stackOut = new ItemStack(itemEntry);
                    }
                    else
                    {
                        stackOut = new ItemStack(itemEntry, count);
                    }
                    if (data.isEmpty() == false && data.equals(ComponentMap.EMPTY) == false)
                    {
                        stackOut.applyComponentsFrom(data);
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
    public static ItemStack getItemStackFromString(String stringIn, @Nonnull DynamicRegistryManager registry)
    {
        ItemStringReader itemStringReader = new ItemStringReader(registry);
        ItemStringReader.ItemResult results;

        try
        {
            results = itemStringReader.consume(new StringReader(stringIn));
        }
        catch (CommandSyntaxException e)
        {
            MaLiLib.LOGGER.warn(StringUtils.translate("malilib.error.invalid_item_stack_entry.nbt_syntax", stringIn));
            return null;
        }

        ItemStack stackOut = new ItemStack(results.item());
        stackOut.applyChanges(results.components());

        return stackOut;
    }

    /**
     * Get an Item's Registry Entry.
     *
     * @param id ()
     * @param registry ()
     * @return ()
     */
    public static RegistryEntry<Item> getItemEntry(Identifier id, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return registry.getOrThrow(Registries.ITEM.getKey()).getEntry(id).orElseThrow();
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
        return stack.contains(DataComponentTypes.BLOCK_ENTITY_DATA);
    }

    /**
     * Get the NBT Data out of a Stored Block Entity contained within an Item Stack.
     *
     * @param stack ()
     * @return ()
     */
    public static NbtCompound getStoredBlockEntityNbt(ItemStack stack)
    {
        if (stack.contains(DataComponentTypes.BLOCK_ENTITY_DATA))
        {
            TypedEntityData<BlockEntityType<?>> component = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);

            if (component != null)
            {
                return component.copyNbtWithoutId();
            }
        }

        return new NbtCompound();
    }

	/**
	 * Get the Data Tag out of a Stored Block Entity contained within an Item Stack.
	 *
	 * @param stack ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static CompoundData getStoredBlockEntityDataTag(ItemStack stack)
	{
		if (stack.contains(DataComponentTypes.BLOCK_ENTITY_DATA))
		{
			TypedEntityData<BlockEntityType<?>> component = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);

			if (component != null)
			{
				return DataConverterNbt.fromVanillaCompound(component.copyNbtWithoutId());
			}
		}

		return new CompoundData();
	}

	/**
	 * Return an item stack from NBT, or Empty
	 * @param tag ()
	 * @return ()
	 */
    public static ItemStack fromNbtOrEmpty(@Nullable NbtElement tag)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world != null)
        {
            return fromNbtOrEmpty(mc.world.getRegistryManager(), tag);
        }

        return ItemStack.EMPTY;
    }

	/**
	 * Return an item stack from Data Tag, or Empty
	 * @param data ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static ItemStack fromDataOrEmpty(@Nullable CompoundData data)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.world != null)
		{
			return fromDataOrEmpty(mc.world.getRegistryManager(), data);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Return an item stack from an NBT key
	 * @param nbt ()
	 * @param key ()
	 * @return ()
	 */
    public static ItemStack getStackCodec(@Nonnull NbtCompound nbt, String key)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world != null)
        {
            return getStackCodec(nbt, mc.world.getRegistryManager(), key);
        }

        return ItemStack.EMPTY;
    }

	/**
	 * Return an item stack from a Data Tag key
	 * @param data ()
	 * @param key ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static ItemStack getStackCodec(@Nonnull CompoundData data, String key)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.world != null)
		{
			return getStackCodec(data, mc.world.getRegistryManager(), key);
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
    public static NbtCompound putStackCodec(@Nonnull NbtCompound nbt, @Nonnull ItemStack stack, String key)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world != null)
        {
            return putStackCodec(nbt, mc.world.getRegistryManager(), stack, key);
        }

        return new NbtCompound();
    }

	/**
	 * Insert an item stack into Data Tag using a key
	 * @param data ()
	 * @param stack ()
	 * @param key ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static CompoundData putStackCodec(@Nonnull CompoundData data, @Nonnull ItemStack stack, String key)
	{
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.world != null)
		{
			return putStackCodec(data, mc.world.getRegistryManager(), stack, key);
		}

		return new CompoundData();
	}

	/**
	 * Return an item stack from NBT, or Empty
	 * @param tag ()
	 * @param registry ()
	 * @return ()
	 */
    public static ItemStack fromNbtOrEmpty(@Nonnull DynamicRegistryManager registry, @Nullable NbtElement tag)
    {
        if (tag == null)
        {
            return ItemStack.EMPTY;
        }

        return ItemStack.CODEC.parse(registry.getOps(NbtOps.INSTANCE), tag).resultOrPartial().orElse(ItemStack.EMPTY);
    }

	/**
	 * Return an item stack from Data Tag, or Empty
	 * @param tag ()
	 * @param registry ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static ItemStack fromDataOrEmpty(@Nonnull DynamicRegistryManager registry, @Nullable CompoundData tag)
	{
		if (tag == null)
		{
			return ItemStack.EMPTY;
		}

		return ItemStack.CODEC.parse(registry.getOps(NbtOps.INSTANCE), DataConverterNbt.toVanillaCompound(tag)).resultOrPartial().orElse(ItemStack.EMPTY);
	}

	/**
	 * Return an item stack from an NBT key
	 * @param nbt ()
	 * @param registry ()
	 * @param key ()
	 * @return ()
	 */
    public static ItemStack getStackCodec(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry, String key)
    {
        return nbt.get(key, ItemStack.CODEC, registry.getOps(NbtOps.INSTANCE)).orElse(ItemStack.EMPTY);
    }

	/**
	 * Return an item stack from a Data Tag key
	 * @param data ()
	 * @param registry ()
	 * @param key ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static ItemStack getStackCodec(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry, String key)
	{
		return data.getCodec(key, ItemStack.CODEC, registry.getOps(DataOps.INSTANCE)).orElse(ItemStack.EMPTY);
	}

	/**
	 * Insert an item stack into NBT using a key
	 * @param nbt ()
	 * @param registry ()
	 * @param stack ()
	 * @param key ()
	 * @return ()
	 */
    public static NbtCompound putStackCodec(@Nonnull NbtCompound nbt, @Nonnull DynamicRegistryManager registry, @Nonnull ItemStack stack, String key)
    {
        nbt.put(key, ItemStack.CODEC, registry.getOps(NbtOps.INSTANCE), stack);
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
	@ApiStatus.Experimental
	public static CompoundData putStackCodec(@Nonnull CompoundData data, @Nonnull DynamicRegistryManager registry, @Nonnull ItemStack stack, String key)
	{
		return data.putCodec(key, ItemStack.CODEC, registry.getOps(DataOps.INSTANCE), stack);
	}
}
