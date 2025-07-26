package fi.dy.masa.malilib.data.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class ShortDat extends Dat<Short>
{
    public static final Codec<ShortDat> CODEC = Codec.of(
            new Encoder<>()
            {
                @Override
                public <T> DataResult<T> encode(ShortDat input, DynamicOps<T> ops, T prefix)
                {
                    return PrimitiveCodec.SHORT.encode(input.getValue(), ops, prefix);
                }
            },
            new Decoder<>()
            {
                @Override
                public <T> DataResult<Pair<ShortDat, T>> decode(DynamicOps<T> ops, T input)
                {
                    return PrimitiveCodec.SHORT.decode(ops, input).map(
                            e ->
                                    Pair.of(new ShortDat(e.getFirst()), ops.empty())
                    );
                }
            }
    );

    public ShortDat(Short value)
    {
        super(Short.class, value);
    }
}
