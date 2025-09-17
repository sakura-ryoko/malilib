package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import malilib.util.data.Constants;
import malilib.util.data.tag.util.SizeTracker;

public class FloatData extends BaseData
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
    public void write(DataOutput output) throws IOException
    {
        output.writeFloat(this.value);
    }

    public static FloatData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        sizeTracker.increment(4);
        return new FloatData(input.readFloat());
    }
}
