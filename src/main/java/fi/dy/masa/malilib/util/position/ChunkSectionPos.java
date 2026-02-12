package fi.dy.masa.malilib.util.position;

import java.util.Comparator;
import javax.annotation.Nonnull;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

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
                    PrimitiveCodec.INT.fieldOf("x").forGetter(net.minecraft.core.Vec3i::getX),
                    PrimitiveCodec.INT.fieldOf("y").forGetter(net.minecraft.core.Vec3i::getY),
                    PrimitiveCodec.INT.fieldOf("z").forGetter(net.minecraft.core.Vec3i::getZ)
            ).apply(inst, ChunkSectionPos::new)
    );
    public static final Codec<ChunkSectionPos> CODEC = VEC3I_CODEC;
    public static final StreamCodec<@NotNull ByteBuf, @NotNull ChunkSectionPos> PACKET_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, ChunkSectionPos value)
        {
            ByteBufCodecs.INT.encode(buf, value.getX());
            ByteBufCodecs.INT.encode(buf, value.getY());
            ByteBufCodecs.INT.encode(buf, value.getZ());
        }

        @Override
        public @Nonnull ChunkSectionPos decode(@Nonnull ByteBuf buf)
        {
            return new ChunkSectionPos(
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf)
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

    @Override
    public @NonNull String toString()
    {
        return "ChunkSectionPos{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }

    public static ChunkSectionPos ofBlockPos(net.minecraft.core.Vec3i blockPos)
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
