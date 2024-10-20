package fi.dy.masa.malilib.util;

import net.minecraft.util.math.Vec3d;

/**
 * Post-ReWrite code
 */
public class MathUtils
{
    public static Vec3d getRotationVector(float yaw, float pitch)
    {
        double f = Math.cos(-yaw * (Math.PI / 180.0) - Math.PI);
        double g = Math.sin(-yaw * (Math.PI / 180.0) - Math.PI);
        double h = -Math.cos(-pitch * (Math.PI / 180.0));
        double i = Math.sin(-pitch * (Math.PI / 180.0));

        return new Vec3d(g * h, i, f * h);
    }

    public static Vec3d scale(Vec3d vec, double factor)
    {
        return new Vec3d(vec.x * factor, vec.y * factor, vec.z * factor);
    }

    public static double squareDistanceTo(Vec3d i, Vec3d v)
    {
        return squareDistanceTo(i, v.x, v.y, v.z);
    }

    public static double squareDistanceTo(Vec3d v, double x, double y, double z)
    {
        return v.x * x + v.y * y + v.z * z;
    }

    public static int floor(float value)
    {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    public static int floor(double value)
    {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }
}
