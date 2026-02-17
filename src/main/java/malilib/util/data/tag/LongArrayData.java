package malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import malilib.util.data.Constants;
import malilib.util.data.tag.util.SizeTracker;

public class LongArrayData extends BaseData
{
    public static final String TAG_NAME = "TAG_LongArray";

    public final long[] value;

    public LongArrayData(long[] value)
    {
        super(Constants.NBT.TAG_LONG_ARRAY, TAG_NAME);

        this.value = value != null ? value : new long[0];
    }

    public long[] getLongArray()
    {
        return this.value;
    }

    @Override
    public LongArrayData copy()
    {
        long[] arr = new long[this.value.length];
        System.arraycopy(this.value, 0, arr, 0, arr.length);
        return new LongArrayData(arr);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[L;");

        for (int i = 0; i < this.value.length; ++i)
        {
            if (i != 0)
            {
                sb.append(',');
            }

            sb.append(this.value[i]).append('L');
        }

        return sb.append(']').toString();
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeInt(this.value.length);

        for (long i : this.value)
        {
            output.writeLong(i);
        }
    }

    public static LongArrayData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        int len = input.readInt();
        sizeTracker.increment(len * 8 + 4);

        long[] arr = new long[len];

        for (int i = 0; i < len; ++i)
        {
            arr[i] = input.readLong();
        }

        return new LongArrayData(arr);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        LongArrayData other = (LongArrayData) o;

        return Arrays.equals(this.value, other.value);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(this.value);
    }
}
