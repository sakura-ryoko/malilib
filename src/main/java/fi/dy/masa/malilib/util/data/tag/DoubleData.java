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
}
