package malilib.util.data.tag;

import malilib.util.data.Constants;

public class LongData extends BaseData
{
    protected final long value;

    public LongData(long value)
    {
        super(Constants.NBT.TAG_LONG, "TAG_Long");

        this.value = value;
    }

    @Override
    public LongData copy()
    {
        return this;
    }
}
