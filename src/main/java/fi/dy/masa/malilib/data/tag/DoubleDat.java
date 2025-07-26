package fi.dy.masa.malilib.data.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class DoubleDat extends Dat<Double>
{
    public static final Codec<DoubleDat> CODEC = Codec.of(
            new Encoder<>()
            {
                @Override
                public <T> DataResult<T> encode(DoubleDat input, DynamicOps<T> ops, T prefix)
                {
                    return PrimitiveCodec.DOUBLE.encode(input.getValue(), ops, prefix);
                }
            },
            new Decoder<>()
            {
                @Override
                public <T> DataResult<Pair<DoubleDat, T>> decode(DynamicOps<T> ops, T input)
                {
                    return PrimitiveCodec.DOUBLE.decode(ops, input).map(
                            e ->
                                    Pair.of(new DoubleDat(e.getFirst()), ops.empty())
                    );
                }
            }
    );

    public DoubleDat(Double value)
    {
        super(Double.class, value);
    }
}
