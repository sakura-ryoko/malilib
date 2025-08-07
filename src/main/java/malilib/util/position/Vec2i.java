package malilib.util.position;

public class Vec2i
{
    public static final Vec2i ZERO = new Vec2i(0, 0);

    public final int x;
    public final int y;

    public Vec2i(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public double getSquaredDistanceTo(int x, int y)
    {
        double diffX = (double) x - (double) this.x;
        double diffY = (double) y - (double) this.y;

        return diffX * diffX + diffY * diffY;
    }

    public double getDistanceTo(int x, int y)
    {
        return Math.sqrt(this.getSquaredDistanceTo(x, y));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        Vec2i vec2i = (Vec2i) o;

        if (this.x != vec2i.x) { return false; }
        return this.y == vec2i.y;
    }

    @Override
    public int hashCode()
    {
        int result = this.x;
        result = 31 * result + this.y;
        return result;
    }

    @Override
    public String toString()
    {
        return "Vec2i{x=" + this.x + ", y=" + this.y + "}";
    }
}
