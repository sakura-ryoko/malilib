package malilib.util.nbt;

import malilib.util.data.Constants;

public class LongArrayData extends BaseData
{
    protected final long[] value;

    public LongArrayData(long[] value)
    {
        super(Constants.NBT.TAG_LONG_ARRAY, "TAG_LongArray");

        this.value = value;
    }

    @Override
    public LongArrayData copy()
    {
        long[] arr = new long[this.value.length];
        System.arraycopy(this.value, 0, arr, 0, arr.length);
        return new LongArrayData(arr);
    }
}
