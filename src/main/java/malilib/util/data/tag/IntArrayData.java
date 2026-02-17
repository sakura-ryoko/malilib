package malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import malilib.util.data.Constants;
import malilib.util.data.tag.util.SizeTracker;

public class IntArrayData extends BaseData
{
    public static final String TAG_NAME = "TAG_IntArray";

    public final int[] value;

    public IntArrayData(int[] value)
    {
        super(Constants.NBT.TAG_INT_ARRAY, TAG_NAME);

        this.value = value != null ? value : new int[0];
    }

    public int[] getIntArray()
    {
        return this.value;
    }

    @Override
    public IntArrayData copy()
    {
        int[] arr = new int[this.value.length];
        System.arraycopy(this.value, 0, arr, 0, arr.length);
        return new IntArrayData(arr);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[I;");

        for (int i = 0; i < this.value.length; ++i)
        {
            if (i != 0)
            {
                sb.append(',');
            }

            sb.append(this.value[i]);
        }

        return sb.append(']').toString();
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        output.writeInt(this.value.length);

        for (int i : this.value)
        {
            output.writeInt(i);
        }
    }

    public static IntArrayData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        int len = input.readInt();
        sizeTracker.increment(len * 4 + 4);

        int[] arr = new int[len];

        for (int i = 0; i < len; ++i)
        {
            arr[i] = input.readInt();
        }

        return new IntArrayData(arr);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        IntArrayData other = (IntArrayData) o;

        return Arrays.equals(this.value, other.value);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(this.value);
    }
}
