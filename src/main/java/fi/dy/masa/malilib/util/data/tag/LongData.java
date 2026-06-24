package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class LongData extends BaseData
        implements NumberData
{
    public static final String TAG_NAME = "TAG_Long";

    public final long value;

    public LongData(long value)
    {
        super(Constants.NBT.TAG_LONG, TAG_NAME);

        this.value = value;
    }

    public long getLong()
    {
        return this.value;
    }

    @Override
    public LongData copy()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return this.value + "L";
    }

    @Override
    public Optional<Number> asNumber()
    {
        return Optional.of(this.value);
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeLong(this.value);
    }

    public static LongData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        sizeTracker.increment(8);
        return new LongData(input.readLong());
    }
}
