package fi.dy.masa.malilib.util.position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import fi.dy.masa.malilib.util.MathUtils;

public class BlockPos extends Vec3i
{
    public static final Codec<BlockPos> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("x").forGetter(Vec3i::getX),
                    PrimitiveCodec.INT.fieldOf("y").forGetter(net.minecraft.util.math.Vec3i::getY),
                    PrimitiveCodec.INT.fieldOf("z").forGetter(net.minecraft.util.math.Vec3i::getZ)
            ).apply(inst, BlockPos::new)
    );
    public static final PacketCodec<@NotNull ByteBuf, @NotNull BlockPos> PACKET_CODEC = new PacketCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, BlockPos value)
        {
            PacketCodecs.INTEGER.encode(buf, value.getX());
            PacketCodecs.INTEGER.encode(buf, value.getY());
            PacketCodecs.INTEGER.encode(buf, value.getZ());
        }

        @Override
        public @Nonnull BlockPos decode(@Nonnull ByteBuf buf)
        {
            return new BlockPos(
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf)
            );
        }
    };
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    public BlockPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public BlockPos(net.minecraft.util.math.Vec3i pos)
    {
        super(pos.getX(), pos.getY(), pos.getZ());
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
    public BlockPos add(net.minecraft.util.math.Vec3i other)
    {
        return this.add(other.getX(), other.getY(), other.getZ());
    }

    @Override
    public BlockPos subtract(net.minecraft.util.math.Vec3i other)
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

    public net.minecraft.util.math.BlockPos toVanillaPos()
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

    public BlockPos offset(final int x, final int y, final int z)
    {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    @Override
    public BlockPos north()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
    }

    @Override
    public BlockPos south()
    {
        return new BlockPos(this.getX(), this.getY(), this.getZ() + 1);
    }

    @Override
    public BlockPos west()
    {
        return new BlockPos(this.getX() - 1, this.getY(), this.getZ());
    }

    @Override
    public BlockPos east()
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
    public String toString()
    {
        return "BlockPos:{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
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
    public static BlockPos of(@Nullable net.minecraft.util.math.BlockPos pos)
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

        public MutBlockPos(net.minecraft.util.math.Vec3i pos)
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

        public MutBlockPos setX(int x)
        {
            this.x = x;
            return this;
        }

        public MutBlockPos setY(int y)
        {
            this.y = y;
            return this;
        }

        public MutBlockPos setZ(int z)
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

        public MutBlockPos set(net.minecraft.util.math.Vec3i pos)
        {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            return this;
        }

        public MutBlockPos setOffset(net.minecraft.util.math.Vec3i pos, net.minecraft.util.math.Direction direction)
        {
            this.x = pos.getX() + this.getAxisOffset(direction, net.minecraft.util.math.Direction.Axis.X);
            this.y = pos.getY() + this.getAxisOffset(direction, net.minecraft.util.math.Direction.Axis.Y);
            this.z = pos.getZ() + this.getAxisOffset(direction, net.minecraft.util.math.Direction.Axis.Z);
            return this;
        }

        // Wrapper for missing Vanilla method
        public int getAxisOffset(net.minecraft.util.math.Direction direction, net.minecraft.util.math.Direction.Axis axis)
        {
            return direction.getAxis() == net.minecraft.util.math.Direction.Axis.X ? direction.getDirection().offset() : 0;
        }

        public MutBlockPos setOffset(net.minecraft.util.math.Vec3i pos, Direction direction)
        {
            return this.setOffset(pos, direction, 1);
        }

        public MutBlockPos setOffset(net.minecraft.util.math.Vec3i pos, Direction direction, int amount)
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

        public MutBlockPos addMut(net.minecraft.util.math.Vec3i pos)
        {
            this.x += pos.getX();
            this.y += pos.getY();
            this.z += pos.getZ();
            return this;
        }

        public MutBlockPos subtractMut(net.minecraft.util.math.Vec3i pos)
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
        public net.minecraft.util.math.BlockPos toVanillaPos()
        {
            return new net.minecraft.util.math.BlockPos(this.x, this.y, this.z);
        }
    }
}
