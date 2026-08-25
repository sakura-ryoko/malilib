package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;
import fi.dy.masa.malilib.util.data.tag.util.SizeTrackerException;

public class ShortData extends BaseData implements NumberData
{
    public static final String TAG_NAME = "TAG_Short";

    public final short value;

    public ShortData(short value)
    {
        super(Constants.NBT.TAG_SHORT, TAG_NAME);

        this.value = value;
    }

    public short getShort()
    {
        return this.value;
    }

    @Override
    public ShortData copy()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return this.value + "s";
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public int sizeInBytes()
    {
        return Short.BYTES;
    }

    @Override
    public Optional<Number> asNumber()
    {
        return Optional.of(this.value);
    }

    @Override
    public void write(DataOutput output) throws IOException, SizeTrackerException
    {
        output.writeShort(this.value);
    }

    public static ShortData read(DataInput input, int depth, SizeTracker sizeTracker)
            throws IOException, SizeTrackerException
    {
        sizeTracker.increment(Short.BYTES);
        return new ShortData(input.readShort());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ShortData other = (ShortData) o;

        return this.value == other.value;
    }

    @Override
    public int hashCode()
    {
        return this.value;
    }
}
