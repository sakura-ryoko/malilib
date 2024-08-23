package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.NbtIntArray;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class IntBoundingBox
{
    public final int minX;
    public final int minY;
    public final int minZ;
    public final int maxX;
    public final int maxY;
    public final int maxZ;

    public IntBoundingBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public boolean containsPos(Vec3i pos)
    {
        return pos.getX() >= this.minX &&
               pos.getX() <= this.maxX &&
               pos.getZ() >= this.minZ &&
               pos.getZ() <= this.maxZ &&
               pos.getY() >= this.minY &&
               pos.getY() <= this.maxY;
    }

    public boolean containsPos(long pos)
    {
        int x = BlockPos.unpackLongX(pos);
        int y = BlockPos.unpackLongY(pos);
        int z = BlockPos.unpackLongZ(pos);

        return x >= this.minX && y >= this.minY && z >= this.minZ &&
               x <= this.maxX && y <= this.maxY && z <= this.maxZ;
    }

    public boolean intersects(IntBoundingBox box)
    {
        return this.maxX >= box.minX &&
               this.minX <= box.maxX &&
               this.maxZ >= box.minZ &&
               this.minZ <= box.maxZ &&
               this.maxY >= box.minY &&
               this.minY <= box.maxY;
    }

    public int getMinValueForAxis(Direction.Axis axis)
    {
        switch (axis)
        {
            case X: return this.minX;
            case Y: return this.minY;
            case Z: return this.minZ;
        }

        return 0;
    }

    public int getMaxValueForAxis(Direction.Axis axis)
    {
        switch (axis)
        {
            case X: return this.maxX;
            case Y: return this.maxY;
            case Z: return this.maxZ;
        }

        return 0;
    }

    public BlockBox toVanillaBox()
    {
        return new BlockBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public NbtIntArray toNBTIntArray()
    {
        return new NbtIntArray(new int[] { this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ });
    }

    public static IntBoundingBox fromVanillaBox(BlockBox box)
    {
        return createProper(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
    }

    public static IntBoundingBox createProper(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return new IntBoundingBox(
                Math.min(x1, x2),
                Math.min(y1, y2),
                Math.min(z1, z2),
                Math.max(x1, x2),
                Math.max(y1, y2),
                Math.max(z1, z2));
    }

    public static IntBoundingBox createForWorldBounds(@Nullable World world)
    {
        int worldMinH = -30000000;
        int worldMaxH =  30000000;
        int worldMinY = world != null ? world.getBottomY() : -64;
        int worldMaxY = world != null ? world.getTopYInclusive() : 319;

        return new IntBoundingBox(worldMinH, worldMinY, worldMinH, worldMaxH, worldMaxY, worldMaxH);
    }

    public static IntBoundingBox fromArray(int[] coords)
    {
        if (coords.length == 6)
        {
            return new IntBoundingBox(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
        }
        else
        {
            return new IntBoundingBox(0, 0, 0, 0, 0, 0);
        }
    }

    public IntBoundingBox expand(int amount)
    {
        return this.expand(amount, amount, amount);
    }

    public IntBoundingBox expand(int x, int y, int z)
    {
        return new IntBoundingBox(this.minX - x, this.minY - y, this.minZ - z,
                                  this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public IntBoundingBox shrink(int x, int y, int z)
    {
        return this.expand(-x, -y, -z);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.maxX;
        result = prime * result + this.maxY;
        result = prime * result + this.maxZ;
        result = prime * result + this.minX;
        result = prime * result + this.minY;
        result = prime * result + this.minZ;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass())
        {
            return false;
        }

        IntBoundingBox other = (IntBoundingBox) obj;

        return this.maxX == other.maxX &&
               this.maxY == other.maxY &&
               this.maxZ == other.maxZ &&
               this.minX == other.minX &&
               this.minY == other.minY &&
               this.minZ == other.minZ;
    }
}
