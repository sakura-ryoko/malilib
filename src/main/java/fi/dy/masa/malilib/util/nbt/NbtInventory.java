package fi.dy.masa.malilib.util.nbt;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import com.mojang.serialization.DynamicOps;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.ListData;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.log.AnsiLogger;

/**
 * This makes reading / Writing Inventories to / from NBT (or Data Tag) a piece of cake.
 * Supports Inventory, Nbt, Data Tag, or DefaultList<> interfaces; and uses the newer Mojang
 * 'StackWithSlot' system.<br>
 * We will use our own `EntrySlot` system.
 */
public class NbtInventory implements AutoCloseable
{
    private static final AnsiLogger LOGGER = new AnsiLogger(NbtInventory.class, true, true);
//    public static final Comparator<ItemStackWithSlot> SLOT_COMPARATOR = new StackWithSlotComparator();
    public static final Comparator<EntrySlot> COMPARATOR = new EntrySlotComparator();
    public static final int VILLAGER_SIZE = 8;
    public static final int DEFAULT_SIZE = 27;
    public static final int PLAYER_SIZE = 36;
    public static final int DOUBLE_SIZE = 54;
    public static final int MAX_SIZE = 256;
    private HashSet<EntrySlot> items;

    private NbtInventory() {}

    /**
     * Create a new blank {@link NbtInventory} of the size specified.
     *
     * @param size -
     * @return -
     */
    public static NbtInventory create(int size)
    {
        NbtInventory newInv = new NbtInventory();
        //LOGGER.info("init() size: [{}]", size);
        size = getAdjustedSize(Mth.clamp(size, 1, MAX_SIZE));
        newInv.buildEmptyList(size);
        return newInv;
    }

    /**
     * Common Function to try to get the "corrected" Inventory size based on
     * an existing `list.size()` for example.
     * <br>
     *
     * @param size The Size to adjust.
     * @return The Adjusted Size.
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

    private void buildEmptyList(int size) throws RuntimeException
    {
        if (this.items != null)
        {
            throw new RuntimeException("List not empty!");
        }

        this.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            this.items.add(new EntrySlot(i, ItemStack.EMPTY));
        }
    }

    /**
     * This exists because an NBT List can have empty slots not accounted for in the middle of its current size;
     * Such as an empty slot in the middle of a Hopper Minecart.  This code fixes this problem.
     * @param slotsUsed ()
     */
    private void verifySize(List<Integer> slotsUsed, int maxSlot)
    {
        int size = MathUtils.max(this.size(), maxSlot);

        size = getAdjustedSize(size);

        for (int i = 0; i < size; i++)
        {
            if (!slotsUsed.contains(i))
            {
                //LOGGER.info("verifySize(): [{}]: found unused slot Number; adding Empty slot...", i);
//                this.items.add(new ItemStackWithSlot(i, ItemStack.EMPTY));
                this.items.add(new EntrySlot(i, ItemStack.EMPTY));
            }
        }
    }

    /**
     * Resort this {@link NbtInventory} by Slot ID.
     */
    public NbtInventory sorted()
    {
        if (this.size() > 0)
        {
            List<EntrySlot> sorted = new ArrayList<>(this.items);
            sorted.sort(COMPARATOR);
            this.items.clear();
            this.items.addAll(sorted);
        }

        return this;
    }

    /**
     * Return if this NbtInventory is Empty
     * @return -
     */
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

    /**
     * Return this NbtInventory size
     * @return -
     */
    public int size()
    {
        if (this.items == null)
        {
            return -1;
        }

        return this.items.size();
    }

