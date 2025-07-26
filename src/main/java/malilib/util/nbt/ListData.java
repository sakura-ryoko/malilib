package malilib.util.nbt;

import java.util.ArrayList;

import malilib.util.data.Constants;

public class ListData extends BaseData
{
    protected final ArrayList<BaseData> list = new ArrayList<>();
    protected final int containedType;

    public ListData(int containedType)
    {
        super(Constants.NBT.TAG_LIST, "TAG_List");

        this.containedType = containedType;
    }

    public int getContainedType()
    {
        return this.containedType;
    }

    public int size()
    {
        return this.list.size();
    }

    @Override
    public BaseData copy()
    {
        ListData copy = new ListData(this.containedType);

        for (BaseData data : this.list)
        {
            copy.list.add(data.copy());
        }

        return copy;
    }
}
