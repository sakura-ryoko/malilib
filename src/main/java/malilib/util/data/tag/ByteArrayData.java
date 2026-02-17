package malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import malilib.util.data.Constants;
import malilib.util.data.tag.util.SizeTracker;

public class ByteArrayData extends BaseData
{
    public static final String TAG_NAME = "TAG_ByteArray";

    public final byte[] value;

    public ByteArrayData(byte[] value)
    {
        super(Constants.NBT.TAG_BYTE_ARRAY, TAG_NAME);

        this.value = value != null ? value : new byte[0];
    }

    public byte[] getByteArray()
    {
        return this.value;
    }

    @Override
    public ByteArrayData copy()
    {
        byte[] arr = new byte[this.value.length];
        System.arraycopy(this.value, 0, arr, 0, arr.length);
        return new ByteArrayData(arr);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[B;");

        for (int i = 0; i < this.value.length; ++i)
        {
            if (i != 0)
            {
                sb.append(',');
            }

            sb.append(this.value[i]).append('B');
        }

        return sb.append(']').toString();
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeInt(this.value.length);
        output.write(this.value);
    }

    public static ByteArrayData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        int len = input.readInt();
        sizeTracker.increment(len + 4);

        byte[] arr = new byte[len];
        input.readFully(arr);

        return new ByteArrayData(arr);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ByteArrayData other = (ByteArrayData) o;

        return Arrays.equals(this.value, other.value);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(this.value);
    }
}
