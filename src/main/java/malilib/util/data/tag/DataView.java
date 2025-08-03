package malilib.util.data.tag;

import java.util.Optional;
import java.util.Set;

import malilib.util.data.Constants;

public interface DataView
{
    boolean contains(String key, int requestedType);

    boolean containsList(String key, int listEntryType);

    int size();

    boolean isEmpty();

    Set<String> getKeys();

    Optional<BaseData> getData(String key);

    boolean getBoolean(String key);

    byte getByte(String key);

    short getShort(String key);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    double getDouble(String key);

    String getString(String key);

    byte[] getByteArray(String key);

    int[] getIntArray(String key);

    long[] getLongArray(String key);

    CompoundData getCompound(String key);

    ListData getList(String key, int containedType);


    default boolean getBooleanOrDefault(String key, boolean defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_BYTE) == false)
        {
            return defaultValue;
        }

        return this.getBoolean(key);
    }

    default byte getByteOrDefault(String key, byte defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_ANY_NUMERIC) == false)
        {
            return defaultValue;
        }

        return this.getByte(key);

    }

    default short getShortOrDefault(String key, short defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_ANY_NUMERIC) == false)
        {
            return defaultValue;
        }

        return this.getShort(key);

    }

    default int getIntOrDefault(String key, int defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_ANY_NUMERIC) == false)
        {
            return defaultValue;
        }

        return this.getInt(key);

    }

    default long getLongOrDefault(String key, long defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_ANY_NUMERIC) == false)
        {
            return defaultValue;
        }

        return this.getLong(key);

    }

    default float getFloatOrDefault(String key, float defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_FLOAT) == false)
        {
            return defaultValue;
        }

        return this.getFloat(key);

    }

    default double getDoubleOrDefault(String key, double defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_DOUBLE) == false)
        {
            return defaultValue;
        }

        return this.getDouble(key);

    }

    default String getStringOrDefault(String key, String defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_STRING) == false)
        {
            return defaultValue;
        }

        return this.getString(key);

    }

    default byte[] getByteArrayOrDefault(String key, byte[] defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_BYTE_ARRAY) == false)
        {
            return defaultValue;
        }

        return this.getByteArray(key);

    }

    default int[] getIntArrayOrDefault(String key, int[] defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_INT_ARRAY) == false)
        {
            return defaultValue;
        }

        return this.getIntArray(key);

    }

    default long[] getLongArrayOrDefault(String key, long[] defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_LONG_ARRAY) == false)
        {
            return defaultValue;
        }

        return this.getLongArray(key);

    }

    default CompoundData getCompoundOrDefault(String key, CompoundData defaultValue)
    {
        if (this.contains(key, Constants.NBT.TAG_COMPOUND) == false)
        {
            return defaultValue;
        }

        return this.getCompound(key);

    }

    default ListData getListOrDefault(String key, int containedType, ListData defaultValue)
    {
        Optional<BaseData> dataOpt = this.getData(key);

        if (dataOpt.isPresent() == false)
        {
            return defaultValue;
        }

        BaseData data = dataOpt.get();

        if (data.getType() != Constants.NBT.TAG_LIST)
        {
            return defaultValue;
        }

        ListData list = (ListData) data;

        if (list.getContainedType() != containedType)
        {
            return defaultValue;
        }

        return list;
    }
}
