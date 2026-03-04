package fi.dy.masa.malilib.util.nbt;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.log.AnsiLogger;

/**
 * This makes reading / Writing Inventories to / from NBT (or Data Tag) a piece of cake.
 * Supports Inventory, Nbt, Data Tag, or DefaultList<> interfaces; and uses the newer Mojang
 * 'StackWithSlot' system.
 */
public class NbtInventory implements AutoCloseable
{
    private static final AnsiLogger LOGGER = new AnsiLogger(NbtInventory.class, true, true);
    public static final Comparator<ItemStackWithSlot> COMPARATOR = new StackWithSlotComparator();
    public static final int VILLAGER_SIZE = 8;
    public static final int DEFAULT_SIZE = 27;
    public static final int PLAYER_SIZE = 36;
    public static final int DOUBLE_SIZE = 54;
    public static final int MAX_SIZE = 256;
    private HashSet<ItemStackWithSlot> items;

    private NbtInventory() {}

    public static NbtInventory create(int size)
    {
        NbtInventory newInv = new NbtInventory();

        //LOGGER.info("init() size: [{}]", size);
        size = getAdjustedSize(Mth.clamp(size, 1, MAX_SIZE));
        newInv.buildEmptyList(size);

        return newInv;
    }

    private void buildEmptyList(int size) throws RuntimeException
    {
        if (this.items != null)
        {
            throw new RuntimeException("List not empty!");
        }

        this.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            this.items.add(new ItemStackWithSlot(i, ItemStack.EMPTY));
        }
    }

    /**
     * Resort this {@link NbtInventory} by Slot ID.
     */
    public NbtInventory sorted()
    {
        if (this.size() > 0)
        {
            List<ItemStackWithSlot> sorted = new ArrayList<>(this.items);
            sorted.sort(COMPARATOR);
            this.items.clear();
            this.items.addAll(sorted);
        }

        return this;
    }

    public boolean isEmpty()
    {
        if (this.items == null || this.items.isEmpty())
        {
            return true;
        }

        AtomicBoolean bool = new AtomicBoolean(true);

        this.items.forEach(
                (slot) ->
                {
                    if (!slot.stack().isEmpty())
                    {
                        bool.set(false);
                    }
                }
        );

        return bool.get();
    }

    public int size()
    {
        if (this.items == null)
        {
            return -1;
        }

        return this.items.size();
    }

    /**
     * Return this Inventory as a DefaultList<ItemStack>
     * @return ()
     */
    public NonNullList<@NotNull ItemStack> toVanillaList(int size)
    {
        if (this.isEmpty())
        {
            return NonNullList.create();
        }

        size = getAdjustedSize(Math.clamp(size, this.size(), MAX_SIZE));

        NonNullList<@NotNull ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);
        AtomicInteger i = new AtomicInteger(0);

        this.items.forEach(
                (slot) ->
                    {
                        list.set(slot.slot(), slot.stack());
                        //LOGGER.info("toVanillaList():[{}]: slot [{}], stack: [{}]", i.get(), slot.slot(), slot.stack().toString());
                        i.getAndIncrement();
                    }
        );

        return list;
    }

    /**
     * Create a new NbtInventory from a DefaultedList<ItemStack>; making all the slot numbers the stack index.
     * @param list ()
     * @return ()
     */
    public static @Nullable NbtInventory fromVanillaList(@Nonnull NonNullList<@NotNull ItemStack> list)
    {
        int size = list.size();

        if (size < 1)
        {
            return null;
        }

        size = getAdjustedSize(Mth.clamp(size, 1, MAX_SIZE));
        NbtInventory newInv = new NbtInventory();
        newInv.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            ItemStackWithSlot slot = new ItemStackWithSlot(i, list.get(i));
            //LOGGER.info("fromVanillaList():[{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
            newInv.items.add(slot);
        }

        return newInv;
    }

    /**
     * Convert this Inventory to a Vanilla Inventory object.
     * Supports oversized Inventories (MAX_SIZE) and DoubleInventory (DOUBLE_SIZE); or defaults to (DEFAULT_SIZE)
     * @return ()
     */
    public @Nullable Container toInventory(final int size)
    {
        if (this.isEmpty())
        {
            return null;
        }

        int sizeAdj = getAdjustedSize(Math.clamp(size, this.size(), MAX_SIZE));
        Container inv = new SimpleContainer(sizeAdj);

        //LOGGER.warn("toInventory(): sizeAdj [{}] -> inv size [{}]", sizeAdj, inv.size());
        AtomicInteger i = new AtomicInteger(0);

        this.items.forEach(
                (slot) ->
                {
                    //LOGGER.info("toInventory():[{}]: slot [{}], stack: [{}]", i.get(), slot.slot(), slot.stack().toString());
                    inv.setItem(slot.slot(), slot.stack());
                    i.getAndIncrement();
                }
        );

        return inv;
    }

    /**
     * Creates a new NbtInventory from a vanilla Inventory object; making all the slot numbers the stack index.
     * @param inv ()
     * @return ()
     */
    public static NbtInventory fromInventory(@Nonnull Container inv)
    {
        NbtInventory newInv = new NbtInventory();
        List<Integer> slotsUsed = new ArrayList<>();
        int size = inv.getContainerSize();
        int maxSlot = 0;

        size = getAdjustedSize(Mth.clamp(size, 1, MAX_SIZE));
        newInv.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            ItemStackWithSlot slot = new ItemStackWithSlot(i, inv.getItem(i));
            //LOGGER.info("fromInventory():[{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
            newInv.items.add(slot);
            slotsUsed.add(slot.slot());

            if (slot.slot() > maxSlot)
            {
                maxSlot = slot.slot();
            }
        }

        newInv.verifySize(slotsUsed, maxSlot);

        return newInv;
    }

    /**
     * Uses the newer Vanilla 'WriterView' interface to write this Inventory to it; using our 'NbtView' wrapper.
     * @param registry ()
     * @return ()
     */
    public @Nullable NbtView toNbtWriterView(@Nonnull RegistryAccess registry)
    {
        if (this.isEmpty())
        {
            return null;
        }

        final int size = getAdjustedSize(this.size());

        NbtView view = NbtView.getWriter(registry);
        NonNullList<@NotNull ItemStack> list = this.toVanillaList(size);

        ContainerHelper.saveAllItems(Objects.requireNonNull(view.getWriter()), list);

        return view;
    }

    /**
     * Uses the newer Vanilla 'ReaderView' interface to create a new NbtInventory; using our 'NbtView' wrapper.
     * @param view ()
     * @param size ()
     * @return ()
     */
    public static @Nullable NbtInventory fromNbtReaderView(@Nonnull NbtView view, int size)
    {
        if (size < 1)
        {
            return null;
        }

        size = getAdjustedSize(Mth.clamp(size, 1, MAX_SIZE));
        NonNullList<@NotNull ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);

        ContainerHelper.loadAllItems(Objects.requireNonNull(view.getReader()), list);
        return fromVanillaList(list);
    }

    /**
     * Converts the first Inventory element to a single NbtCompound.
     * @return ()
     * @throws RuntimeException ()
     */
    public CompoundTag toNbtSingle(@Nonnull RegistryAccess registry) throws RuntimeException
    {
        if (this.size() > 1)
        {
            throw new RuntimeException("Inventory is too large for a single entry!");
        }

        ItemStackWithSlot slot = this.items.stream().findFirst().orElseThrow();

        if (!slot.stack().isEmpty())
        {
            Tag element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(NbtOps.INSTANCE), slot).getPartialOrThrow();
//            LOGGER.info("toNbtSingle(): --> nbt: [{}]", element.toString());
            return (CompoundTag) element;
        }

        return new CompoundTag();
    }

	public CompoundData toDataSingle(@Nonnull RegistryAccess registry)
	{
        if (this.size() > 1)
        {
            throw new RuntimeException("Inventory is too large for a single entry!");
        }

        ItemStackWithSlot slot = this.items.stream().findFirst().orElseThrow();

        if (!slot.stack().isEmpty())
        {
            BaseData element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(DataOps.INSTANCE), slot).getPartialOrThrow();
//            LOGGER.info("toDataSingle(): --> nbt: [{}]", element.toString());
            return (CompoundData) element;
        }

        return new CompoundData();
	}

    /**
     * Converts this Inventory to a basic NbtList with Slot information.
     * @return ()
     * @throws RuntimeException ()
     */
    public ListTag toNbtList(@Nonnull RegistryAccess registry) throws RuntimeException
    {
        ListTag nbt = new ListTag();

        if (this.isEmpty())
        {
            return nbt;
        }

        this.items.forEach(
                (slot) ->
                {
                    if (!slot.stack().isEmpty())
                    {
                        Tag element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(NbtOps.INSTANCE), slot).getPartialOrThrow();
                        //LOGGER.info("toNbtList(): slot [{}] --> nbt: [{}]", slot.slot(), element.toString());
                        nbt.add(element);
                    }
                }
        );

        return nbt;
    }

	public ListData toDataList(@Nonnull RegistryAccess registry)
	{
        ListData list = new ListData();

        if (this.isEmpty())
        {
            return list;
        }

        this.items.forEach(
                (slot) ->
                {
                    if (!slot.stack().isEmpty())
                    {
                        BaseData element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(DataOps.INSTANCE), slot).getPartialOrThrow();
                        //LOGGER.info("toDataList(): slot [{}] --> nbt: [{}]", slot.slot(), element.toString());
                        list.add(element);
                    }
                }
        );

        return list;
	}

    /**
     * Writes this Inventory to a Nbt Type (List or Compound) using a key; with slot information.
     * @param type ()
     * @param key ()
     * @return ()
     * @throws RuntimeException ()
     */
    public CompoundTag toNbt(TagType<?> type, String key, @Nonnull RegistryAccess registry) throws RuntimeException
    {
        CompoundTag nbt = new CompoundTag();

        if (type == ListTag.TYPE)
        {
            ListTag list = this.toNbtList(registry);

            if (list.isEmpty())
            {
                return nbt;
            }

            nbt.put(key, list);

            return nbt;
        }
        else if (type == CompoundTag.TYPE)
        {
            nbt.put(key, this.toNbtSingle(registry));

            return nbt;
        }

        throw new RuntimeException("Unsupported Nbt Type!");
    }

	@ApiStatus.Experimental
	public CompoundData toData(int type, String key, @Nonnull RegistryAccess registry) throws RuntimeException
	{
		CompoundData data = new CompoundData();

		if (type == Constants.NBT.TAG_LIST)
		{
			ListData list = this.toDataList(registry);

			if (list.isEmpty())
			{
				return data;
			}

			return data.put(key, list);
		}
		else if (type == Constants.NBT.TAG_COMPOUND)
		{
			return data.put(key, this.toDataSingle(registry));
		}

		throw new RuntimeException("Unsupported Data Type!");
	}

    /**
     * Creates a new NbtInventory from a Nbt Type (List or Compound) using a key; retains slot information.
     * @param nbtIn ()
     * @param key ()
     * @param noSlotId (If the List doesn't include Slots, generate them using inventory index)
     * @return ()
     * @throws RuntimeException ()
     */
    public static @Nullable NbtInventory fromNbt(@Nonnull CompoundTag nbtIn, String key, boolean noSlotId, @Nonnull RegistryAccess registry) throws RuntimeException
    {
        if (nbtIn.isEmpty() || !nbtIn.contains(key))
        {
            return null;
        }

        if (Objects.requireNonNull(nbtIn.get(key)).getType() == ListTag.TYPE)
        {
            return fromNbtList(nbtIn.getListOrEmpty(key), noSlotId, registry);
        }
        else if (Objects.requireNonNull(nbtIn.get(key)).getType() == CompoundTag.TYPE)
        {
            return fromNbtSingle(nbtIn.getCompoundOrEmpty(key), registry);
        }
        else
        {
            throw new RuntimeException("Invalid Nbt Type!");
        }
    }

	@ApiStatus.Experimental
	public static @Nullable NbtInventory fromData(@Nonnull CompoundData data, String key, boolean noSlotId, @Nonnull RegistryAccess registry) throws RuntimeException
	{
		if (data.isEmpty() || !data.containsLenient(key))
		{
			return null;
		}

		if (data.contains(key, Constants.NBT.TAG_LIST))
		{
			return fromDataList(data.getList(key), noSlotId, registry);
		}
		else if (data.contains(key, Constants.NBT.TAG_COMPOUND))
		{
			return fromDataSingle(data.getCompound(key), registry);
		}
		else
		{
			throw new RuntimeException("Invalid Data Type!");
		}
	}

    /**
     * Creates a new NbtInventory from a single-member NbtCompound containing a single item with a slot number.
     * @param tag ()
     * @return ()
     * @throws RuntimeException ()
     */
    public static @Nullable NbtInventory fromNbtSingle(@Nonnull CompoundTag tag, @Nonnull RegistryAccess registry) throws RuntimeException
    {
        if (tag.isEmpty())
        {
            return null;
        }

        NbtInventory newInv = new NbtInventory();
        CompoundTag nbt = checkNbtForIDOverrides(tag);

        newInv.items = new HashSet<>();
        ItemStackWithSlot slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt).getPartialOrThrow();
