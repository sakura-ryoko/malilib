package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class ByteArrayData extends BaseData
        implements ArrayData
{
    public static final String TAG_NAME = "TAG_ByteArray";

    public byte[] value;

    public ByteArrayData(byte[] value)
    {
        super(Constants.NBT.TAG_BYTE_ARRAY, TAG_NAME);

        this.value = value;
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
    public void clear()
    {
        this.value = new byte[0];
    }

    @Override
    public boolean set(int index, BaseData value)
    {
        Optional<Number> opt = value.asNumber();

        if (index < this.size() && index >= 0
            && opt.isPresent())
        {
            this.value[index] = opt.get().byteValue();
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
            this.value = ArrayUtils.add(this.value, index, opt.get().byteValue());
            return true;
        }

        return false;
    }

    @Override
    @Nullable
    public ByteData remove(int index)
    {
        if (index < this.size() && index >= 0)
        {
            byte entry = this.value[index];
            this.value = ArrayUtils.remove(this.value, index);
            return new ByteData(entry);
        }

        return null;
    }

    @Override
    @Nullable
    public ByteData get(int index)
    {
        if (index < this.size() && index >= 0)
        {
            return new ByteData(this.value[index]);
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
}
