package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;

@ApiStatus.Experimental
public class IntDat implements IDat<Integer>
{
	private Integer value;

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
        this.value = value;
    }

	@Override
	public Integer getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Integer newValue)
	{
		this.value = newValue;
	}

	@Override
	public DatType getType()
	{
		return DatType.INT;
	}

	@Override
	public NbtElement toVanilla()
	{
		return NbtInt.of(this.value);
	}
}
