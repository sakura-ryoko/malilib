package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;

@ApiStatus.Experimental
public class FloatDat implements IDat<Float>
{
	private Float value;

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
        this.value = value;
    }

	@Override
	public Float getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Float newValue)
	{
		this.value = newValue;
	}

	@Override
	public DatType getType()
	{
		return DatType.FLOAT;
	}

	@Override
	public NbtElement toVanilla()
	{
		return NbtFloat.of(this.value);
	}
}
