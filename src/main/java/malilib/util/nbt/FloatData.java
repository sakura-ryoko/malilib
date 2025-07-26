package malilib.util.nbt;

import malilib.util.data.Constants;

public class FloatData extends BaseData
{
    protected final float value;

    public FloatData(float value)
    {
        super(Constants.NBT.TAG_FLOAT, "TAG_Float");

        this.value = value;
    }

    @Override
    public FloatData copy()
    {
        return this;
    }
}
