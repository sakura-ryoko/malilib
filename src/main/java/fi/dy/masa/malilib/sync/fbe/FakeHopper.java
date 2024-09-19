package fi.dy.masa.malilib.sync.fbe;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class FakeHopper extends FakeLootableContainer
{
    private static final int MAX_SLOTS = 5;
    private Direction facing;
    private int transferCooldown;

    public FakeHopper(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, MAX_SLOTS);
        this.inventory = DefaultedList.ofSize(MAX_SLOTS, ItemStack.EMPTY);
        this.transferCooldown = -1;
        this.facing = state.get(HopperBlock.FACING);
    }

    public FakeHopper(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        //this.setWorld(world);
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public FakeHopper createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeHopper(BlockEntityType.HOPPER, pos, state);
    }

    public double getHopperX()
    {
        return (double) this.getPos().getX() + 0.5;
    }

    public double getHopperY()
    {
        return (double) this.getPos().getY() + 0.5;
    }

    public double getHopperZ()
    {
        return (double) this.getPos().getZ() + 0.5;
    }

    public boolean canBlockFromAbove()
    {
        return true;
    }

    public void setTransferCooldown(int transferCooldown)
    {
        this.transferCooldown = transferCooldown;
    }

    public boolean needsCooldown()
    {
        return this.transferCooldown > 0;
    }

    public boolean isDisabled()
    {
        return this.transferCooldown > 8;
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.readNbt(nbt, registries);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt))
        {
            Inventories.readNbt(nbt, this.inventory, registries);
        }

        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.writeNbt(nbt, registries);
        if (!this.writeLootTable(nbt))
        {
            Inventories.writeNbt(nbt, this.inventory, registries);
        }

        nbt.putInt("TransferCooldown", this.transferCooldown);
    }
}