//        LOGGER.info("fromNbtSingle(): slot [{}], stack: [{}]", slot.slot(), slot.stack().toString());
        newInv.items.add(slot);

        return newInv;
    }

	@ApiStatus.Experimental
	public static @Nullable NbtInventory fromDataSingle(@Nonnull CompoundData data, @Nonnull RegistryAccess registry) throws RuntimeException
	{
        if (data.isEmpty())
        {
            return null;
        }

        NbtInventory newInv = new NbtInventory();
        CompoundData tag = checkDataForIDOverrides(data);

        newInv.items = new HashSet<>();
        ItemStackWithSlot slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(DataOps.INSTANCE), tag).getPartialOrThrow();
//        LOGGER.info("fromNbtSingle(): slot [{}], stack: [{}]", slot.slot(), slot.stack().toString());
        newInv.items.add(slot);

        return newInv;
	}

    /**
     * Creates a new NbtInventory from an NbtList; utilizing Slot information.
     * @param list ()
     * @param noSlotId (If the List doesn't include Slots, generate them using inventory index)
     * @return ()
     * @throws RuntimeException ()
     */
    public static @Nullable NbtInventory fromNbtList(@Nonnull ListTag list, boolean noSlotId, @Nonnull RegistryAccess registry)
            throws RuntimeException
    {
        if (list.isEmpty())
        {
            return null;
        }
        else if (list.size() > MAX_SIZE)
        {
            throw new RuntimeException("Nbt List is too large!");
        }

        int size = list.size();
        size = getAdjustedSize(Mth.clamp(size, 1, MAX_SIZE));
        NbtInventory newInv = new NbtInventory();
        List<Integer> slotsUsed = new ArrayList<>();
        int maxSlot = 0;

        newInv.items = new HashSet<>();
        //LOGGER.info("fromNbtList(): listSize: [{}], invSize: [{}]", list.size(), size);

        for (int i = 0; i < list.size(); i++)
        {
            CompoundTag tag = checkNbtForIDOverrides((CompoundTag) list.get(i));
            ItemStackWithSlot slot;

            // Some lists, such as the "Inventory" tag does not include slot ID's
            if (noSlotId)
            {
                slot = new ItemStackWithSlot(i, ItemStack.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), tag).getPartialOrThrow());
            }
            else
            {
                slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), tag).getPartialOrThrow();
            }

            //LOGGER.info("fromNbtList(): [{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
            newInv.items.add(slot);
            slotsUsed.add(slot.slot());

            if (slot.slot() > maxSlot)
            {
                maxSlot = slot.slot();
            }
        }

        newInv.verifySize(slotsUsed, maxSlot);
