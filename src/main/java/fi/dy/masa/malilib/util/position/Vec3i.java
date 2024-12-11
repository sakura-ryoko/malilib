package fi.dy.masa.malilib.util.position;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3i;

import net.minecraft.util.math.BlockPos;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class Vec3i extends net.minecraft.util.math.BlockPos
{
    public static final Vec3i ZERO = new Vec3i(0, 0, 0);

    /*
    public final int x;
    public final int y;
    public final int z;
    */

    public Vec3i(int x, int y, int z)
    {
        super(x, y, z);
        /*
        this.x = x;
        this.y = y;
        this.z = z;
        */
    }

    public Vec3i of(BlockPos blockPos)
    {
        return new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public Vec3i of(net.minecraft.util.math.Vec3i vanilla)
    {
        return new Vec3i(vanilla.getX(), vanilla.getY(), vanilla.getZ());
    }

    /*
    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }
    */

    public long squareDistanceTo(Vec3i other)
    {
        return this.squareDistanceTo(other.getX(), other.getY(), other.getZ());
    }

    public long squareDistanceTo(int x, int y, int z)
    {
        return (long) this.getX() * x + (long) this.getY() * y + (long) this.getZ() * z;
    }

    public double squareDistanceOfCenterTo(Vec3d pos)
    {
        return (this.getX() + 0.5) * pos.x + (this.getY() + 0.5) * pos.y + (this.getZ() + 0.5) * pos.z;
    }

    public net.minecraft.util.math.Vec3i toVanilla()
    {
        return new net.minecraft.util.math.Vec3i(this.getX(), this.getY(), this.getZ());
    }

    public net.minecraft.util.math.BlockPos toBlockPos()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }

    public Vector3i toVector()
    {
        return new Vector3i(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public String toString()
    {
        return "Vec3i{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }
}
