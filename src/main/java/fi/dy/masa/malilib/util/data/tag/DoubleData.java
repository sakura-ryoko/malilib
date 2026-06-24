package fi.dy.masa.malilib.util.data.tag;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class DoubleData extends BaseData
        implements NumberData
{
    public static final String TAG_NAME = "TAG_Double";

    public final double value;

    public DoubleData(double value)
    {
        super(Constants.NBT.TAG_DOUBLE, TAG_NAME);

        this.value = value;
    }

    public double getDouble()
    {
        return this.value;
    }

    @Override
    public DoubleData copy()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return this.value + "d";
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
        output.writeDouble(this.value);
    }

    public static DoubleData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        sizeTracker.increment(8);
        return new DoubleData(input.readDouble());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        DoubleData other = (DoubleData) o;

        return Double.compare(other.value, this.value) == 0;
    }

    @Override
    public int hashCode()
    {
        long temp = Double.doubleToLongBits(this.value);
        return (int) (temp ^ (temp >>> 32));
    }
}
