package fi.dy.masa.malilib.data.tag;

import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;

@ApiStatus.Experimental
public class FractionDat implements IDat<Fraction>
{
	private Fraction value;

    public static final Codec<FractionDat> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("numerator").forGetter(get -> get.value.getNumerator()),
                    PrimitiveCodec.INT.fieldOf("denominator").forGetter(get -> get.value.getDenominator())
            ).apply(inst, FractionDat::new)
    );

    public FractionDat(Fraction value)
    {
        this.value = value;
    }

    public FractionDat(final int numerator, final int denominator)
    {
        this.value = Fraction.getFraction(numerator, denominator);
    }

	@Override
	public Fraction getValue()
	{
		return this.value;
	}

	@Override
	public void setValue(Fraction newValue)
	{
		this.value = newValue;
	}

	@Override
	public DatType getType()
	{
		return DatType.FRACTION;
	}

	@Override
	public NbtElement toVanilla()
	{
		int[] array = new int[2];

		array[0] = this.value.getNumerator();
		array[1] = this.value.getDenominator();

		return new NbtIntArray(array);
	}
}
