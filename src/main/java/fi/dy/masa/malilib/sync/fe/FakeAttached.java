package fi.dy.masa.malilib.sync.fe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FakeAttached extends FakeEntity
{
    protected BlockPos attachedBlockPos;

    public FakeAttached(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public FakeAttached(EntityType<?> type, World world, int entityId, BlockPos attachedBlockPos)
    {
        this(type, world, entityId);
        this.attachedBlockPos = attachedBlockPos;
    }

    protected abstract void updateAttachmentPosition();

    public abstract boolean canStayAttached();

    public abstract void onBreak(@Nullable FakeEntity breaker);

    public boolean canHit()
    {
        return true;
    }

    protected boolean shouldSetPositionOnLoad()
    {
        return false;
    }

    public void setPosition(double x, double y, double z)
    {
        this.attachedBlockPos = BlockPos.ofFloored(x, y, z);
        this.updateAttachmentPosition();
    }

    public BlockPos getAttachedBlockPos()
    {
        return this.attachedBlockPos;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        BlockPos blockPos = this.getAttachedBlockPos();
        nbt.putInt("TileX", blockPos.getX());
        nbt.putInt("TileY", blockPos.getY());
        nbt.putInt("TileZ", blockPos.getZ());
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        BlockPos blockPos = new BlockPos(nbt.getInt("TileX"), nbt.getInt("TileY"), nbt.getInt("TileZ"));

        if (blockPos.isWithinDistance(this.getBlockPos(), 16.0))
        {
            this.attachedBlockPos = blockPos;
        }
    }
}
