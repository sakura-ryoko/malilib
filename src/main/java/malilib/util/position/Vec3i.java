package malilib.util.position;

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

    public long getSquaredDistanceTo(Vec3i other)
    {
        return this.getSquaredDistanceTo(other.getX(), other.getY(), other.getZ());
    }

    public long getSquaredDistanceTo(int x, int y, int z)
    {
        long diffX = x - this.getX();
        long diffY = y - this.getY();
        long diffZ = z - this.getZ();

        return diffX * diffX + diffY * diffY + diffZ * diffZ;
    }

    public double getSquaredDistanceOfCenterTo(Vec3d pos)
    {
        double diffX = pos.x - (this.getX() + 0.5);
        double diffY = pos.y - (this.getY() + 0.5);
        double diffZ = pos.z - (this.getZ() + 0.5);

        return diffX * diffX + diffY * diffY + diffZ * diffZ;
    }

    @Override
    public String toString()
    {
        return "Vec3i{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }

    /*
    @Override
    public boolean equals(Object object)
    {
        if (this == object) {
            return true;
        } else if (! (object instanceof Vec3i)) {
            return false;
        } else {
            Vec3i vec3i = (Vec3i) object;
            if (this.getX() != vec3i.getX()) {
                return false;
            } else if (this.getY() != vec3i.getY()) {
                return false;
            } else {
                return this.getZ() == vec3i.getZ();
            }
        }
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }
    */
}
