package fi.dy.masa.malilib.data.tag;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

@ApiStatus.Experimental
public class BoolDat implements IDat<Boolean>
{
	private Boolean value;

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
        this.value = value;
    }

	@Override
	public Boolean getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Boolean newValue)
	{
		this.value = newValue;
	}

	@Override
	public DatType getType()
	{
		return DatType.BOOL;
	}

	@Override
	public NbtElement toVanilla()
	{
		return NbtByte.of(this.value);
	}
}
