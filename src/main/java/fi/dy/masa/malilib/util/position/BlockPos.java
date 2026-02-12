package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import fi.dy.masa.malilib.util.MathUtils;

public class BlockPos extends Vec3i
{
    public static final Codec<BlockPos> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("x").forGetter(net.minecraft.core.Vec3i::getX),
                    PrimitiveCodec.INT.fieldOf("y").forGetter(net.minecraft.core.Vec3i::getY),
                    PrimitiveCodec.INT.fieldOf("z").forGetter(net.minecraft.core.Vec3i::getZ)
            ).apply(inst, BlockPos::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull BlockPos> PACKET_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, BlockPos value)
        {
            ByteBufCodecs.INT.encode(buf, value.getX());
            ByteBufCodecs.INT.encode(buf, value.getY());
            ByteBufCodecs.INT.encode(buf, value.getZ());
        }

        @Override
        public @Nonnull BlockPos decode(@Nonnull ByteBuf buf)
        {
            return new BlockPos(
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf)
            );
        }
    };
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    public BlockPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public BlockPos(Vec3i pos)
    {
        super(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos offset(Direction direction, int amount)
    {
        return new BlockPos(this.getX() + direction.getXOffset() * amount,
                            this.getY() + direction.getYOffset() * amount,
                            this.getZ() + direction.getZOffset() * amount);
    }

    public BlockPos offset(Direction direction)
    {
        return this.offset(direction, 1);
    }

//    @Override
    public BlockPos add(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos subtract(int x, int y, int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() - x, this.getY() - y, this.getZ() - z);
    }

//    @Override
    public BlockPos add(net.minecraft.core.Vec3i other)
    {
        return this.add(other.getX(), other.getY(), other.getZ());
    }

    @Override
    public @NonNull BlockPos subtract(net.minecraft.core.Vec3i other)
    {
        return this.subtract(other.getX(), other.getY(), other.getZ());
    }

    public BlockPos add(Vec3i other)
    {
        return this.add(other.getX(), other.getY(), other.getZ());
    }

    public BlockPos subtract(Vec3i other)
    {
        return this.subtract(other.getX(), other.getY(), other.getZ());
    }

//    @Override
    public BlockPos toImmutable()
    {
        return this;
    }

    public net.minecraft.core.BlockPos toVanillaPos()
    {
        return this;
    }

//    @Override
    public BlockPos down()
    {
        return new BlockPos(this.getX(), this.getY() - 1, this.getZ());
    }

//    @Override
    public BlockPos up()
    {
        return new BlockPos(this.getX(), this.getY() + 1, this.getZ());
    }

    @Override
    public @NonNull BlockPos north()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
    }

    @Override
    public @NonNull BlockPos south()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() + 1);
    }

    @Override
    public @NonNull BlockPos west()
    {
        return new BlockPos(this.getX() - 1, this.getY(), this.getZ());
    }

    @Override
    public @NonNull BlockPos east()
    {
        return new BlockPos(this.getX() + 1, this.getY(), this.getZ());
    }

    public long toPackedLong()
    {
        int x = this.getX() & 0x3FFFFFF;
        int y = this.getY() & 0xFFF;
        int z = this.getZ() & 0x3FFFFFF;

        return ((long) x << (26 + 12)) | ((long) y << 26) | ((long) z);
    }

    @Override
    public @NonNull String toString()
    {
        return "BlockPos{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }

    public static BlockPos fromPacked(long posLong)
    {
        int x = (int) ( posLong               >> (64 - 26));
        int y = (int) ((posLong <<       26 ) >> (64 - 12));
        int z = (int) ((posLong << (26 + 12)) >> (64 - 26));

        return new BlockPos(x, y, z);
    }

    public static BlockPos ofFloored(Vec3d pos)
    {
        return ofFloored(pos.x, pos.y, pos.z);
    }

    public static BlockPos ofFloored(double x, double y, double z)
    {
        return new BlockPos(MathUtils.floor(x), MathUtils.floor(y), MathUtils.floor(z));
    }

    @Nullable
    public static BlockPos of(@Nullable net.minecraft.core.BlockPos pos)
    {
        if (pos == null)
        {
            return null;
        }

        return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static class MutBlockPos extends BlockPos
    {
        private int x;
        private int y;
        private int z;

        public MutBlockPos()
        {
            this(0, 0, 0);
        }

        public MutBlockPos(int x, int y, int z)
        {
            super(x, y, z);

            this.x = x;
            this.y = y;
            this.z = z;
        }

        public MutBlockPos(net.minecraft.core.Vec3i pos)
        {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public int getX()
        {
            return this.x;
        }

        @Override
        public int getY()
        {
            return this.y;
        }

        @Override
        public int getZ()
        {
            return this.z;
        }

        public @NonNull MutBlockPos setX(int x)
        {
            this.x = x;
            return this;
        }

        public @NonNull MutBlockPos setY(int y)
        {
            this.y = y;
            return this;
        }

        public @NonNull MutBlockPos setZ(int z)
        {
            this.z = z;
            return this;
        }

        public MutBlockPos set(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public MutBlockPos set(net.minecraft.core.Vec3i pos)
        {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            return this;
        }

        public MutBlockPos setOffset(net.minecraft.core.Vec3i pos, net.minecraft.core.Direction direction)
        {
            this.x = pos.getX() + this.getAxisOffset(direction, net.minecraft.core.Direction.Axis.X);
            this.y = pos.getY() + this.getAxisOffset(direction, net.minecraft.core.Direction.Axis.Y);
            this.z = pos.getZ() + this.getAxisOffset(direction, net.minecraft.core.Direction.Axis.Z);
            return this;
        }

        // Wrapper for missing Vanilla method
        public int getAxisOffset(net.minecraft.core.Direction direction, net.minecraft.core.Direction.Axis axis)
        {
            return direction.getAxis() == net.minecraft.core.Direction.Axis.X ? direction.getAxisDirection().getStep() : 0;
        }

        public MutBlockPos setOffset(net.minecraft.core.Vec3i pos, Direction direction)
        {
            return this.setOffset(pos, direction, 1);
        }

        public MutBlockPos setOffset(net.minecraft.core.Vec3i pos, Direction direction, int amount)
        {
            this.x = pos.getX() + direction.getXOffset() * amount;
            this.y = pos.getY() + direction.getYOffset() * amount;
            this.z = pos.getZ() + direction.getZOffset() * amount;
            return this;
        }

        public MutBlockPos move(Direction direction, int amount)
        {
            this.set(this.getX() + direction.getXOffset() * amount,
                     this.getY() + direction.getYOffset() * amount,
                     this.getZ() + direction.getZOffset() * amount);

            return this;
        }

        public MutBlockPos move(Direction direction)
        {
            return this.move(direction, 1);
        }

        public MutBlockPos addMut(Vec3i pos)
        {
            this.x += pos.getX();
            this.y += pos.getY();
            this.z += pos.getZ();
            return this;
        }

        public MutBlockPos subtractMut(Vec3i pos)
        {
            this.x -= pos.getX();
            this.y -= pos.getY();
            this.z -= pos.getZ();
            return this;
        }

        public MutBlockPos addMut(int x, int y, int z)
        {
            this.x += x;
            this.y += y;
            this.z += z;
            return this;
        }

        public MutBlockPos subtractMut(int x, int y, int z)
        {
            this.x -= x;
            this.y -= y;
            this.z -= z;
            return this;
        }

        @Override
        public BlockPos toImmutable()
        {
            return new BlockPos(this.x, this.y, this.z);
        }

        @Override
        public net.minecraft.core.BlockPos toVanillaPos()
        {
            return new net.minecraft.core.BlockPos(this.x, this.y, this.z);
        }
    }
}
