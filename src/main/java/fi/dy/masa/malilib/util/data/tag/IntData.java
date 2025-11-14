package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class IntData extends BaseData
        implements NumberData
{
    public static final String TAG_NAME = "TAG_Int";

    public final int value;

    public IntData(int value)
    {
        super(Constants.NBT.TAG_INT, TAG_NAME);

        this.value = value;
    }

    public int getInt()
    {
        return this.value;
    }

    @Override
    public IntData copy()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return this.value + "";
    }

    @Override
    public Optional<Number> asNumber()
    {
        return Optional.of(this.value);
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeInt(this.value);
    }

    public static IntData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        sizeTracker.increment(4);
        return new IntData(input.readInt());
    }
}
