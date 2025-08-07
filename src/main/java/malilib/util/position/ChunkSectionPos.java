package malilib.util.position;

import java.util.Comparator;

public class ChunkSectionPos extends Vec3i
{
    public ChunkSectionPos(BlockPos pos)
    {
        this(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    public ChunkSectionPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    @Override
    public String toString()
    {
        return "ChunkSectionPos{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }

    public static ChunkSectionPos ofBlockPos(net.minecraft.util.math.Vec3i blockPos)
    {
        return new ChunkSectionPos(blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4);
    }

    public static class DistanceComparator implements Comparator<ChunkSectionPos>
    {
        private final ChunkSectionPos referencePosition;

        public DistanceComparator(ChunkSectionPos referencePosition)
        {
            this.referencePosition = referencePosition;
        }

        @Override
        public int compare(ChunkSectionPos pos1, ChunkSectionPos pos2)
        {
            double dist1 = pos1.getSquaredDistanceTo(this.referencePosition);
            double dist2 = pos2.getSquaredDistanceTo(this.referencePosition);

            return Double.compare(dist1, dist2);
        }
    }
}
