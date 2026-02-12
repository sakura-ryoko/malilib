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

public class ChunkPos extends net.minecraft.world.level.ChunkPos
{
    public static final Codec<ChunkPos> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("x").forGetter(ChunkPos::getX),
                    PrimitiveCodec.INT.fieldOf("z").forGetter(ChunkPos::getZ)
            ).apply(inst, ChunkPos::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull ChunkPos> PACKET_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, ChunkPos value)
        {
            ByteBufCodecs.INT.encode(buf, value.getX());
            ByteBufCodecs.INT.encode(buf, value.getZ());
        }

        @Override
        public @Nonnull ChunkPos decode(@Nonnull ByteBuf buf)
        {
            return new ChunkPos(
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf)
            );
        }
    };

    public ChunkPos(int x, int z)
    {
        super(x, z);
    }

    public int getX()
    {
        return this.x;
    }

    public int getZ()
    {
        return this.z;
    }

    @Override
    public @NonNull String toString()
    {
        return "ChunkPos{x=" + this.x + ", z=" + this.z + "}";
    }

    public static long asLong(int chunkX, int chunkZ)
    {
        return ((long) chunkZ << 32) | ((long) chunkX & 0xFFFFFFFFL);
    }

    public static ChunkPos of(net.minecraft.world.level.ChunkPos pos)
    {
        return new ChunkPos(pos.x, pos.z);
    }
}
