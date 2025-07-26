package fi.dy.masa.malilib.data.tag;

import org.apache.commons.lang3.math.Fraction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FractionDat extends Dat<Fraction>
{
    public static final Codec<FractionDat> CODEC = RecordCodecBuilder.create(
            inst -> inst.group(
                    PrimitiveCodec.INT.fieldOf("numerator").forGetter(get -> get.value.getNumerator()),
                    PrimitiveCodec.INT.fieldOf("denominator").forGetter(get -> get.value.getDenominator())
            ).apply(inst, FractionDat::new)
    );

    public FractionDat(Fraction value)
    {
        super(Fraction.class, Type.FRACTION, value);
    }

    public FractionDat(final int numerator, final int denominator)
    {
        super(Fraction.class, Type.FRACTION, Fraction.getFraction(numerator, denominator));
    }
}
