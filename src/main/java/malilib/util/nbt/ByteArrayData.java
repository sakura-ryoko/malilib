package malilib.util.nbt;

import malilib.util.data.Constants;

public class ByteArrayData extends BaseData
{
    protected final byte[] value;

    public ByteArrayData(byte[] value)
    {
        super(Constants.NBT.TAG_BYTE_ARRAY, "TAG_ByteArray");

        this.value = value;
    }

    @Override
    public ByteArrayData copy()
    {
        byte[] arr = new byte[this.value.length];
        System.arraycopy(this.value, 0, arr, 0, arr.length);
        return new ByteArrayData(arr);
    }
}
