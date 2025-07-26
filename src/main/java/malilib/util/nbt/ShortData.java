package malilib.util.nbt;

import malilib.util.data.Constants;

public class ShortData extends BaseData
{
    protected final short value;

    public ShortData(short value)
    {
        super(Constants.NBT.TAG_SHORT, "TAG_Short");

        this.value = value;
    }

    @Override
    public ShortData copy()
    {
        return this;
    }
}
