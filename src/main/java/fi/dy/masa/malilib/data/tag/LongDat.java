package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;

@ApiStatus.Experimental
public class LongDat implements IDat<Long>
{
	private Long value;

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
        this.value = value;
    }

	@Override
	public Long getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Long newValue)
	{
		this.value = newValue;
	}

	@Override
	public DatType getType()
	{
		return DatType.LONG;
	}

	@Override
	public NbtElement toVanilla()
	{
		return NbtLong.of(this.value);
	}
}
