package fi.dy.masa.malilib.util.nbt;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

public class NbtInventory implements AutoCloseable
{
    public static final int DEFAULT_SIZE = 27;
    public static final int DOUBLE_SIZE = 54;
    public static final int MAX_SIZE = 256;
    private HashSet<StackWithSlot> items;

    private NbtInventory() {}

    public static NbtInventory create(int size)
    {
        NbtInventory newInv = new NbtInventory();

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

    public DefaultedList<ItemStack> toVanillaList()
    {
        if (this.isEmpty())
        {
            return DefaultedList.of();
        }

        DefaultedList<ItemStack> list = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);

        this.items.forEach(
                (slot) -> list.set(slot.slot(), slot.stack())
        );

        return list;
    }

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
            newInv.items.add(new StackWithSlot(i, list.get(i)));
        }

        return newInv;
    }

    public @Nullable Inventory toInventory()
    {
        if (this.isEmpty())
        {
            return null;
        }

        Inventory inv;

        if (this.size() > DEFAULT_SIZE)
        {
            inv = new DoubleInventory(new SimpleInventory(), new SimpleInventory());
        }
        else
        {
            inv = new SimpleInventory();
        }

        this.items.forEach(
                (slot) -> inv.setStack(slot.slot(), slot.stack())
        );

        return inv;
    }

    public static NbtInventory fromInventory(@Nonnull Inventory inv)
    {
        NbtInventory newInv = new NbtInventory();

        int size = inv.size();
        size = MathHelper.clamp(size, 1, MAX_SIZE);
        newInv.items = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            newInv.items.add(new StackWithSlot(i, inv.getStack(i)));
        }

        return newInv;
    }

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

    public NbtElement toNbtSingle() throws RuntimeException
    {
        if (this.size() > 1)
        {
            throw new RuntimeException("Inventory is too large for a single entry!");
        }

        return StackWithSlot.CODEC.encodeStart(NbtOps.INSTANCE, this.items.stream().findFirst().orElseThrow()).getPartialOrThrow();
    }

    public NbtList toNbtList() throws RuntimeException
    {
        NbtList nbt = new NbtList();

        if (this.isEmpty())
        {
            return nbt;
        }

        this.items.forEach(
                (slot) -> nbt.add(StackWithSlot.CODEC.encodeStart(NbtOps.INSTANCE, slot).getPartialOrThrow())
        );

        return nbt;
    }

    public @Nullable NbtCompound toNbt(NbtType<?> type, String key) throws RuntimeException
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

    public static @Nullable NbtInventory fromNbtSingle(@Nonnull NbtCompound nbt) throws RuntimeException
    {
        if (nbt.isEmpty())
        {
            return null;
        }

        NbtInventory newInv = new NbtInventory();

        newInv.items = new HashSet<>();
        newInv.items.add(StackWithSlot.CODEC.parse(NbtOps.INSTANCE, nbt).getPartialOrThrow());

        return newInv;
    }

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
            newInv.items.add(StackWithSlot.CODEC.parse(NbtOps.INSTANCE, list.get(i)).getPartialOrThrow());
        }

        return newInv;
    }

    @Override
    public void close() throws Exception
    {
        this.items.clear();
    }
}
