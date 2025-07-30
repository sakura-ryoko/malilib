package malilib.util.data.tag;

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

    public void clear()
    {
        this.list.clear();
    }

    public boolean remove(int index)
    {
        if (index < this.list.size())
        {
            this.list.remove(index);
            return true;
        }

        return false;
    }

    public boolean add(BaseData entry)
    {
        if (entry.getType() != this.containedType)
        {
            return false;
        }

        this.list.add(entry);
        return true;
    }

    public BaseData get(int index)
    {
        return this.list.get(index);
    }

    public byte getByteAt(int index)
    {
        if (this.containedType == Constants.NBT.TAG_BYTE)
        {
            return ((ByteData) this.list.get(index)).value;
        }

        return 0;
    }

    public short getShortAt(int index)
    {
        if (this.containedType == Constants.NBT.TAG_SHORT)
        {
            return ((ShortData) this.list.get(index)).value;
        }

        return 0;
    }

    public int getIntAt(int index)
    {
        if (this.containedType == Constants.NBT.TAG_INT)
        {
            return ((IntData) this.list.get(index)).value;
        }

        return 0;
    }

    public long getLongAt(int index)
    {
        if (this.containedType == Constants.NBT.TAG_LONG)
        {
            return ((LongData) this.list.get(index)).value;
        }

        return 0;
    }

    public float getFloatAt(int index)
    {
        if (this.containedType == Constants.NBT.TAG_FLOAT)
        {
            return ((FloatData) this.list.get(index)).value;
        }

        return 0.0f;
    }

    public double getDoubleAt(int index)
    {
        if (this.containedType == Constants.NBT.TAG_DOUBLE)
        {
            return ((DoubleData) this.list.get(index)).value;
        }

        return 0.0;
    }

    public CompoundData getCompoundAt(int index)
    {
        if (this.containedType == Constants.NBT.TAG_COMPOUND)
        {
            return (CompoundData) this.list.get(index);
        }

        return new CompoundData();
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