//        newInv.dumpInv();

        return newInv;
    }

	@ApiStatus.Experimental
	public static @Nullable NbtInventory fromDataList(@Nonnull ListData list, boolean noSlotId, @Nonnull RegistryAccess registry)
            throws RuntimeException
	{
        if (list.isEmpty())
        {
            return null;
        }
        else if (list.size() > MAX_SIZE)
        {
            throw new RuntimeException("Data List is too large!");
        }

        int size = list.size();
        size = getAdjustedSize(Mth.clamp(size, 1, MAX_SIZE));
        NbtInventory newInv = new NbtInventory();
        List<Integer> slotsUsed = new ArrayList<>();
        int maxSlot = 0;

        newInv.items = new HashSet<>();
        //LOGGER.info("fromDataList(): listSize: [{}], invSize: [{}]", list.size(), size);

        for (int i = 0; i < list.size(); i++)
        {
            CompoundData tag = checkDataForIDOverrides(list.getCompoundAt(i));
            ItemStackWithSlot slot;

            // Some lists, such as the "Inventory" tag does not include slot ID's
            if (noSlotId)
            {
                slot = new ItemStackWithSlot(i, ItemStack.CODEC.parse(registry.createSerializationContext(DataOps.INSTANCE), tag).getPartialOrThrow());
            }
            else
            {
                slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(DataOps.INSTANCE), tag).getPartialOrThrow();
            }

            //LOGGER.info("fromDataList(): [{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
            newInv.items.add(slot);
            slotsUsed.add(slot.slot());

            if (slot.slot() > maxSlot)
            {
                maxSlot = slot.slot();
            }
        }

        newInv.verifySize(slotsUsed, maxSlot);
