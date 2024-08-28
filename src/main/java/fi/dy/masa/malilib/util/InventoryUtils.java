package fi.dy.masa.malilib.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.lang3.math.Fraction;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import fi.dy.masa.malilib.MaLiLib;

public class InventoryUtils
{
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
     * @param tag1 (ComponentMap 1)
     * @param tag2 (ComponentMap 2)
     * @param type (DataComponentType) [OPTIONAL]
     * @param ignoredKeys (keys to ignore) [OPTIONAL]
     * @return (return value)
     * @param <T> DataComponentType extendable
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
     * @param tag1 (NbtCompound tag1)
     * @param tag2 (NbtCompound tag2)
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
     * Swaps the stack from the slot <b>slotNum</b> to the given hotbar slot <b>hotbarSlot</b>
     * @param container
     * @param slotNum
     * @param hotbarSlot
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
     * @param slotNumber
     * @param allowOffhand
     * @return
     */
    public static boolean isRegularInventorySlot(int slotNumber, boolean allowOffhand)
    {
        return slotNumber > 8 && (allowOffhand || slotNumber < 45);
    }

    /**
     * Finds an empty slot in the player inventory. Armor slots are not valid for the return value of this method.
     * Whether or not the offhand slot is valid, depends on the <b>allowOffhand</b> argument.
     * @param containerPlayer
     * @param allowOffhand
     * @param reverse
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
     * @param container
     * @param stackReference
     * @param reverse
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
     * @param stackReference
     * @param mc
     * @return true if an item was swapped to the main hand, false if it was already in the hand, or was not found in the inventory
     */
    public static boolean swapItemToMainHand(ItemStack stackReference, MinecraftClient mc)
    {
        PlayerEntity player = mc.player;
        boolean isCreative = player.isCreative();

        // Already holding the requested item
        if (areStacksEqualIgnoreNbt(stackReference, player.getMainHandStack()))
        {
            return false;
        }

        if (isCreative)
        {
            player.getInventory().addPickBlock(stackReference);
            mc.interactionManager.clickCreativeStack(player.getMainHandStack(), 36 + player.getInventory().selectedSlot); // sendSlotPacket
            return true;
        }
        else
        {
            int slot = findSlotWithItem(player.playerScreenHandler, stackReference, true);

            if (slot != -1)
            {
                int currentHotbarSlot = player.getInventory().selectedSlot;
                mc.interactionManager.clickSlot(player.playerScreenHandler.syncId, slot, currentHotbarSlot, SlotActionType.SWAP, mc.player);
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the inventory at the given position, if any.
     * Combines chest inventories into double chest inventories when applicable.
     * @param world
     * @param pos
     * @return
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
                            Inventory invRight = type == ChestType.RIGHT ?             inv : (Inventory) te2;
                            Inventory invLeft  = type == ChestType.RIGHT ? (Inventory) te2 :             inv;
                            inv = new DoubleInventory(invRight, invLeft);
                        }
                    }
                }
            }
            /*
            else if (state.getBlock() instanceof CrafterBlock && te instanceof CrafterBlockEntity ce)
            {
                return getAsInventory(ce.getHeldStacks());
            }
             */

            return inv;
        }

        return null;
    }

    /**
     * Checks if the given Shulker Box (or other storage item with the
     * same NBT data structure) currently contains any items.
     * @param stackShulkerBox
     * @return
     */
    public static boolean shulkerBoxHasItems(ItemStack stackShulkerBox)
    {
        ContainerComponent countContainer = stackShulkerBox.getComponents().get(DataComponentTypes.CONTAINER);

        if (countContainer != null)
        {
            return countContainer.iterateNonEmpty().iterator().hasNext();
        }

        return false;
    }

    /**
     * Checks if the given NBT currently contains any items, using the NBT Items[] interface.
     * @param tag
     * @return
     */
    public static boolean hasNbtItems(NbtCompound tag)
    {
        if (tag.contains("Items", Constants.NBT.TAG_LIST))
        {
            NbtList tagList = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
            return tagList.size() > 0;
        }

        return false;
    }

    /**
     * Returns the list of items currently stored in the given NBT Items[] interface.
     * Does not keep empty slots.
     * @param tag The item holding the inventory contents
     * @return
     */
    public static DefaultedList<ItemStack> getNbtItems(NbtCompound tag)
    {
        if (tag.contains("Items", Constants.NBT.TAG_LIST))
        {
            NbtList tagList = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
            final int count = tagList.size();
            DefaultedList<ItemStack> items = DefaultedList.ofSize(count);

            for (int i = 0; i < count; ++i)
            {
                ItemStack stack = ItemStack.fromNbtOrEmpty(MinecraftClient.getInstance().world.getRegistryManager(), tagList.getCompound(i));

                if (stack.isEmpty() == false)
                {
                    items.add(stack);
                }
            }

            return items;
        }

        return DefaultedList.of();
    }

    /**
     * Returns the list of items currently stored in the given NBT Items[] interface.
     * Preserves empty slots.
     * @param tagIn The tag holding the inventory contents
     * @param slotCount the maximum number of slots, and thus also the size of the list to create
     * @return
     */
    public static DefaultedList<ItemStack> getNbtItems(NbtCompound tagIn, int slotCount)
    {
        if (tagIn.contains("Items", Constants.NBT.TAG_LIST))
        {
            NbtList tagList = tagIn.getList("Items", Constants.NBT.TAG_COMPOUND);
            final int count = tagList.size();
            int maxSlot = -1;

            if (slotCount <= 0)
            {
                for (int i = 0; i < count; ++i)
                {
                    NbtCompound nbt = tagList.getCompound(i);
                    int slot = nbt.getByte("Slot");

                    if (slot > maxSlot)
                    {
                        maxSlot = slot;
                    }
                }

                slotCount = maxSlot + 1;
            }

            DefaultedList<ItemStack> items = DefaultedList.ofSize(slotCount, ItemStack.EMPTY);

            for (int i = 0; i < count; ++i)
            {
                NbtCompound nbt = tagList.getCompound(i);
                ItemStack stack = ItemStack.fromNbtOrEmpty(MinecraftClient.getInstance().world.getRegistryManager(), nbt);
                int slot = nbt.getByte("Slot");

                if (slot >= 0 && slot < items.size() && stack.isEmpty() == false)
                {
                    items.set(slot, stack);
                }
            }

            return items;
        }

        return DefaultedList.of();
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Does not keep empty slots.
     * @param stackIn The item holding the inventory contents
     * @return
     */
    public static DefaultedList<ItemStack> getStoredItems(ItemStack stackIn)
    {
        ContainerComponent container = stackIn.getComponents().get(DataComponentTypes.CONTAINER);

        if (container != null)
        {
            Iterator<ItemStack> iter = container.streamNonEmpty().iterator();
            DefaultedList<ItemStack> items = DefaultedList.ofSize((int) container.streamNonEmpty().count());

            // Using 'container.copyTo(items)' will break Litematica's Material List
            while (iter.hasNext())
            {
                items.add(iter.next());
            }

            return items;
        }

        return DefaultedList.of();
    }

    /**
     * Returns the list of items currently stored in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * Preserves empty slots.
     * @param stackIn The item holding the inventory contents
     * @param slotCount the maximum number of slots, and thus also the size of the list to create
     * @return
     */
    public static DefaultedList<ItemStack> getStoredItems(ItemStack stackIn, int slotCount)
    {
        ContainerComponent itemContainer = stackIn.getComponents().get(DataComponentTypes.CONTAINER);

        // Using itemContainer.copyTo() does not preserve empty stacks.  Iterator again.
        if (itemContainer != null)
        {
            long defSlotCount = itemContainer.stream().count();

            // ContainerComponent.MAX_SLOTS = 256; (private)
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
                if (iter.hasNext())
                {
                    items.add(iter.next());
                }
                else
                {
                    items.add(ItemStack.EMPTY);
                }
            }

            return items;
        }
        else
        {
            return DefaultedList.of();
        }
    }

    // Same code as above, but for BUNDLE_CONTENTS, such as for the Materials List under Litematica.
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
     * @param stack
     * @return
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
     * @param stack
     * @return
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
     * @param stackIn
     * @return
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
                    items.add(slot);
                }
            }

            return items;
        }

        return DefaultedList.of();
    }

    /**
     * Returns a list of ItemStacks from the Bundle.  Preserves Empty Stacks up to maxSlots.
     * @param stackIn
     * @param maxSlots
     * @return
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
                items.add(iter.next());
                limit++;
            }

            return items;
        }

        return DefaultedList.of();
    }

    /**
     * Returns a map of the stored item counts in the given Shulker Box
     * (or other storage item with the same NBT data structure).
     * @param stackShulkerBox
     * @return
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
     * @param inv
     * @return
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
     * Returns the given list of items wrapped as an InventoryBasic
     * @param items
     * @return
     */
    public static Inventory getAsInventory(DefaultedList<ItemStack> items)
    {
        SimpleInventory inv = new SimpleInventory(items.size());

        for (int slot = 0; slot < items.size(); ++slot)
        {
            inv.setStack(slot, items.get(slot));
        }

        return inv;
    }

    /**
     * Creates an ItemStack via a String
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
     * @param itemNameIn (String containing the item name)
     * @param data (ComponentMap data to import)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn, ComponentMap data)
    {
        return getItemStackFromString(itemNameIn, -1, data);
    }

    /**
     * Creates an ItemStack via a String
     * @param itemNameIn (String containing the item name)
     * @param count (How many in this stack)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn, int count)
    {
        return getItemStackFromString(itemNameIn, count, ComponentMap.EMPTY);
    }

    /**
     * Creates an ItemStack via a String
     * @param itemNameIn (String containing the item name)
     * @param count (How many in this stack)
     * @param data (ComponentMap data to import)
     * @return (The ItemStack object or ItemStack.EMPTY, aka Air)
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemNameIn, int count, ComponentMap data)
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

                //registries.getOptionalEntry(RegistryKeys.ITEM).getmatchesId(itemId);

                Optional<RegistryEntry.Reference<Item>> opt = Registries.ITEM.get(itemId);

                if (opt.isPresent())
                {
                    //Item item = Registries.ITEM.get(itemId);
                    //RegistryEntry<Item> itemEntry = RegistryEntry.of(item);

                    RegistryEntry<Item> itemEntry = opt.get();

                    if (itemEntry.hasKeyAndValue())
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
                }
                else
                {
                    MaLiLib.logger.warn(StringUtils.translate("malilib.error.invalid_item_stack_entry.string", itemName));
                }
            }
        }

        return null;
    }

    /**
     * Create ItemStack from a string, using a Data Components aware method, wrapping the Vanilla ItemStringReader method
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
            //MaLiLib.logger.warn("getItemStackFromString(): Invalid NBT Syntax");
            MaLiLib.logger.warn(StringUtils.translate("malilib.error.invalid_item_stack_entry.nbt_syntax", stringIn));
            return null;
        }

        ItemStack stackOut = new ItemStack(results.item());
        stackOut.applyChanges(results.components());

        return stackOut;
    }
}
