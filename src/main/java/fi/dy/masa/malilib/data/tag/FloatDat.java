package fi.dy.masa.malilib.data.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class FloatDat extends Dat<Float>
{
    public static final Codec<FloatDat> CODEC = Codec.of(
            new Encoder<>()
            {
                @Override
                public <T> DataResult<T> encode(FloatDat input, DynamicOps<T> ops, T prefix)
                {
                    return PrimitiveCodec.FLOAT.encode(input.getValue(), ops, prefix);
                }
            },
            new Decoder<>()
            {
                @Override
                public <T> DataResult<Pair<FloatDat, T>> decode(DynamicOps<T> ops, T input)
                {
                    return PrimitiveCodec.FLOAT.decode(ops, input).map(
                            e ->
                                    Pair.of(new FloatDat(e.getFirst()), ops.empty())
                    );
                }
            }
    );

    public FloatDat(Float value)
    {
        super(Float.class, value);
    }
}
