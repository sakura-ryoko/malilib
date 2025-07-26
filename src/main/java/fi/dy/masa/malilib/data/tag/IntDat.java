package fi.dy.masa.malilib.data.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class IntDat extends Dat<Integer>
{
    public static final Codec<IntDat> CODEC = Codec.of(
            new Encoder<>()
            {
                @Override
                public <T> DataResult<T> encode(IntDat input, DynamicOps<T> ops, T prefix)
                {
                    return PrimitiveCodec.INT.encode(input.getValue(), ops, prefix);
                }
            },
            new Decoder<>()
            {
                @Override
                public <T> DataResult<Pair<IntDat, T>> decode(DynamicOps<T> ops, T input)
                {
                    return PrimitiveCodec.INT.decode(ops, input).map(
                            e ->
                                    Pair.of(new IntDat(e.getFirst()), ops.empty())
                    );
                }
            }
    );

    public IntDat(Integer value)
    {
        super(Integer.class, value);
    }
}
