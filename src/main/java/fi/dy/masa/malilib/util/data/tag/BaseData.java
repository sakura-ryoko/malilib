package fi.dy.masa.malilib.util.data.tag;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public abstract class BaseData
{
    protected final int type;
    protected final String displayName;

    protected BaseData(int type, String displayName)
    {
        this.type = type;
        this.displayName = displayName;
    }

    public int getType()
    {
        return this.type;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public abstract BaseData copy();

	public abstract String toString();

    public Optional<Number> asNumber()
    {
        return Optional.empty();
    }

    public abstract void write(DataOutput output) throws IOException;

    public static BaseData createTag(int tagType, DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        switch (tagType)
        {
            case Constants.NBT.TAG_BYTE:        return ByteData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_SHORT:       return ShortData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_INT:         return IntData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_LONG:        return LongData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_FLOAT:       return FloatData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_DOUBLE:      return DoubleData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_STRING:      return StringData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_BYTE_ARRAY:  return ByteArrayData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_INT_ARRAY:   return IntArrayData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_LONG_ARRAY:  return LongArrayData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_COMPOUND:    return CompoundData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_LIST:        return ListData.read(input, depth, sizeTracker);
            case Constants.NBT.TAG_END:         return EmptyData.read(input, depth, sizeTracker);
            default:
                throw new IOException("Unknown tag type " + tagType);
        }
    }
}
