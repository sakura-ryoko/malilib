package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Vec3i extends net.minecraft.core.BlockPos
{
    public static final Codec<Vec3i> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("x").forGetter(net.minecraft.core.Vec3i::getX),
                    PrimitiveCodec.INT.fieldOf("y").forGetter(net.minecraft.core.Vec3i::getY),
                    PrimitiveCodec.INT.fieldOf("z").forGetter(net.minecraft.core.Vec3i::getZ)
            ).apply(inst, Vec3i::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull Vec3i> PACKET_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, Vec3i value)
        {
            ByteBufCodecs.INT.encode(buf, value.getX());
            ByteBufCodecs.INT.encode(buf, value.getY());
            ByteBufCodecs.INT.encode(buf, value.getZ());
        }

        @Override
        public @Nonnull Vec3i decode(@Nonnull ByteBuf buf)
        {
            return new Vec3i(
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf)
            );
        }
    };
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
    public @NonNull String toString()
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
