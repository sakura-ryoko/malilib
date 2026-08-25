package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;
import fi.dy.masa.malilib.util.data.tag.util.SizeTrackerException;

public class LongData extends BaseData implements NumberData
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
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public int sizeInBytes()
    {
        return Long.BYTES;
    }

    @Override
    public Optional<Number> asNumber()
    {
        return Optional.of(this.value);
    }

    @Override
    public void write(DataOutput output) throws IOException, SizeTrackerException
    {
        output.writeLong(this.value);
    }

    public static LongData read(DataInput input, int depth, SizeTracker sizeTracker)
            throws IOException, SizeTrackerException
    {
        sizeTracker.increment(Long.BYTES);
        return new LongData(input.readLong());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        LongData other = (LongData) o;

        return this.value == other.value;
    }

    @Override
    public int hashCode()
    {
        return (int) (this.value ^ (this.value >>> 32));
    }
}