    /**
     * Return this Inventory as a {@link NonNullList}
     *
     * @return -
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
     * Create a new {@link NbtInventory} from a {@link NonNullList}; making all the slot numbers the stack index.
     *
     * @param list -
     * @return -
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
            EntrySlot slot = new EntrySlot(i, list.get(i));
            //LOGGER.info("fromVanillaList():[{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
            newInv.items.add(slot);
        }

        return newInv;
    }

    /**
     * Convert this Inventory to a Vanilla {@link Container} object.
     * Supports oversized Inventories (MAX_SIZE) and DoubleInventory (DOUBLE_SIZE); or defaults to (DEFAULT_SIZE)
     *
     * @return -
     */
    public @Nullable Container toInventory(final int size)
    {
        if (this.isEmpty())
        {
            return null;
        }

        int sizeAdj = getAdjustedSize(Math.clamp(size, this.size(), MAX_SIZE));
        Container inv = new SimpleContainer(sizeAdj);

        //LOGGER.warn("toInventory(): sizeAdj [{}] -> inv size [{}]", sizeAdj, inv.getContainerSize());
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
     * Creates a new {@link NbtInventory} from a vanilla Inventory object; making all the slot numbers the stack index.
     *
     * @param inv -
     * @return -
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
            EntrySlot slot = new EntrySlot(i, inv.getItem(i));
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
     * @param registry RegistryAccess object
     * @return -
     * @implNote This is used after 1.21.6
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
     * @param view -
     * @param size -
     * @return -
     * @implNote This is used after 1.21.6
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
     * Converts the first {@link NbtInventory} element to a single {@link CompoundTag}.
     *
     * @return -
     * @throws RuntimeException -
     */
    public CompoundTag toNbtSingle(@Nonnull RegistryAccess registry) throws RuntimeException
    {
        if (this.size() > 1)
        {
            throw new RuntimeException("Inventory is too large for a single entry!");
        }

        EntrySlot slot = this.items.stream().findFirst().orElseThrow();

        if (!slot.stack().isEmpty())
        {
//            Tag element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(NbtOps.INSTANCE), slot).getPartialOrThrow();
            CompoundTag data = slot.toNbt(registry);
            //LOGGER.info("toNbtSingle(): --> nbt: [{}]", data.toString());
            return data;
        }

        return new CompoundTag();
    }

    /**
     * Converts the first {@link NbtInventory} element to a single {@link CompoundData}.
     *
     * @return -
     * @throws RuntimeException -
     */
	public CompoundData toDataSingle(@Nonnull RegistryAccess registry)
	{
        if (this.size() > 1)
        {
            throw new RuntimeException("Inventory is too large for a single entry!");
        }

        EntrySlot slot = this.items.stream().findFirst().orElseThrow();

        if (!slot.stack().isEmpty())
        {
//            BaseData element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(DataOps.INSTANCE), slot).getPartialOrThrow();
            CompoundData data = slot.toData(registry);
            //LOGGER.info("toDataSingle(): --> nbt: [{}]", data.toString());
            return data;
        }

        return new CompoundData();
	}

    /**
     * Converts this {@link NbtInventory} to a basic {@link ListTag} with Slot information.
     *
     * @return -
     * @throws RuntimeException -
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
//                        Tag element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(NbtOps.INSTANCE), slot).getPartialOrThrow();
                        Tag element = slot.toNbt(registry);
                        //LOGGER.info("toNbtList(): slot [{}] --> nbt: [{}]", slot.slot(), element.toString());
                        nbt.add(element);
                    }
                }
        );

        return nbt;
    }

    /**
     * Converts this {@link NbtInventory} to a basic {@link ListData} with Slot information.
     *
     * @return -
     * @throws RuntimeException -
     */
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
//                        BaseData element = ItemStackWithSlot.CODEC.encodeStart(registry.createSerializationContext(DataOps.INSTANCE), slot).getPartialOrThrow();
                        CompoundData data = slot.toData(registry);
                        //LOGGER.info("toDataList(): slot [{}] --> nbt: [{}]", slot.slot(), data.toString());
                        list.add(data);
                    }
                }
        );

        return list;
	}

    /**
     * Writes this {@link NbtInventory} to a Data Type (List or Compound) using a key; with slot information.
     *
     * @param type -
     * @param key  -
     * @return -
     * @throws RuntimeException -
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

    /**
     * Writes this {@link NbtInventory} to a Data Type (List or Compound) using a key; with slot information.
     *
     * @param type -
     * @param key  -
     * @return -
     * @throws RuntimeException -
     */
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
     * Creates a new {@link NbtInventory} from a Data Type (List or Compound) using a key; retains slot information.
     *
     * @param nbtIn    -
     * @param key      The Key of the Data to read
     * @param noSlotId If the List doesn't include Slots, generate them using inventory index
     * @return -
     * @throws RuntimeException -
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

    /**
     * Creates a new {@link NbtInventory} from a Data Type (List or Compound) using a key; retains slot information.
     *
     * @param data     -
     * @param key      The Key of the Data to read
     * @param noSlotId If the List doesn't include Slots, generate them using inventory index
     * @return -
     * @throws RuntimeException -
     */
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
     * Creates a new {@link NbtInventory} from a single-member {@link CompoundData} containing a single item with a slot number.
     *
     * @param tag -
     * @return -
     * @throws RuntimeException -
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
//        ItemStackWithSlot slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt).getPartialOrThrow();
        EntrySlot slot = EntrySlot.fromNbt(nbt, registry);
        //LOGGER.info("fromNbtSingle(): slot [{}], stack: [{}]", slot.slot(), slot.stack().toString());
        newInv.items.add(slot);

        return newInv;
    }

    /**
     * Creates a new {@link NbtInventory} from a single-member {@link CompoundData} containing a single item with a slot number.
     *
     * @param data -
     * @return -
     * @throws RuntimeException -
     */
	public static @Nullable NbtInventory fromDataSingle(@Nonnull CompoundData data, @Nonnull RegistryAccess registry) throws RuntimeException
	{
        if (data.isEmpty())
        {
            return null;
        }

        NbtInventory newInv = new NbtInventory();
        CompoundData tag = checkDataForIDOverrides(data);

        newInv.items = new HashSet<>();
//        ItemStackWithSlot slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(DataOps.INSTANCE), tag).getPartialOrThrow();
        EntrySlot slot = EntrySlot.fromData(tag, registry);
        //LOGGER.info("fromNbtSingle(): slot [{}], stack: [{}]", slot.slot(), slot.stack().toString());
        newInv.items.add(slot);

        return newInv;
	}

    /**
     * Creates a new {@link NbtInventory} from an {@link ListTag}; utilizing Slot information.
     *
     * @param list     -
     * @param noSlotId If the List doesn't include Slots, generate them using inventory index
     * @return -
     * @throws RuntimeException -
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
//            ItemStackWithSlot slot;
            EntrySlot slot;

            // Some lists, such as the "Inventory" tag does not include slot ID's
            if (noSlotId)
            {
//                slot = new ItemStackWithSlot(i, ItemStack.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), tag).getPartialOrThrow());
                slot = EntrySlot.fromNbt(tag, registry);
                slot.setSlot(i);
            }
            else
            {
//                slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), tag).getPartialOrThrow();
                slot = EntrySlot.fromNbt(tag, registry);
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

    /**
     * Creates a new {@link NbtInventory} from an {@link ListData}; utilizing Slot information.
     *
     * @param list     -
     * @param noSlotId If the List doesn't include Slots, generate them using inventory index
     * @return -
     * @throws RuntimeException -
     */
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
//            ItemStackWithSlot slot;
            EntrySlot slot;

            // Some lists, such as the "Inventory" tag does not include slot ID's
            if (noSlotId)
            {
//                slot = new ItemStackWithSlot(i, ItemStack.CODEC.parse(registry.createSerializationContext(DataOps.INSTANCE), tag).getPartialOrThrow());
                slot = EntrySlot.fromData(tag, registry);
                slot.setSlot(i);
            }
            else
            {
//                slot = ItemStackWithSlot.CODEC.parse(registry.createSerializationContext(DataOps.INSTANCE), tag).getPartialOrThrow();
                slot = EntrySlot.fromData(tag, registry);
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
     * Primarily for Broken NBT (Item ID) situations where the Server
     * might not be equal in version over ViaVersion, and the like.
     * Such problems arise under the DataInventory.
     *
     * @param in -
     * @return -
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
     * Primarily for Broken NBT (Item ID) situations where the Server
     * might not be equal in version over ViaVersion, and the like.
     * Such problems arise under the DataInventory.
     *
     * @param in -
     * @return -
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
     * InventoryView compat
     * @return -
     */
    public int getSize()
    {
        return this.size();
    }

    /**
     * InventoryView compat
     * @return -
     */
    public ItemStack getStack(int slot)
    {
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);

        this.items.forEach(
                entry ->
                {
                    if (entry.slot() == slot)
                    {
                        result.set(entry.stack().copy());
                    }
                });

        return result.get();
    }

    @VisibleForTesting
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

    /**
     * Equivalence with <b>ItemStackWithSlot</b> from ~1.21.8+
     */
    public static class EntrySlot
    {
        private final ItemStack stack;
        private int slot;

        public EntrySlot(int slot, ItemStack stack)
        {
            this.slot = slot;
            this.stack = stack.copy();
        }

        public void setSlot(int slot)
        {
            this.slot = slot;
        }

        public int slot() {return this.slot;}

        public ItemStack stack() {return this.stack;}

        public CompoundData toData(@Nonnull RegistryAccess registry)
        {
            CompoundData data;
            DynamicOps<BaseData> ops = registry.createSerializationContext(DataOps.INSTANCE);

            try
            {
                data = (CompoundData) ItemStack.CODEC.encodeStart(ops, this.stack).getOrThrow();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("EntrySlot#toData: Exception Serializing Item: [{}]; {}", this.stack.getItemName().getString(), e.getLocalizedMessage());
                final String id = this.stack.getItemName().getString();
                final Component text = id.isEmpty()
                                       ? StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable")
                                       : StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable.id", id);
                ItemLore lore = new ItemLore(List.of(text));
                DataComponentPatch.Builder builder = DataComponentPatch.builder();
                builder.set(DataComponents.LORE, lore);
                ItemStack fallback = new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(Items.BARRIER), 1, builder.build());
                data = (CompoundData) ItemStack.CODEC.encodeStart(ops, fallback).getOrThrow();
            }

            data.putByte(NbtKeys.SLOT, (byte) this.slot);
            return data;
        }

        public static EntrySlot fromData(CompoundData data, @Nonnull RegistryAccess registry)
        {
            final int slot = data.getByteOrDefault(NbtKeys.SLOT, (byte) 0) & 0xFF;
            ItemStack stack;

            try
            {
                stack = ItemStack.CODEC.parse(registry.createSerializationContext(DataOps.INSTANCE), data).getOrThrow();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("EntrySlot#fromData: Exception Deserializing Item: [{}]; {}", data.toString(), e.getLocalizedMessage());
                final String id = data.getStringOrDefault(NbtKeys.ID, "");
                final Component text = id.isEmpty()
                                       ? StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable")
                                       : StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable.id", id);
                ItemLore lore = new ItemLore(List.of(text));
                DataComponentPatch.Builder builder = DataComponentPatch.builder();
                builder.set(DataComponents.LORE, lore);
                ItemStack fallback = new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(Items.BARRIER), 1, builder.build());
                stack = fallback.copy();
            }

            return new EntrySlot(slot, stack.copy());
        }

        public CompoundTag toNbt(@Nonnull RegistryAccess registry)
        {
            CompoundTag nbt;
            DynamicOps<Tag> ops = registry.createSerializationContext(NbtOps.INSTANCE);

            try
            {
                nbt = (CompoundTag) ItemStack.CODEC.encodeStart(ops, this.stack).getOrThrow();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("EntrySlot#toNbt: Exception Serializing Item: [{}]; {}", this.stack.getItemName().getString(), e.getLocalizedMessage());
                final String id = this.stack.getItemName().getString();
                final Component text = id.isEmpty()
                                       ? StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable")
                                       : StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable.id", id);
                ItemLore lore = new ItemLore(List.of(text));
                DataComponentPatch.Builder builder = DataComponentPatch.builder();
                builder.set(DataComponents.LORE, lore);
                ItemStack fallback = new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(Items.BARRIER), 1, builder.build());
                nbt = (CompoundTag) ItemStack.CODEC.encodeStart(ops, fallback).getOrThrow();
            }

            nbt.putByte(NbtKeys.SLOT, (byte) this.slot);
            return nbt;
        }

        public static EntrySlot fromNbt(CompoundTag nbt, @Nonnull RegistryAccess registry)
        {
            final int slot = nbt.getByteOr(NbtKeys.SLOT, (byte) 0) & 0xFF;
            ItemStack stack;

            try
            {
                stack = ItemStack.CODEC.parse(registry.createSerializationContext(NbtOps.INSTANCE), nbt).getOrThrow();
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("EntrySlot#fromNbt: Exception Deserializing Item: [{}]; {}", nbt.toString(), e.getLocalizedMessage());
                final String id = nbt.getStringOr(NbtKeys.ID, "");
                final Component text = id.isEmpty()
                                       ? StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable")
                                       : StringUtils.translateAsText("malilib.gui.tooltip.nbt.unparsable.id", id);
                ItemLore lore = new ItemLore(List.of(text));
                DataComponentPatch.Builder builder = DataComponentPatch.builder();
                builder.set(DataComponents.LORE, lore);
                ItemStack fallback = new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(Items.BARRIER), 1, builder.build());
                stack = fallback.copy();
            }

            return new EntrySlot(slot, stack.copy());
        }
    }

    public static class StackWithSlotComparator implements Comparator<ItemStackWithSlot>
    {
        @Override
        public int compare(ItemStackWithSlot o1, ItemStackWithSlot o2)
        {
            return Integer.compare(o1.slot(), o2.slot());
        }
    }

    public static class EntrySlotComparator implements Comparator<EntrySlot>
    {
        @Override
        public int compare(EntrySlot o1, EntrySlot o2)
        {
            return Integer.compare(o1.slot(), o2.slot());
        }
    }
}
