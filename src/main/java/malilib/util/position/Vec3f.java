package malilib.util.position;

public class Vec3f
{
    public static final Vec3f ZERO = new Vec3f(0.0F, 0.0F, 0.0F);

    public final float x;
    public final float y;
    public final float z;

    public Vec3f(double x, double y, double z)
    {
        this((float) x, (float) y, (float) z);
    }

    public Vec3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public float getZ()
    {
        return this.z;
    }

    public double getSquaredDistanceTo(double x, double y, double z)
    {
        double diffX = x - this.x;
        double diffY = y - this.y;
        double diffZ = z - this.z;

        return diffX * diffX + diffY * diffY + diffZ * diffZ;
    }

    public double getDistanceTo(double x, double y, double z)
    {
        return Math.sqrt(this.getSquaredDistanceTo(x, y, z));
    }

    public Vec3f normalize()
    {
        return normalized(this.x, this.y, this.z);
    }

    public static Vec3f normalized(float x, float y, float z)
    {
        double d = Math.sqrt(x * x + y * y + z * z);
        return d < 1.0E-4 ? ZERO : new Vec3f(x / d, y / d, z / d);
    }

    @Override
    public String toString()
    {
        return "Vec3f{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}

        Vec3f vec3f = (Vec3f) o;

        if (Float.compare(vec3f.x, this.x) != 0) {return false;}
        if (Float.compare(vec3f.y, this.y) != 0) {return false;}
        return Float.compare(vec3f.z, this.z) == 0;
    }

    @Override
    public int hashCode()
    {
        int result = (this.x != +0.0f ? Float.floatToIntBits(this.x) : 0);
        result = 31 * result + (this.y != +0.0f ? Float.floatToIntBits(this.y) : 0);
        result = 31 * result + (this.z != +0.0f ? Float.floatToIntBits(this.z) : 0);
        return result;
    }
}
