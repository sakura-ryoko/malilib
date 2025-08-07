package malilib.util.position;

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

    public double getSquaredDistanceTo(Vec3d other)
    {
        return this.getSquaredDistanceTo(other.x, other.y, other.z);
    }

    public double getSquaredDistanceTo(double x, double y, double z)
    {
        double diffX = x - this.x;
        double diffY = y - this.y;
        double diffZ = z - this.z;

        return diffX * diffX + diffY * diffY + diffZ * diffZ;
    }

    public double getDistanceTo(Vec3d other)
    {
        return this.getDistanceTo(other.x, other.y, other.z);
    }

    public double getDistanceTo(double x, double y, double z)
    {
        return Math.sqrt(this.getSquaredDistanceTo(x, y, z));
    }

    public Vec3d normalize()
    {
        return normalized(this.x, this.y, this.z);
    }

    public double getSquaredDistanceTo(net.minecraft.util.math.Vec3d other)
    {
        return this.getSquaredDistanceTo(other.x, other.y, other.z);
    }

    @Override
    public String toString()
    {
        return "Vec3d{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    public static Vec3d of(double x, double y, double z)
    {
        return new Vec3d(x, y, z);
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

    public static Vec3d of(net.minecraft.util.math.Vec3d pos)
    {
        return new Vec3d(pos.x, pos.y, pos.z);
    }

    public static Vec3d of(net.minecraft.util.math.Vec3i pos)
    {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}

        Vec3d vec3d = (Vec3d) o;

        if (Double.compare(vec3d.x, this.x) != 0) {return false;}
        if (Double.compare(vec3d.y, this.y) != 0) {return false;}
        return Double.compare(vec3d.z, this.z) == 0;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
