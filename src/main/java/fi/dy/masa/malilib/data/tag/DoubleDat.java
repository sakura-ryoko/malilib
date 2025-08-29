package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;

@ApiStatus.Experimental
public class DoubleDat implements IDat<Double>
{
	private Double value;

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
        this.value = value;
    }

	@Override
	public Double getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Double newValue)
	{
		this.value = newValue;
	}

	@Override
	public DatType getType()
	{
		return DatType.DOUBLE;
	}

	@Override
	public NbtElement toVanilla()
	{
		return NbtDouble.of(this.value);
	}
}
