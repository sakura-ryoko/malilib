package fi.dy.masa.malilib.sync.fbe;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeRecords extends FakeBlockEntity implements SingleStackInventory.SingleStackBlockEntityInventory
{
    ItemStack recordStack;
    private long ticks;

    public FakeRecords(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.recordStack = ItemStack.EMPTY;
    }

    public FakeRecords(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        System.out.print("be -> FakeJuke\n");
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public BlockEntity asBlockEntity()
    {
        return null;
    }

    public ItemStack getStack()
    {
        return this.recordStack;
    }

    public void setStack(ItemStack stack)
    {
        this.recordStack = stack;
    }

    public void markDirty()
    {
        // NO-OP
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.readNbt(nbt, registries);
        if (nbt.contains("RecordItem", 10))
        {
            this.recordStack = ItemStack.fromNbt(registries, nbt.getCompound("RecordItem")).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.recordStack = ItemStack.EMPTY;
        }

        if (nbt.contains("ticks_since_song_started", 4))
        {
            this.ticks = nbt.getLong("ticks_since_song_started");
        }
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.writeNbt(nbt, registries);
        if (!this.getStack().isEmpty())
        {
            nbt.put("RecordItem", this.getStack().toNbt(registries));
        }

        if (this.ticks >= 0L)
        {
            nbt.putLong("ticks_since_song_started", this.ticks);
        }
    }
}
