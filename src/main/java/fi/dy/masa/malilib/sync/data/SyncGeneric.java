package fi.dy.masa.malilib.sync.data;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SyncGeneric extends SyncData
{
    public SyncGeneric(BlockEntityType<?> type, BlockPos pos, BlockState state, World world)
    {
        super(type, pos, state, world);
    }

    public SyncGeneric(BlockEntity be)
    {
        this(be.getType(), be.getPos(), be.getCachedState(), be.getWorld());
        System.out.printf("SyncGeneric() init for block entity [%s], pos [%s]\n", be.getClass().getTypeName(), be.getPos().toShortString());
        this.copyNbtFromBlockEntity(be);
    }

    public SyncGeneric(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public SyncGeneric(Entity entity)
    {
        this(entity.getType(), entity.getWorld(), entity.getId());
        System.out.printf("SyncGeneric() init for entity [%s], id [%d]\n", entity.getClass().getTypeName(), entity.getId());
        this.copyNbtFromEntity(entity);
    }

    public SyncData asSyncData()
    {
        return this;
    }

    protected void initInventory()
    {
        System.out.print("SyncGeneric() initInventory()\n");
    }

    protected void initAttributes(Entity entity)
    {
        System.out.printf("SyncGeneric() initAttributes for entity [%s]\n", entity.getType().getBaseClass().getTypeName());
    }

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt);
        System.out.printf("SyncGeneric() readNbt: [%s]\n", nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
        System.out.printf("SyncGeneric() writeNbt: [%s]\n", nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        System.out.printf("SyncGeneric() readCustomDataFromNBT: [%s]\n", nbt != null ? nbt.toString() : "<EMPTY>");
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        System.out.printf("SyncGeneric() writeCustomDataToNbt: [%s]\n", nbt != null ? nbt.toString() : "<EMPTY>");
    }
}
