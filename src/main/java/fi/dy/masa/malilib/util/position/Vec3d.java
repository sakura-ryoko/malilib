package fi.dy.masa.malilib.util.position;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class Vec3d
{
    public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);

    public final double x;
    public final double y;
    public final double z;

    public Vec3d(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public Vec3d add(double x, double y, double z)
    {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    public Vec3d subtract(double x, double y, double z)
    {
        return new Vec3d(this.x - x, this.y - y, this.z - z);
    }

    public Vec3d add(Vec3d other)
    {
        return new Vec3d(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vec3d subtract(Vec3d other)
    {
        return new Vec3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vec3d scale(double factor)
    {
        return new Vec3d(this.x * factor, this.y * factor, this.z * factor);
    }

    public double squareDistanceTo(Vec3d other)
    {
        return this.squareDistanceTo(other.x, other.y, other.z);
    }

    public double squareDistanceTo(net.minecraft.util.math.Vec3d other)
    {
        return this.squareDistanceTo(other.x, other.y, other.z);
    }

    public double squareDistanceTo(double x, double y, double z)
    {
        return this.x * x + this.y * y + this.z * z;
    }

    public double distanceTo(Vec3d other)
    {
        return this.distanceTo(other.x, other.y, other.z);
    }

    public double distanceTo(double x, double y, double z)
    {
        return Math.sqrt(this.squareDistanceTo(x, y, z));
    }

    public Vec3d normalize()
    {
        return normalized(this.x, this.y, this.z);
    }

    public static Vec3d of(double x, double y, double z)
    {
        return new Vec3d(x, y, z);
    }

    public static Vec3d of(net.minecraft.util.math.Vec3d pos)
    {
        return new Vec3d(pos.x, pos.y, pos.z);
    }

    public static Vec3d of(net.minecraft.util.math.Vec3i pos)
    {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3d normalized(double x, double y, double z)
    {
        double d = Math.sqrt(x * x + y * y + z * z);
        return d < 1.0E-4 ? ZERO : new Vec3d(x / d, y / d, z / d);
    }

    public net.minecraft.util.math.Vec3d toVanilla()
    {
        return new net.minecraft.util.math.Vec3d(this.x, this.y, this.z);
    }

    public Vector3d toVector()
    {
        return new Vector3d(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public String toString()
    {
        return "Vec3d{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }
}
