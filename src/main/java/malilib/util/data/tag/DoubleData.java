package malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import malilib.util.data.Constants;
import malilib.util.data.tag.util.SizeTracker;

public class DoubleData extends BaseData
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
