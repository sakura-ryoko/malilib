package malilib.util.nbt;

import malilib.util.data.Constants;

public class StringData extends BaseData
{
    protected final String value;

    public StringData(String value)
    {
        super(Constants.NBT.TAG_STRING, "TAG_String");

        this.value = value;
    }

    @Override
    public StringData copy()
    {
        return this;
    }
}
