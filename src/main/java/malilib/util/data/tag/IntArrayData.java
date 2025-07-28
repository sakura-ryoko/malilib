package malilib.util.data.tag;

import malilib.util.data.Constants;

public class IntArrayData extends BaseData
{
    protected final int[] value;

    public IntArrayData(int[] value)
    {
        super(Constants.NBT.TAG_INT_ARRAY, "TAG_IntArray");

        this.value = value;
    }

    @Override
    public IntArrayData copy()
    {
        int[] arr = new int[this.value.length];
        System.arraycopy(this.value, 0, arr, 0, arr.length);
        return new IntArrayData(arr);
    }
}
