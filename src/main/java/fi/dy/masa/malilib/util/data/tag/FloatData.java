package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class FloatData extends BaseData
        implements NumberData
{
    public static final String TAG_NAME = "TAG_Float";

    public final float value;

    public FloatData(float value)
    {
        super(Constants.NBT.TAG_FLOAT, TAG_NAME);

        this.value = value;
    }

    public float getFloat()
    {
        return this.value;
    }

    @Override
    public FloatData copy()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return this.value + "f";
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public Optional<Number> asNumber()
    {
        return Optional.of(this.value);
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeFloat(this.value);
    }

    public static FloatData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        sizeTracker.increment(4);
        return new FloatData(input.readFloat());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        FloatData other = (FloatData) o;

        return Float.compare(other.value, this.value) == 0;
    }

    @Override
    public int hashCode()
    {
        return (this.value != 0.0f ? Float.floatToIntBits(this.value) : 0);
    }
}
