package malilib.util.nbt;

import malilib.util.data.Constants;

public class DoubleData extends BaseData
{
    protected final double value;

    public DoubleData(double value)
    {
        super(Constants.NBT.TAG_DOUBLE, "TAG_Double");

        this.value = value;
    }

    @Override
    public DoubleData copy()
    {
        return this;
    }
}
