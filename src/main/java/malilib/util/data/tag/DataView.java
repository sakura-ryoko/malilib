package malilib.util.data.tag;

import java.util.Optional;
import java.util.Set;

import malilib.util.data.Constants;

public interface DataView
{
    /**
     * @return the number of tags/keys stored in this data structure
     */
    int size();

    boolean isEmpty();

    Set<String> getKeys();

    boolean contains(String key, int requestedType);

    boolean containsList(String key, int listEntryType);

    Optional<BaseData> getData(String key);

    Optional<BaseData> getData(String key, int requestedType);

    /**
     * @return the requested boolean value, or false if this key doesn't exist
     */
    boolean getBoolean(String key);

    /**
     * @return the requested byte value, or 0 if this key doesn't exist
     */
    byte getByte(String key);

    /**
     * @return the requested short value, or 0 if this key doesn't exist
     */
    short getShort(String key);

    /**
     * @return the requested int value, or 0 if this key doesn't exist
     */
    int getInt(String key);

    /**
     * @return the requested long value, or 0L if this key doesn't exist
     */
    long getLong(String key);

    /**
     * @return the requested float value, or 0.0f if this key doesn't exist
     */
    float getFloat(String key);

    /**
     * @return the requested double value, or 0.0 if this key doesn't exist
     */
    double getDouble(String key);

    /**
     * @return the requested string, or an empty string if this key doesn't exist
     */
    String getString(String key);

    /**
     * @return the requested array, or an empty array if this key doesn't exist
     */
    byte[] getByteArray(String key);

    /**
     * @return the requested array, or an empty array if this key doesn't exist
     */
    int[] getIntArray(String key);

    /**
     * @return the requested array, or an empty array if this key doesn't exist
     */
    long[] getLongArray(String key);

    /**
     * @return the requested compound tag, or an empty compound if this key doesn't exist
     */
    CompoundData getCompound(String key);

    /**
     * @return the requested list, or an empty list if this key doesn't exist
     */
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
        Optional<BaseData> dataOpt = this.getData(key, Constants.NBT.TAG_LIST);

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
