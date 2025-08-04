package malilib.util.position;

public class ChunkPos extends net.minecraft.util.math.ChunkPos
{
    public ChunkPos(int x, int z)
    {
        super(x, z);
    }

    public int getX()
    {
        return this.x;
    }

    public int getZ()
    {
        return this.z;
    }

    @Override
    public String toString()
    {
        return "ChunkPos{x=" + this.x + ", z=" + this.z + "}";
    }

    public static long asLong(int chunkX, int chunkZ)
    {
        return ((long) chunkZ << 32) | ((long) chunkX & 0xFFFFFFFFL);
    }

    public static ChunkPos of(net.minecraft.util.math.ChunkPos pos)
    {
        return new ChunkPos(pos.x, pos.z);
    }
}
