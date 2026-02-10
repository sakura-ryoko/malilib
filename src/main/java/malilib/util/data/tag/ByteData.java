package malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import malilib.util.data.Constants;
import malilib.util.data.tag.util.SizeTracker;

public class ByteData extends BaseData
{
    public static final String TAG_NAME = "TAG_Byte";

    public final byte value;

    public ByteData(byte value)
    {
        super(Constants.NBT.TAG_BYTE, TAG_NAME);

        this.value = value;
    }

    public byte getByte()
    {
        return this.value;
    }

    @Override
    public ByteData copy()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return this.value + "b";
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeByte(this.value);
    }

    public static ByteData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        sizeTracker.increment(1);
        return new ByteData(input.readByte());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ByteData other = (ByteData) o;

        return this.value == other.value;
    }

    @Override
    public int hashCode()
    {
        return this.value;
    }
}