//        newInv.dumpInv();

        return newInv;
	}

    /**
     * Primarily for Broken NBT situations where the Server might be outdated over ViaVersion, and the like.
     * @param in ()
     * @return ()
     */
    private static CompoundData checkDataForIDOverrides(CompoundData in)
    {
        String id = in.getStringOrDefault(NbtKeys.ID, "");

        if (NbtOverrides.ID_OVERRIDES.containsKey(id))
        {
            id = NbtOverrides.ID_OVERRIDES.get(id);
            in.putString(NbtKeys.ID, id);
        }

        return in;
    }

    /**
     * Primarily for Broken NBT situations where the Server might be outdated over ViaVersion, and the like.
     * @param in ()
     * @return ()
     */
    private static CompoundTag checkNbtForIDOverrides(CompoundTag in)
    {
        String id = in.getStringOr(NbtKeys.ID, "");

        if (NbtOverrides.ID_OVERRIDES.containsKey(id))
        {
            id = NbtOverrides.ID_OVERRIDES.get(id);
            in.putString(NbtKeys.ID, id);
        }

        return in;
    }

    /**
     * This exists because an NBT List can have empty slots not accounted for in the middle of its current size;
     * Such as an empty slot in the middle of a Hopper Minecart.  This code fixes this problem.
     * @param slotsUsed ()
     */
    private void verifySize(List<Integer> slotsUsed, int maxSlot)
    {
        int size = Math.max(this.size(), maxSlot);

        size = getAdjustedSize(size);

        for (int i = 0; i < size; i++)
        {
            if (!slotsUsed.contains(i))
            {
                //LOGGER.info("verifySize(): [{}]: found unused slot Number; adding Empty slot...", i);
                this.items.add(new ItemStackWithSlot(i, ItemStack.EMPTY));
            }
        }
    }

    /**
     * Common Function to try to get the "corrected" Inventory size based on
     * an existing `list.size()` for example.
     * @param size ()
     * @return ()
     */
    public static int getAdjustedSize(int size)
    {
        //LOGGER.debug("getAdjustedSize(): sizeIn: [{}]", size);

        if (size <= VILLAGER_SIZE)
        {
            return size;
        }
        else if (size <= DEFAULT_SIZE)
        {
            return DEFAULT_SIZE;
        }
        else if (size <= PLAYER_SIZE)
        {
            return PLAYER_SIZE;
        }
        else if (size <= DOUBLE_SIZE)
        {
            return DOUBLE_SIZE;
        }
        else
        {
            return Math.min(size, MAX_SIZE);
        }
    }

    public void dumpInv()
    {
        AtomicInteger i = new AtomicInteger(0);
        LOGGER.info("dumpInv() --> START");

        this.items.forEach(
                (slot) ->
                {
                    LOGGER.info("[{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
                    i.getAndIncrement();
                }
        );

        LOGGER.info("dumpInv() --> END");
    }

    @Override
    public void close() throws Exception
    {
        this.items.clear();
    }

    public static class StackWithSlotComparator implements Comparator<ItemStackWithSlot>
    {
        @Override
        public int compare(ItemStackWithSlot o1, ItemStackWithSlot o2)
        {
            return Integer.compare(o1.slot(), o2.slot());
        }
    }
}
