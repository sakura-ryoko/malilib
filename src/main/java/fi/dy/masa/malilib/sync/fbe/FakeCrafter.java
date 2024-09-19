package fi.dy.masa.malilib.sync.fbe;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeCrafter extends FakeLootableContainer implements RecipeInputInventory
{
    public static final int MAX_SLOTS = 9;
    private DefaultedList<ItemStack> inputStacks;
    private final Set<Integer> disabledSlots;
    private int craftingTicksRemaining;
    private boolean triggered;

    public FakeCrafter(BlockPos pos, BlockState state)
    {
        super(BlockEntityType.CRAFTER, pos, state);
        this.inputStacks = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
        this.disabledSlots = new HashSet<>();
        this.craftingTicksRemaining = 0;
    }

    public FakeCrafter(BlockEntity be, World world)
    {
        this(be.getPos(), be.getCachedState());
        this.setWorld(world);
        this.copyFromBlockEntity(be, world.getRegistryManager());
    }

    @Override
    public int getWidth()
    {
        return 3;
    }

    @Override
    public int getHeight()
    {
        return 3;
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder)
    {
        // NO-OP
    }

    public int size()
    {
        return 9;
    }

    public boolean isEmpty()
    {
        Iterator<ItemStack> iter = this.inputStacks.iterator();

        ItemStack itemStack;
        do
        {
            if (!iter.hasNext())
            {
                return true;
            }

            itemStack = iter.next();
        }
        while (itemStack.isEmpty());

        return false;
    }

    public ItemStack getStack(int slot)
    {
        return this.inputStacks.get(slot);
    }

    public void setStack(int slot, ItemStack stack)
    {
        if (this.isSlotDisabled(slot))
        {
            this.setSlotEnabled(slot, true);
        }

        super.setStack(slot, stack);
    }

    public DefaultedList<ItemStack> getHeldStacks()
    {
        return this.inputStacks;
    }

    public void setHeldStacks(DefaultedList<ItemStack> inventory)
    {
        this.inputStacks = inventory;
    }

    public boolean isTriggered()
    {
        return false;
    }

    public int getCraftingTicksRemaining()
    {
        return this.craftingTicksRemaining;
    }

    public void setCraftingTicksRemaining(int craftingTicksRemaining)
    {
        this.craftingTicksRemaining = craftingTicksRemaining;
    }

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

    public boolean isValid(int slot, ItemStack stack)
    {
        if (isSlotDisabled(slot))
        {
            return false;
        }
        ItemStack entry = this.inputStacks.get(slot);

        if (entry.getCount() < entry.getMaxCount() && ItemStack.areItemsAndComponentsEqual(stack, entry))
        {
            return true;
        }
        else
        {
            return entry.isEmpty();
        }
    }

    public Set<Integer> getDisabledSlots()
    {
        return this.disabledSlots;
    }

    private void putDisabledSlots(NbtCompound nbt)
    {
        IntList intList = new IntArrayList();

        for (int i = 0; i < 9; ++i)
        {
            if (this.isSlotDisabled(i))
            {
                intList.add(i);
            }
        }

        nbt.putIntArray("disabled_slots", intList);
    }

    private void putTriggered(NbtCompound nbt)
    {
        nbt.putInt("triggered", this.triggered ? 1 : 0);
    }

    public void setTriggered(boolean triggered)
    {
        this.triggered = triggered;
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.readNbt(nbt, registry);
        this.craftingTicksRemaining = nbt.getInt("crafting_ticks_remaining");
        this.inputStacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);

        if (!this.readLootTable(nbt))
        {
            Inventories.readNbt(nbt, this.inputStacks, registry);
        }

        int[] is = nbt.getIntArray("disabled_slots");

        for (int j : is)
        {
            if (!this.isSlotDisabled(j))
            {
                this.setSlotEnabled(j, false);
            }
        }

        this.setTriggered(nbt.getInt("triggered") > 0);
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.writeNbt(nbt, registry);
        nbt.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
        if (!this.writeLootTable(nbt))
        {
            Inventories.writeNbt(nbt, this.inputStacks, registry);
        }

        this.putDisabledSlots(nbt);
        this.putTriggered(nbt);
    }
}