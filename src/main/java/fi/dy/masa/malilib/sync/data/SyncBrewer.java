package fi.dy.masa.malilib.sync.data;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SyncBrewer extends SyncInventory implements SidedInventory
{
    private static final String BREW_TIME_KEY = "BrewTime";
    private static final String FUEL_KEY = "Fuel";
    private static final int MAX_SLOTS = 5;
    private static final int BREWING_SLOT = 3;
    private Item itemBrewing;
    private int brewTime;
    private int fuel;

    public SyncBrewer(BlockEntityType<?> type, BlockPos pos, BlockState state, World world, int maxSlots)
    {
        super(type, pos, state, world, maxSlots);
        this.initInventory();
    }

    public SyncBrewer(BlockEntity be)
    {
        this(be.getType(), be.getPos(), be.getCachedState(), be.getWorld(), MAX_SLOTS);
        this.copyNbtFromBlockEntity(be);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    protected void initInventory()
    {
        super.initInventory();
        this.stacks = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
    }

    public Item getItemBrewing()
    {
        return this.itemBrewing;
    }

    public int getBrewTime()
    {
        return this.brewTime;
    }

    public int getFuel()
    {
        return this.fuel;
    }

    public int[] getAvailableSlots(Direction side)
    {
        if (side == Direction.UP)
        {
            return new int[]{3};
        }
        else
        {
            return side == Direction.DOWN ? new int[]{0, 1, 2, 3} : new int[]{0, 1, 2, 4};
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
    {
        return true;
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir)
    {
        return true;
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt, registry);
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt, registry);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        this.stacks = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.stacks, this.getRegistryManager());
        this.brewTime = nbt.getShort(BREW_TIME_KEY);
        if (this.brewTime > 0)
        {
            this.itemBrewing = this.stacks.get(BREWING_SLOT).getItem();
        }

        this.fuel = nbt.getByte(FUEL_KEY);
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putShort(BREW_TIME_KEY, (short) this.brewTime);
        Inventories.writeNbt(nbt, this.stacks, this.getRegistryManager());
        nbt.putByte(FUEL_KEY, (byte) this.fuel);
    }
}
