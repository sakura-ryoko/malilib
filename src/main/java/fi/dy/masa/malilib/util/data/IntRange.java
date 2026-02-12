package fi.dy.masa.malilib.util.data;

import javax.annotation.Nonnull;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class IntRange
{
    public static final Codec<IntRange> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("start").forGetter(get -> get.first),
                    PrimitiveCodec.INT.fieldOf("length").forGetter(get -> get.length)
            ).apply(inst, IntRange::new)
    );
    public static final StreamCodec<@NotNull ByteBuf, @NotNull IntRange> PACKET_CODEC = new StreamCodec<>()
    {
        @Override
        public void encode(@Nonnull ByteBuf buf, IntRange value)
        {
            ByteBufCodecs.INT.encode(buf, value.first);
            ByteBufCodecs.INT.encode(buf, value.length);
        }

        @Override
        public @Nonnull IntRange decode(@Nonnull ByteBuf buf)
        {
            return new IntRange(
                    ByteBufCodecs.INT.decode(buf),
                    ByteBufCodecs.INT.decode(buf)
            );
        }
    };

    protected final int first;
    protected final int last;
    protected final int length;

    public IntRange(int start, int length)
    {
        this.first = start;
        this.length = length;
        this.last = start + length - 1;
    }

    public int getFirst()
    {
        return this.first;
    }

    public int getLast()
    {
        return this.last;
    }

    public int getLength()
    {
        return this.length;
    }

    public boolean contains(int value)
    {
        return value >= this.first && value <= this.last;
    }

    @Override
    public String toString()
    {
        return String.format("IntRange:{first:%d,last:%d,length:%d}", this.first, this.last, this.length);
    }

    public static IntRange of(int start, int length)
    {
        return new IntRange(start, length);
    }
}
