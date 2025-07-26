package fi.dy.masa.malilib.data.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class LongDat extends Dat<Long>
{
    public static final Codec<LongDat> CODEC = Codec.of(
            new Encoder<>()
            {
                @Override
                public <T> DataResult<T> encode(LongDat input, DynamicOps<T> ops, T prefix)
                {
                    return PrimitiveCodec.LONG.encode(input.getValue(), ops, prefix);
                }
            },
            new Decoder<>()
            {
                @Override
                public <T> DataResult<Pair<LongDat, T>> decode(DynamicOps<T> ops, T input)
                {
                    return PrimitiveCodec.LONG.decode(ops, input).map(
                            e ->
                                    Pair.of(new LongDat(e.getFirst()), ops.empty())
                    );
                }
            }
    );

    public LongDat(Long value)
    {
        super(Long.class, Type.LONG, value);
    }
}
