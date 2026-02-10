package malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import com.google.common.collect.Lists;

import malilib.MaLiLib;
import malilib.util.data.Constants;
import malilib.util.data.tag.util.SizeTracker;

public class ListData extends BaseData
{
    public static final String TAG_NAME = "TAG_List";

    protected final ArrayList<BaseData> list;
    protected final int containedType;

    public ListData(int containedType)
    {
        this(containedType, new ArrayList<>());
    }

    public ListData(int containedType, ArrayList<BaseData> list)
    {
        super(Constants.NBT.TAG_LIST, TAG_NAME);

        this.containedType = containedType;
        this.list = list;
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

    public boolean set(int index, BaseData entry)
    {
        if (entry.getType() != this.containedType ||
            index < 0 ||
            index >= this.list.size())
        {
            return false;
        }

        this.list.set(index, entry);
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
    public ListData copy()
    {
        ListData copy = new ListData(this.containedType);

        for (BaseData data : this.list)
        {
            copy.list.add(data.copy());
        }

        return copy;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < this.list.size(); ++i)
        {
            if (i != 0)
            {
                sb.append(',');
            }

            sb.append(this.list.get(i).toString());
        }

        return sb.append(']').toString();
    }

    @Override
    public void write(DataOutput output) throws IOException
    {
        int containedType = this.list.isEmpty() ? Constants.NBT.TAG_END : this.containedType;
        int listSize = this.list.size();

        output.writeByte(containedType);
        output.writeInt(listSize);

        for (int i = 0; i < listSize; ++i)
        {
            this.list.get(i).write(output);
        }
    }

    public static ListData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        if (depth > 512)
        {
            throw new IOException("Tried to read NBT tag with too high complexity, depth > 512");
        }

        int tagType = input.readByte();
        int len = input.readInt();
        sizeTracker.increment(5);

        if (tagType == Constants.NBT.TAG_END && len > 0)
        {
            throw new IOException("ListData of type TAG_End");
        }

        ArrayList<BaseData> list = Lists.newArrayListWithCapacity(len);

        for (int i = 0; i < len; ++i)
        {
            BaseData data;

            try
            {
                data = BaseData.createTag(tagType, input, depth + 1, sizeTracker);
            }
            catch (IOException e)
            {
                MaLiLib.LOGGER.warn("Failed to read data for list member at index {}", i);
                throw e;
            }

            if (data == null)
            {
                throw new IOException("ListData: Failed to read entry at index " + i);
            }

            list.add(data);
        }

        return new ListData(tagType, list);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        ListData other = (ListData) o;

        if (this.containedType != other.containedType) { return false; }
        return Objects.equals(this.list, other.list);
    }

    @Override
    public int hashCode()
    {
        int result = this.list != null ? this.list.hashCode() : 0;
        result = 31 * result + this.containedType;
        return result;
    }
}
