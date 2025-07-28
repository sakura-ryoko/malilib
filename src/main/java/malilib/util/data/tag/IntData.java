package malilib.util.data.tag;

import malilib.util.data.Constants;

public class IntData extends BaseData
{
    protected final int value;

    public IntData(int value)
    {
        super(Constants.NBT.TAG_INT, "TAG_Int");

        this.value = value;
    }

    @Override
    public IntData copy()
    {
        return this;
    }
}
