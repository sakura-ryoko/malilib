package fi.dy.masa.malilib.util.nbt;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

import fi.dy.masa.malilib.util.log.AnsiLogger;

/**
 * This makes reading / Writing Inventories to / from NBT a piece of cake.
 * Supports Inventory, Nbt, or DefaultList<> interfaces; and uses the newer Mojang
 * 'StackWithSlot' system.
 */
public class NbtInventory implements AutoCloseable
{
    private static final AnsiLogger LOGGER = new AnsiLogger(NbtInventory.class, true, true);
    public static final int DEFAULT_SIZE = 27;
    public static final int DOUBLE_SIZE = 54;
    public static final int MAX_SIZE = 256;
    private HashSet<StackWithSlot> items;

    private NbtInventory() {}

    public static NbtInventory create(int size)
    {
        NbtInventory newInv = new NbtInventory();

        //LOGGER.info("init() size: [{}]", size);
        size = MathHelper.clamp(size, 1, MAX_SIZE);
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
            this.items.add(new StackWithSlot(i, ItemStack.EMPTY));
        }
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
    public DefaultedList<ItemStack> toVanillaList()
    {
        if (this.isEmpty())
        {
            return DefaultedList.of();
        }

        DefaultedList<ItemStack> list = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
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
    public static @Nullable NbtInventory fromVanillaList(@Nonnull DefaultedList<ItemStack> list)
    {
        int size = list.size();

        if (size < 1)
        {
            return null;
        }

        NbtInventory newInv = new NbtInventory();

        size = MathHelper.clamp(size, 1, MAX_SIZE);
        newInv.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            StackWithSlot slot = new StackWithSlot(i, list.get(i));
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
    public @Nullable Inventory toInventory()
    {
        if (this.isEmpty())
        {
            return null;
        }

        Inventory inv;

        if (this.size() > DOUBLE_SIZE)
        {
            inv = new SimpleInventory(MAX_SIZE);
        }
        else if (this.size() >= DEFAULT_SIZE && this.size() <= DOUBLE_SIZE)
        {
            inv = new DoubleInventory(new SimpleInventory(DEFAULT_SIZE), new SimpleInventory(DEFAULT_SIZE));
        }
        else
        {
            inv = new SimpleInventory(DEFAULT_SIZE);
        }

        AtomicInteger i = new AtomicInteger(0);

        this.items.forEach(
                (slot) ->
                {
                    //LOGGER.info("toInventory():[{}]: slot [{}], stack: [{}]", i.get(), slot.slot(), slot.stack().toString());
                    inv.setStack(slot.slot(), slot.stack());
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
    public static NbtInventory fromInventory(@Nonnull Inventory inv)
    {
        NbtInventory newInv = new NbtInventory();

        int size = inv.size();
        size = MathHelper.clamp(size, 1, MAX_SIZE);
        newInv.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            StackWithSlot slot = new StackWithSlot(i, inv.getStack(i));
            //LOGGER.info("fromInventory():[{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
            newInv.items.add(slot);
        }

        return newInv;
    }

    /**
     * Uses the newer Vanilla 'WriterView' interface to write this Inventory to it; using our 'NbtView' wrapper.
     * @param registry ()
     * @return ()
     */
    public @Nullable NbtView toNbtView(@Nonnull DynamicRegistryManager registry)
    {
        if (this.isEmpty())
        {
            return null;
        }

        NbtView view = NbtView.getWriter(registry);
        DefaultedList<ItemStack> list = this.toVanillaList();

        Inventories.writeData(Objects.requireNonNull(view.getWriter()), list);

        return view;
    }

    /**
     * Uses the newer Vanilla 'ReaderView' interface to create a new NbtInventory; using our 'NbtView' wrapper.
     * @param view ()
     * @param size ()
     * @return ()
     */
    public static @Nullable NbtInventory fromNbtView(@Nonnull NbtView view, int size)
    {
        if (size < 1)
        {
            return null;
        }

        size = MathHelper.clamp(size, 1, MAX_SIZE);
        DefaultedList<ItemStack> list = DefaultedList.ofSize(size, ItemStack.EMPTY);

        Inventories.readData(Objects.requireNonNull(view.getReader()), list);
        return fromVanillaList(list);
    }

    /**
     * Converts the first Inventory element to a single NbtElement.
     * @return ()
     * @throws RuntimeException ()
     */
    public NbtElement toNbtSingle() throws RuntimeException
    {
        if (this.size() > 1)
        {
            throw new RuntimeException("Inventory is too large for a single entry!");
        }

        NbtElement element = StackWithSlot.CODEC.encodeStart(NbtOps.INSTANCE, this.items.stream().findFirst().orElseThrow()).getPartialOrThrow();
        //LOGGER.info("toNbtSingle(): --> nbt: [{}]", element.toString());
        return element;
    }

    /**
     * Converts this Inventory to a basic NbtList with Slot information.
     * @return ()
     * @throws RuntimeException ()
     */
    public NbtList toNbtList() throws RuntimeException
    {
        NbtList nbt = new NbtList();

        if (this.isEmpty())
        {
            return nbt;
        }

        this.items.forEach(
                (slot) ->
                {
                    NbtElement element = StackWithSlot.CODEC.encodeStart(NbtOps.INSTANCE, slot).getPartialOrThrow();
                    //LOGGER.info("toNbtList(): slot [{}] --> nbt: [{}]", slot.slot(), element.toString());
                    nbt.add(element);
                }
        );

        return nbt;
    }

    /**
     * Writes this Inventory to a Nbt Type (List or Compound) using a key; with slot information.
     * @param type ()
     * @param key ()
     * @return ()
     * @throws RuntimeException ()
     */
    public NbtCompound toNbt(NbtType<?> type, String key) throws RuntimeException
    {
        NbtCompound nbt = new NbtCompound();

        if (type == NbtList.TYPE)
        {
            NbtList list = this.toNbtList();

            if (list.isEmpty())
            {
                return nbt;
            }

            nbt.put(key, list);

            return nbt;
        }
        else if (type == NbtCompound.TYPE)
        {
            nbt.put(key, this.toNbtSingle());

            return nbt;
        }

        throw new RuntimeException("Unsupported Nbt Type!");
    }

    /**
     * Creates a new NbtInventory from a Nbt Type (List or Compound) using a key; retains slot information.
     * @param nbtIn ()
     * @param key ()
     * @return ()
     * @throws RuntimeException ()
     */
    public static @Nullable NbtInventory fromNbt(@Nonnull NbtCompound nbtIn, String key) throws RuntimeException
    {
        if (nbtIn.isEmpty() || !nbtIn.contains(key))
        {
            return null;
        }

        if (Objects.requireNonNull(nbtIn.get(key)).getNbtType() == NbtList.TYPE)
        {
            return fromNbtList(nbtIn.getListOrEmpty(key));
        }
        else if (Objects.requireNonNull(nbtIn.get(key)).getNbtType() == NbtCompound.TYPE)
        {
            return fromNbtSingle(nbtIn.getCompoundOrEmpty(key));
        }
        else
        {
            throw new RuntimeException("Invalid Nbt Type!");
        }
    }

    /**
     * Creates a new NbtInventory from a single-member NbtCompound containing a single item with a slot number.
     * @param nbt ()
     * @return ()
     * @throws RuntimeException ()
     */
    public static @Nullable NbtInventory fromNbtSingle(@Nonnull NbtCompound nbt) throws RuntimeException
    {
        if (nbt.isEmpty())
        {
            return null;
        }

        NbtInventory newInv = new NbtInventory();

        newInv.items = new HashSet<>();
        StackWithSlot slot = StackWithSlot.CODEC.parse(NbtOps.INSTANCE, nbt).getPartialOrThrow();
        //LOGGER.info("fromNbtSingle(): slot [{}], stack: [{}]", slot.slot(), slot.stack().toString());
        newInv.items.add(slot);

        return newInv;
    }

    /**
     * Creates a new NbtInventory from an NbtList; utilizing Slot information.
     * @param list ()
     * @return ()
     * @throws RuntimeException ()
     */
    public static @Nullable NbtInventory fromNbtList(@Nonnull NbtList list) throws RuntimeException
    {
        if (list.isEmpty())
        {
            return null;
        }
        else if (list.size() > MAX_SIZE)
        {
            throw new RuntimeException("Nbt List is too large!");
        }

        NbtInventory newInv = new NbtInventory();
        int size = list.size();
        size = MathHelper.clamp(size, 1, MAX_SIZE);
        newInv.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            StackWithSlot slot = StackWithSlot.CODEC.parse(NbtOps.INSTANCE, list.get(i)).getPartialOrThrow();
            //LOGGER.info("fromNbtList(): [{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
            newInv.items.add(slot);
        }

        return newInv;
    }

    public void dumpInv()
    {
        AtomicInteger i = new AtomicInteger(0);
        LOGGER.info("dumpInv() --> START");

        this.items.forEach(
                (slot ->
                {
                    LOGGER.info("[{}]: slot [{}], stack: [{}]", i, slot.slot(), slot.stack().toString());
                })
        );

        LOGGER.info("dumpInv() --> END");
    }

    @Override
    public void close() throws Exception
    {
        this.items.clear();
    }
}
