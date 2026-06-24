package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Post-ReWrite code
 */
public class Vec2d
{
    public static final Codec<Vec2d> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.DOUBLE.fieldOf("x").forGetter(get -> get.x),
                    PrimitiveCodec.DOUBLE.fieldOf("y").forGetter(get -> get.y)
            ).apply(inst, Vec2d::new)
    );
    public static final PacketCodec<ByteBuf, Vec2d> PACKET_CODEC = new PacketCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, Vec2d value)
        {
            PacketCodecs.DOUBLE.encode(buf, value.x);
            PacketCodecs.DOUBLE.encode(buf, value.y);
        }

        @Override
        public @Nonnull Vec2d decode(@Nonnull ByteBuf buf)
        {
            return new Vec2d(
                    PacketCodecs.DOUBLE.decode(buf),
                    PacketCodecs.DOUBLE.decode(buf)
            );
        }
    };
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
