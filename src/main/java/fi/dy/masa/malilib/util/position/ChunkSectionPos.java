package fi.dy.masa.malilib.util.position;

import java.util.Comparator;
import javax.annotation.Nonnull;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class ChunkSectionPos extends Vec3i
{
    public static final Codec<ChunkSectionPos> BLOCK_POS_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(get ->
                                                                    new BlockPos(get.getX(), get.getY(), get.getZ()))
            ).apply(inst, ChunkSectionPos::new)
    );
    public static final Codec<ChunkSectionPos> VEC3I_CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("x").forGetter(net.minecraft.util.math.Vec3i::getX),
                    PrimitiveCodec.INT.fieldOf("y").forGetter(net.minecraft.util.math.Vec3i::getY),
                    PrimitiveCodec.INT.fieldOf("z").forGetter(net.minecraft.util.math.Vec3i::getZ)
            ).apply(inst, ChunkSectionPos::new)
    );
    public static final Codec<ChunkSectionPos> CODEC = VEC3I_CODEC;
    public static final PacketCodec<@NotNull ByteBuf, @NotNull ChunkSectionPos> PACKET_CODEC = new PacketCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, ChunkSectionPos value)
        {
            PacketCodecs.INTEGER.encode(buf, value.getX());
            PacketCodecs.INTEGER.encode(buf, value.getY());
            PacketCodecs.INTEGER.encode(buf, value.getZ());
        }

        @Override
        public @Nonnull ChunkSectionPos decode(@Nonnull ByteBuf buf)
        {
            return new ChunkSectionPos(
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf),
                    PacketCodecs.INTEGER.decode(buf)
            );
        }
    };

    public ChunkSectionPos(BlockPos pos)
    {
        this(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    public ChunkSectionPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public static ChunkSectionPos of(final ChunkPos pos, final int y)
    {
        return new ChunkSectionPos(pos.x(), y, pos.z());
    }

    public static int x(final long node)
    {
        return (int) (node << 0 >> 42);
    }

    public static int y(final long node)
    {
        return (int) (node << 44 >> 44);
    }

    public static int z(final long node)
    {
        return (int) (node << 22 >> 42);
    }

    public static int toCoord(final int section)
    {
        return section << 4;
    }

    public static int toCoord(final int section, final int offset)
    {
        return toCoord(section) + offset;
    }

    public int minX()
    {
        return toCoord(this.getX());
    }

    public int minY()
    {
        return toCoord(this.getY());
    }

    public int minZ()
    {
        return toCoord(this.getZ());
    }

    public int maxX()
    {
        return toCoord(this.getX(), 15);
    }

    public int maxY()
    {
        return toCoord(this.getY(), 15);
    }

    public int maxZ()
    {
        return toCoord(this.getZ(), 15);
    }

    public BlockPos origin()
    {
        return new BlockPos(toCoord(this.getX()), toCoord(this.getY()), toCoord(this.getZ()));
    }

    public BlockPos center()
    {
        return this.origin().offset(8, 8, 8);
    }

    public ChunkSectionPos offset(final int x, final int y, final int z)
    {
        return x == 0 && y == 0 && z == 0
               ? this
               : new ChunkSectionPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public static long asLong(final int x, final int y, final int z)
    {
        long l = 0L;
        l |= (x & 4194303L) << 42;
        l |= (y & 1048575L) << 0;
        return l | (z & 4194303L) << 20;
    }

    @Override
    public String toString()
    {
        return "ChunkSectionPos{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }

    public static ChunkSectionPos of(net.minecraft.util.math.Vec3i blockPos)
    {
        return new ChunkSectionPos(blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4);
    }

    public static class DistanceComparator implements Comparator<ChunkSectionPos>
    {
        private final ChunkSectionPos referencePosition;

        public DistanceComparator(ChunkSectionPos referencePosition)
        {
            this.referencePosition = referencePosition;
        }

        @Override
        public int compare(ChunkSectionPos pos1, ChunkSectionPos pos2)
        {
            double dist1 = pos1.getSquaredDistanceTo(this.referencePosition);
            double dist2 = pos2.getSquaredDistanceTo(this.referencePosition);

            return Double.compare(dist1, dist2);
        }
    }
}
