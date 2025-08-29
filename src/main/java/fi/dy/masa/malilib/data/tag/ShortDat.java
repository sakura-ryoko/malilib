package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;

@ApiStatus.Experimental
public class ShortDat implements IDat<Short>
{
	private Short value;

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
        this.value = value;
    }

	@Override
	public Short getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Short newValue)
	{
		this.value = newValue;
	}

	@Override
	public DatType getType()
	{
		return DatType.SHORT;
	}

	@Override
	public NbtElement toVanilla()
	{
		return NbtLong.of(this.value);
	}
}
