package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Vec3d
{
    public static final Codec<Vec3d> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.DOUBLE.fieldOf("x").forGetter(get -> get.x),
                    PrimitiveCodec.DOUBLE.fieldOf("y").forGetter(get -> get.y),
                    PrimitiveCodec.DOUBLE.fieldOf("z").forGetter(get -> get.z)
            ).apply(inst, Vec3d::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull Vec3d> PACKET_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, Vec3d value)
        {
            ByteBufCodecs.DOUBLE.encode(buf, value.x);
            ByteBufCodecs.DOUBLE.encode(buf, value.y);
            ByteBufCodecs.DOUBLE.encode(buf, value.z);
        }

        @Override
        public @Nonnull Vec3d decode(@Nonnull ByteBuf buf)
        {
            return new Vec3d(
                    ByteBufCodecs.DOUBLE.decode(buf),
                    ByteBufCodecs.DOUBLE.decode(buf),
                    ByteBufCodecs.DOUBLE.decode(buf)
            );
        }
    };
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

    public double getSquaredDistanceTo(net.minecraft.world.phys.Vec3 other)
    {
        return this.getSquaredDistanceTo(other.x, other.y, other.z);
    }

    @Override
    public String toString()
    {
        return "Vec3d{x=" + this.x + ", y=" + this.y + ", z=" + this.z + "}";
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

    public static Vec3d of(double x, double y, double z)
    {
        return new Vec3d(x, y, z);
    }

    public static Vec3d normalized(double x, double y, double z)
    {
        double d = Math.sqrt(x * x + y * y + z * z);
        return d < 1.0E-4 ? ZERO : new Vec3d(x / d, y / d, z / d);
    }

    public net.minecraft.world.phys.Vec3 toVanilla()
    {
        return new net.minecraft.world.phys.Vec3(this.x, this.y, this.z);
    }

    public static Vec3d of(net.minecraft.world.phys.Vec3 pos)
    {
        return new Vec3d(pos.x, pos.y, pos.z);
    }

    public static Vec3d of(net.minecraft.core.Vec3i pos)
    {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static class MutVec3d
    {
        public double x;
        public double y;
        public double z;

        public MutVec3d()
        {
            this(0, 0, 0);
        }

        public MutVec3d(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public MutVec3d(net.minecraft.world.phys.Vec3 pos)
        {
            this(pos.x, pos.y, pos.z);
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

        public MutVec3d setX(double x)
        {
            this.x = x;
            return this;
        }

        public MutVec3d setY(double y)
        {
            this.y = y;
            return this;
        }

        public MutVec3d setZ(double z)
        {
            this.z = z;
            return this;
        }

        public MutVec3d set(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public MutVec3d setFrom(net.minecraft.world.phys.Vec3 pos)
        {
            this.x = pos.x;
            this.y = pos.y;
            this.z = pos.z;
            return this;
        }

        public MutVec3d add(Vec3d pos)
        {
            this.x += pos.x;
            this.y += pos.y;
            this.z += pos.z;
            return this;
        }

        public MutVec3d subtract(Vec3d pos)
        {
            this.x -= pos.x;
            this.y -= pos.y;
            this.z -= pos.z;
            return this;
        }

        public MutVec3d add(double x, double y, double z)
        {
            this.x += x;
            this.y += y;
            this.z += z;
            return this;
        }

        public MutVec3d subtract(double x, double y, double z)
        {
            this.x -= x;
            this.y -= y;
            this.z -= z;
            return this;
        }

        public Vec3d toImmutable()
        {
            return new Vec3d(this.x, this.y, this.z);
        }

        public net.minecraft.world.phys.Vec3 toVanilla()
        {
            return new net.minecraft.world.phys.Vec3(this.x, this.y, this.z);
        }
    }
}
