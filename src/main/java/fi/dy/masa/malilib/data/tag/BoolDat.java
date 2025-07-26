package fi.dy.masa.malilib.data.tag;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;

public class BoolDat extends Dat<Boolean>
{
    public static final Codec<BoolDat> CODEC = Codec.of(
            new Encoder<>()
            {
                @Override
                public <T> DataResult<T> encode(BoolDat input, DynamicOps<T> ops, T prefix)
                {
                    return PrimitiveCodec.BOOL.encode(input.getValue(), ops, prefix);
                }
            },
            new Decoder<>()
            {
                @Override
                public <T> DataResult<Pair<BoolDat, T>> decode(DynamicOps<T> ops, T input)
                {
                    return PrimitiveCodec.BOOL.decode(ops, input).map(
                            e ->
                                    Pair.of(new BoolDat(e.getFirst()), ops.empty())
                    );
                }
            }
    );

    public BoolDat(Boolean value)
    {
        super(Boolean.class, value);
    }
}
