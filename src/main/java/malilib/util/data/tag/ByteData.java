package malilib.util.data.tag;

import malilib.util.data.Constants;

public class ByteData extends BaseData
{
    protected final byte value;

    public ByteData(byte value)
    {
        super(Constants.NBT.TAG_BYTE, "TAG_Byte");

        this.value = value;
    }

    @Override
    public ByteData copy()
    {
        return this;
    }
}
