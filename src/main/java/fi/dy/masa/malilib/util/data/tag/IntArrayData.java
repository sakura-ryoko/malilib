package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class IntArrayData extends BaseData
{
    public static final String TAG_NAME = "TAG_IntArray";

    public final int[] value;

    public IntArrayData(int[] value)
    {
        super(Constants.NBT.TAG_INT_ARRAY, TAG_NAME);

        this.value = value;
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
}
