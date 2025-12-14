package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import com.google.common.collect.Lists;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

public class ListData extends BaseData
        implements ArrayData
{
    public static final String TAG_NAME = "TAG_List";
    protected final ArrayList<BaseData> list;

    public ListData()
    {
        this(new ArrayList<>());
    }

    public ListData(ArrayList<BaseData> list)
    {
        super(Constants.NBT.TAG_LIST, TAG_NAME);

        this.list = list;
    }

    // Fixes incompatibilities with Vanilla; since a Vanilla NbtList
    // calculates the Contained Type based on the data added to it.
    public int getContainedType()
    {
        int type = Constants.NBT.TAG_END;

        for (BaseData entry : this.list)
        {
            int dataType = entry.type;

            if (type == Constants.NBT.TAG_END)
            {
                type = dataType;
            }
            else if (type != dataType)
            {
                return Constants.NBT.TAG_COMPOUND;
            }
        }

        return type;
    }

    @Override
    public int size()
    {
        return this.list.size();
    }

    @Override
    public void clear()
    {
        this.list.clear();
    }

    @Override
    public boolean set(int index, BaseData value)
    {
        int type = this.getContainedType();

        if (type == Constants.NBT.TAG_END || value.type != type)
        {
            return false;
        }

        if (index < this.size() && index >= 0)
        {
            this.list.set(index, value);
        }

        return false;
    }

    @Override
    public boolean add(int index, BaseData value)
    {
        int type = this.getContainedType();

        if (type == Constants.NBT.TAG_END || value.type != type)
        {
            return false;
        }

        if (index < this.size() && index >= 0)
        {
            this.list.add(index, value);
        }

        return false;
    }

    @Override
    public BaseData remove(int index)
    {
        if (index < this.list.size())
        {
            return this.list.remove(index);
        }

        return EmptyData.INSTANCE;
    }

    public boolean add(BaseData entry)
    {
        int type = this.getContainedType();

        if (type != Constants.NBT.TAG_END &&
            entry.getType() != type)
        {
            return false;
        }

        this.list.add(entry);
        return true;
    }

    public boolean addAll(ListData listData)
    {
        for (BaseData entry : listData.list)
        {
            this.add(entry);
        }

        return true;
    }

    @Override
    public BaseData get(int index)
    {
        return this.list.get(index);
    }

    public byte getByteAt(int index)
    {
        if (this.list.get(index).type == Constants.NBT.TAG_BYTE)
        {
            return ((ByteData) this.list.get(index)).value;
        }

        return 0;
    }

    public short getShortAt(int index)
    {
        if (this.list.get(index).type == Constants.NBT.TAG_SHORT)
        {
            return ((ShortData) this.list.get(index)).value;
        }

        return 0;
    }

    public int getIntAt(int index)
    {
        if (this.list.get(index).type == Constants.NBT.TAG_INT)
        {
            return ((IntData) this.list.get(index)).value;
        }

        return 0;
    }

    public long getLongAt(int index)
    {
        if (this.list.get(index).type == Constants.NBT.TAG_LONG)
        {
            return ((LongData) this.list.get(index)).value;
        }

        return 0;
    }

    public float getFloatAt(int index)
    {
        if (this.list.get(index).type == Constants.NBT.TAG_FLOAT)
        {
            return ((FloatData) this.list.get(index)).value;
        }

        return 0.0f;
    }

    public double getDoubleAt(int index)
    {
        if (this.list.get(index).type == Constants.NBT.TAG_DOUBLE)
        {
            return ((DoubleData) this.list.get(index)).value;
        }

        return 0.0;
    }

    public CompoundData getCompoundAt(int index)
    {
        if (this.list.get(index).type == Constants.NBT.TAG_COMPOUND)
        {
            return (CompoundData) this.list.get(index);
        }

        return new CompoundData();
    }

    @Override
    public ListData copy()
    {
        ListData copy = new ListData();

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
        int containedType = this.list.isEmpty() ? Constants.NBT.TAG_END : this.getContainedType();
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

        return new ListData(list);
    }
}
