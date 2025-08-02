package malilib.util.data.tag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import malilib.util.data.Constants;

public class CompoundData extends BaseData implements DataView
{
    private final LinkedHashMap<String, BaseData> values = new LinkedHashMap<>();

    public CompoundData()
    {
        super(Constants.NBT.TAG_LIST, "TAG_Compound");
    }

    @Override
    public int size()
    {
        return this.values.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.values.isEmpty();
    }

    @Override
    public Set<String> getKeys()
    {
        return this.values.keySet();
    }

    @Override
    public boolean contains(String key, int requestedType)
    {
        BaseData data = this.values.get(key);

        if (data == null)
        {
            return false;
        }

        int hasType = data.getType();

        if (hasType == requestedType)
        {
            return true;
        }

        if (requestedType == Constants.NBT.TAG_ANY_NUMERIC)
        {
            return hasType == Constants.NBT.TAG_BYTE ||
                   hasType == Constants.NBT.TAG_SHORT ||
                   hasType == Constants.NBT.TAG_INT ||
                   hasType == Constants.NBT.TAG_LONG ||
                   hasType == Constants.NBT.TAG_FLOAT ||
                   hasType == Constants.NBT.TAG_DOUBLE;
        }

        return false;
    }

    @Override
    public boolean containsList(String key, int listEntryType)
    {
        BaseData data = this.values.get(key);

        return data != null &&
               data.getType() == Constants.NBT.TAG_LIST &&
               ((ListData) data).getContainedType() == listEntryType;
    }

    public boolean remove(String key)
    {
        return this.values.remove(key) != null;
    }

    @Override
    public Optional<BaseData> getData(String key)
    {
        return Optional.ofNullable(this.values.get(key));
    }

    @Override
    public boolean getBoolean(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_BYTE && ((ByteData) data).value != 0;
    }

    @Override
    public byte getByte(String key)
    {
        BaseData data = this.values.get(key);

        if (data == null)
        {
            return 0;
        }

        if (data.getType() == Constants.NBT.TAG_BYTE)
        {
            return ((ByteData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_SHORT)
        {
            return (byte) ((ShortData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_INT)
        {
            return (byte) ((IntData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_LONG)
        {
            return (byte) ((LongData) data).value;
        }

        return 0;
    }

    @Override
    public short getShort(String key)
    {
        BaseData data = this.values.get(key);

        if (data == null)
        {
            return 0;
        }

        if (data.getType() == Constants.NBT.TAG_SHORT)
        {
            return ((ShortData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_INT)
        {
            return (short) ((IntData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_LONG)
        {
            return (short) ((LongData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_BYTE)
        {
            return ((ByteData) data).value;
        }

        return 0;
    }

    @Override
    public int getInt(String key)
    {
        BaseData data = this.values.get(key);

        if (data == null)
        {
            return 0;
        }

        if (data.getType() == Constants.NBT.TAG_INT)
        {
            return ((IntData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_LONG)
        {
            return (int) ((LongData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_SHORT)
        {
            return ((ShortData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_BYTE)
        {
            return ((ByteData) data).value;
        }

        return 0;
    }

    @Override
    public long getLong(String key)
    {
        BaseData data = this.values.get(key);

        if (data == null)
        {
            return 0;
        }

        if (data.getType() == Constants.NBT.TAG_LONG)
        {
            return ((LongData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_INT)
        {
            return ((IntData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_SHORT)
        {
            return ((ShortData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_BYTE)
        {
            return ((ByteData) data).value;
        }

        return 0;
    }

    @Override
    public float getFloat(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_FLOAT ? ((FloatData) data).value : 0.0f;
    }

    @Override
    public double getDouble(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_DOUBLE ? ((DoubleData) data).value : 0.0;
    }

    @Override
    public String getString(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_STRING ? ((StringData) data).value : "";
    }

    @Override
    public byte[] getByteArray(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_BYTE_ARRAY ? ((ByteArrayData) data).value : new byte[0];
    }

    @Override
    public int[] getIntArray(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_INT_ARRAY ? ((IntArrayData) data).value : new int[0];
    }

    @Override
    public long[] getLongArray(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_LONG_ARRAY ? ((LongArrayData) data).value : new long[0];
    }

    @Override
    public CompoundData getCompound(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_COMPOUND ? (CompoundData) data : new CompoundData();
    }

    @Override
    public ListData getList(String key, int containedType)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_LIST ? (ListData) data : new ListData(containedType);
    }


    public CompoundData putBoolean(String key, boolean value)
    {
        this.values.put(key, new ByteData(value ? (byte) 1 : 0));
        return this;
    }

    public CompoundData putByte(String key, byte value)
    {
        this.values.put(key, new ByteData(value));
        return this;
    }

    public CompoundData putShort(String key, short value)
    {
        this.values.put(key, new ShortData(value));
        return this;
    }

    public CompoundData putInt(String key, int value)
    {
        this.values.put(key, new IntData(value));
        return this;
    }

    public CompoundData putLong(String key, long value)
    {
        this.values.put(key, new LongData(value));
        return this;
    }

    public CompoundData putFloat(String key, float value)
    {
        this.values.put(key, new FloatData(value));
        return this;
    }

    public CompoundData putDouble(String key, double value)
    {
        this.values.put(key, new DoubleData(value));
        return this;
    }

    public CompoundData putString(String key, String value)
    {
        this.values.put(key, new StringData(value));
        return this;
    }

    public CompoundData putByteArray(String key, byte[] value)
    {
        this.values.put(key, new ByteArrayData(value));
        return this;
    }

    public CompoundData putIntArray(String key, int[] value)
    {
        this.values.put(key, new IntArrayData(value));
        return this;
    }

    public CompoundData putLongArray(String key, long[] value)
    {
        this.values.put(key, new LongArrayData(value));
        return this;
    }

    public CompoundData put(String key, BaseData value)
    {
        this.values.put(key, value);
        return this;
    }

    @Override
    public CompoundData copy()
    {
        CompoundData copy = new CompoundData();

        for (Map.Entry<String, BaseData> entry : this.values.entrySet())
        {
            copy.values.put(entry.getKey(), entry.getValue().copy());
        }

        return copy;
    }

    public <T> T convertTo(Function<CompoundData, T> converter)
    {
        return converter.apply(this);
    }
}
