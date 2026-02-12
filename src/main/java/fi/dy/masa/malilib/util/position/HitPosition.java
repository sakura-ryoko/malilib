package fi.dy.masa.malilib.util.position;

public class HitPosition
{
    protected final BlockPos blockPos;
    protected final Vec3d exactPos;
    protected final Direction direction;

    public HitPosition(BlockPos blockPos, Vec3d exactPos, Direction direction)
    {
        this.blockPos = blockPos;
        this.exactPos = exactPos;
        this.direction = direction;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public Vec3d getExactPos()
    {
        return this.exactPos;
    }

    public Direction getSide()
    {
        return this.direction;
    }

    @Override
    public String toString()
    {
        return "HitPosition{blockPos=" + this.blockPos + ", exactPos=" + this.exactPos + ", direction=" + this.direction + "}";
    }

    public static HitPosition of(BlockPos blockPos, Vec3d exactPos, Direction direction)
    {
        return new HitPosition(blockPos, exactPos, direction);
    }
}
