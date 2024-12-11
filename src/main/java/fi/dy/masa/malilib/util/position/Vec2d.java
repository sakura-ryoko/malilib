package fi.dy.masa.malilib.util.position;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector2d;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class Vec2d
{
    public static final Vec2d ZERO = new Vec2d(0.0, 0.0);

    public final double x;
    public final double y;

    public Vec2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getSquaredDistance(double x, double y)
    {
        double diffX = x - this.x;
        double diffY = y - this.y;

        return diffX * diffX + diffY * diffY;
    }

    public double getDistance(double x, double y)
    {
        return Math.sqrt(this.getSquaredDistance(x, y));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        Vec2d vec2d = (Vec2d) o;
        return Double.compare(vec2d.x, this.x) == 0 &&
               Double.compare(vec2d.y, this.y) == 0;
    }

    public Vector2d toVector()
    {
        return new Vector2d(this.getX(), this.getY());
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
        return result;
    }

    @Override
    public String toString()
    {
        return "Vec2d{x=" + this.x + ", y=" + this.y + "}";
    }
}
