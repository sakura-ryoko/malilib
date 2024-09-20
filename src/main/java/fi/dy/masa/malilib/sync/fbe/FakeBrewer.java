package fi.dy.masa.malilib.sync.fbe;

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

public class FakeBrewer extends FakeLockableContainer implements SidedInventory
{
    private static final int MAX_SLOTS = 5;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    private int brewTime;
    private int fuel;

    public FakeBrewer(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, MAX_SLOTS);
        this.inventory = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
    }

    public FakeBrewer(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        System.out.print("be -> FakeBrewer\n");
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public FakeBrewer createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeBrewer(BlockEntityType.BREWING_STAND, pos, state);
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
        super.readNbt(nbt, registry);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory, registry);
        this.brewTime = nbt.getShort("BrewTime");
        if (this.brewTime > 0)
        {
            this.itemBrewing = this.inventory.get(3).getItem();
        }

        this.fuel = nbt.getByte("Fuel");
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.writeNbt(nbt, registry);
        nbt.putShort("BrewTime", (short) this.brewTime);
        Inventories.writeNbt(nbt, this.inventory, registry);
        nbt.putByte("Fuel", (byte) this.fuel);
    }
}
