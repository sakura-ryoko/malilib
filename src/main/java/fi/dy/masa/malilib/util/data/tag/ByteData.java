package fi.dy.masa.malilib.util.data.tag;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public class ByteData extends BaseData
        implements NumberData
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
    public Optional<Number> asNumber()
    {
        return Optional.of(this.value);
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
}
