package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Post-ReWrite code
 */
public class Vec3f
{
    public static final Codec<Vec3f> FLOAT_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.FLOAT.fieldOf("x").forGetter(get -> get.x),
                    PrimitiveCodec.FLOAT.fieldOf("y").forGetter(get -> get.y),
                    PrimitiveCodec.FLOAT.fieldOf("z").forGetter(get -> get.z)
            ).apply(inst, Vec3f::new)
    );
    public static final Codec<Vec3f> DOUBLE_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.DOUBLE.fieldOf("x").forGetter(get -> Double.valueOf(get.x)),
                    PrimitiveCodec.DOUBLE.fieldOf("y").forGetter(get -> Double.valueOf(get.y)),
                    PrimitiveCodec.DOUBLE.fieldOf("z").forGetter(get -> Double.valueOf(get.z))
            ).apply(inst, Vec3f::new)
    );
    public static final Codec<Vec3f> CODEC = FLOAT_CODEC;
    public static final PacketCodec<ByteBuf, Vec3f> PACKET_CODEC = new PacketCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, Vec3f value)
        {
            PacketCodecs.FLOAT.encode(buf, value.x);
            PacketCodecs.FLOAT.encode(buf, value.y);
            PacketCodecs.FLOAT.encode(buf, value.z);
        }

        @Override
        public @Nonnull Vec3f decode(@Nonnull ByteBuf buf)
        {
            return new Vec3f(
                    PacketCodecs.FLOAT.decode(buf),
                    PacketCodecs.FLOAT.decode(buf),
                    PacketCodecs.FLOAT.decode(buf)
            );
        }
    };
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

    public Vec3f normalize()
    {
        return normalized(this.x, this.y, this.z);
    }

    public static Vec3f normalized(float x, float y, float z)
    {
        double d = Math.sqrt(x * x + y * y + z * z);
        return d < 1.0E-4 ? ZERO : new Vec3f(x / d, y / d, z / d);
    }

    public Vector3f toVector()
    {
        return new Vector3f(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public String toString()
    {
        return "Vec3f{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
    }
}
