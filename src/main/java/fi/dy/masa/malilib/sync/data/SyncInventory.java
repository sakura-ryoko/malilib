package fi.dy.masa.malilib.sync.data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.Constants;

public class SyncInventory extends SyncData implements Inventory
{
    private static final String DISABLED_SLOTS_KEY = "disabled_slots";
    private static final String STACKS_KEY = "Items";
    private static final int MAX_SLOTS = 256;
    protected DefaultedList<ItemStack> stacks;
    private final Set<Integer> disabledSlots;

    public SyncInventory(BlockEntityType<?> type, BlockPos pos, BlockState state, World world, int maxSlots)
    {
        super(type, pos, state, world);
        this.stacks = DefaultedList.ofSize(maxSlots, ItemStack.EMPTY);
        this.disabledSlots = new HashSet<>();
    }

    public SyncInventory(BlockEntity be, int maxSlots)
    {
        this(be.getType(), be.getPos(), be.getCachedState(), be.getWorld(), maxSlots);
        this.copyNbtFromBlockEntity(be);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    public SyncInventory(BlockEntity be)
    {
        this(be, MAX_SLOTS);
    }

    protected void initInventory()
    {
        if (this.stacks == null)
        {
            this.stacks = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
        }
    }

    protected void initAttributes(Entity entity)
    {
        // NO-OP
    }

    public DefaultedList<ItemStack> getHeldStacks()
    {
        return this.stacks;
    }

    public void setHeldStacks(DefaultedList<ItemStack> inventory)
    {
        this.stacks = inventory;
    }

    public int size()
    {
        return this.stacks.size();
    }

    public boolean isEmpty()
    {
        return this.stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public ItemStack getStack(int slot)
    {
        return this.stacks.get(slot);
    }

    public ItemStack removeStack(int slot, int amount)
    {
        ItemStack itemStack = Objects.requireNonNullElse(this.stacks.get(slot), ItemStack.EMPTY);
        this.stacks.set(slot, ItemStack.EMPTY);
        return itemStack;
    }

    public ItemStack removeStack(int slot)
    {
        return this.removeStack(slot, 1);
    }

    public void setStack(int slot, ItemStack stack)
    {
        if (stack.isEmpty())
        {
            this.removeStack(slot, 1);
        }
        else
        {
            this.stacks.set(slot, stack);
        }
    }

    public void markDirty()
    {
        // NO-OP
    }

    public boolean canPlayerUse(PlayerEntity player)
    {
        return false;
    }

    // Crafter Slots
    public boolean isSlotDisabled(int slot)
    {
        if (slot >= 0 && slot < 9)
        {
            return this.disabledSlots.contains(slot);
        }

        return false;
    }

    public void setSlotEnabled(int slot, boolean enabled)
    {
        if (slot >= 0 && slot < 9)
        {
            if (isSlotDisabled(slot) && enabled)
            {
                this.disabledSlots.remove(slot);
            }
            else if (!isSlotDisabled(slot) && !enabled)
            {
                this.disabledSlots.add(slot);
            }
        }
    }

    public Set<Integer> getDisabledSlots()
    {
        return this.disabledSlots;
    }

    private void putDisabledSlots(NbtCompound nbt)
    {
        if (this.disabledSlots.isEmpty())
        {
            return;
        }
        IntList intList = new IntArrayList();

        for (int i = 0; i < 9; ++i)
        {
            if (this.isSlotDisabled(i))
            {
                intList.add(i);
            }
        }

        nbt.putIntArray(DISABLED_SLOTS_KEY, intList);
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        if (nbt.contains(STACKS_KEY))
        {
            this.stacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
            Inventories.readNbt(nbt, this.stacks, this.getRegistryManager());
        }
        if (nbt.contains(DISABLED_SLOTS_KEY, Constants.NBT.TAG_INT_ARRAY))
        {
            int[] is = nbt.getIntArray(DISABLED_SLOTS_KEY);

            for (int j : is)
            {
                if (!this.isSlotDisabled(j))
                {
                    this.setSlotEnabled(j, false);
                }
            }
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        if (this.stacks.size() > 1)
        {
            Inventories.writeNbt(nbt, this.stacks, this.getRegistryManager());
        }
        this.putDisabledSlots(nbt);
    }

    protected void readComponents(SyncData.ComponentsAccess components)
    {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.stacks);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.stacks));
    }
}
