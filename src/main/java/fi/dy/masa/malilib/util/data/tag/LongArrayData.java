package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class LongArrayData extends BaseData
        implements ArrayData
{
    public static final String TAG_NAME = "TAG_LongArray";

    public long[] value;

    public LongArrayData(long[] value)
    {
        super(Constants.NBT.TAG_LONG_ARRAY, TAG_NAME);

        this.value = value;
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
    public void clear()
    {
        this.value = new long[0];
    }

    @Override
    public boolean set(int index, BaseData value)
    {
        Optional<Number> opt = value.asNumber();

        if (index < this.size() && index >= 0
            && opt.isPresent())
        {
            this.value[index] = opt.get().longValue();
            return true;
        }

        return false;
    }

    @Override
    public boolean add(int index, BaseData value)
    {
        Optional<Number> opt = value.asNumber();

        if (index < this.size() && index >= 0
            && opt.isPresent())
        {
            this.value = ArrayUtils.add(this.value, index, opt.get().longValue());
            return true;
        }

        return false;
    }

    @Override
    public LongData remove(int index)
    {
        if (index < this.size() && index >= 0)
        {
            long entry = this.value[index];
            this.value = ArrayUtils.remove(this.value, index);
            return new LongData(entry);
        }

        return null;
    }

    @Override
    public LongData get(int index)
    {
        if (index < this.size() && index >= 0)
        {
            return new LongData(this.value[index]);
        }

        return null;
    }

    @Override
    public int size()
    {
        return this.value.length;
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
}
