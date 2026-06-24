package fi.dy.masa.malilib.util.data.tag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.data.tag.util.DataOps;
import fi.dy.masa.malilib.util.data.tag.util.SizeTracker;
import fi.dy.masa.malilib.util.log.AnsiLogger;

public class CompoundData extends BaseData implements DataView
{
	private static final AnsiLogger LOGGER = new AnsiLogger(CompoundData.class, true, true);

    public static final String TAG_NAME = "TAG_Compound";
    private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");

    // Linked map so that it maintains tag insertion order when iterating
    private final LinkedHashMap<String, BaseData> values;

    public CompoundData()
    {
        this(new LinkedHashMap<>());
    }

    public CompoundData(LinkedHashMap<String, BaseData> values)
    {
        super(Constants.NBT.TAG_COMPOUND, TAG_NAME);

        this.values = values;
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

    public Set<Map.Entry<String, BaseData>> entrySet()
    {
        return this.values.entrySet();
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

//		LOGGER.debug("contains: req [{}], has [{}]", requestedType, hasType);

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

		if (data != null)
		{
			LOGGER.debug("containsList: req [{}], has [{}]", listEntryType, ((ListData) data).getContainedType());
		}
		else
		{
			LOGGER.debug("containsList: req [{}], has: [NULL]", listEntryType);
		}

        return data != null &&
               data.getType() == Constants.NBT.TAG_LIST &&
               ((ListData) data).getContainedType() == listEntryType;
    }

	@Override
	public boolean containsLenient(String key)
	{
		return this.values.containsKey(key);
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

    protected long getAsAnyInt(BaseData data)
    {
        if (data.getType() == Constants.NBT.TAG_BYTE)
        {
            return ((ByteData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_SHORT)
        {
            return ((ShortData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_INT)
        {
            return ((IntData) data).value;
        }

        if (data.getType() == Constants.NBT.TAG_LONG)
        {
            return ((LongData) data).value;
        }

        return 0;
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

        return (byte) this.getAsAnyInt(data);
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

        return (short) this.getAsAnyInt(data);
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

        return (int) this.getAsAnyInt(data);
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

        return this.getAsAnyInt(data);
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
    public ListData getList(String key)
    {
        BaseData data = this.values.get(key);
        return data != null && data.getType() == Constants.NBT.TAG_LIST ? (ListData) data : new ListData();
    }

	@Override
	public <T> Optional<T> getCodec(String key, Codec<T> codec, DynamicOps<BaseData> ops)
	{
		BaseData data = this.values.get(key);

		return data == null
		       ? Optional.empty()
		       : codec.parse(ops, data)
		              .resultOrPartial(
							  e -> MaLiLib.LOGGER.error("getCodec: Failed to get field ({}={}): {}", key, data.toString(), e)
		              );
	}

    @Override
    @Deprecated
    public <T> Optional<T> getNbtCodec(String key, Codec<T> codec, DynamicOps<NbtElement> ops)
    {
        BaseData data = this.values.get(key);

        return data == null
               ? Optional.empty()
               : codec.parse(ops, DataConverterNbt.toVanillaNbt(data))
                      .resultOrPartial(
                              e -> MaLiLib.LOGGER.error("getNbtCodec: Failed to get field ({}={}): {}", key, data.toString(), e)
                      );
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

	public <T> CompoundData putCodec(String key, Codec<T> codec, @Nullable T value)
	{
		return this.putCodec(key, codec, DataOps.INSTANCE, value);
	}

	public <T> CompoundData putCodec(String key, Codec<T> codec, DynamicOps<BaseData> ops, @Nullable T value)
	{
		if (value != null)
		{
            // DataConverterNbt.fromVanillaNbt(codec.encodeStart(ops, value)
			this.values.put(key, codec.encodeStart(ops, value).getOrThrow());
		}

		return this;
	}

    @Deprecated
    public <T> CompoundData putNbtCodec(String key, Codec<T> codec, DynamicOps<NbtElement> ops, @Nullable T value)
    {
        if (value != null)
        {
            this.values.put(key, DataConverterNbt.fromVanillaNbt(codec.encodeStart(ops, value).getOrThrow()));
        }

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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("{");
        Set<String> keys = this.values.keySet();

        for (String key : keys)
        {
            if (sb.length() != 1)
            {
                sb.append(',');
            }

            sb.append(handleEscape(key));
            sb.append(':');
            sb.append(this.values.get(key));
        }

        return sb.append('}').toString();
    }

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CompoundData data)
		{
			boolean result = false;

			for (String key : this.getKeys())
			{
				result = this.values.get(key).equals(data.values.get(key));
			}

			return result;
		}

		return false;
	}

    @Override
    public void write(DataOutput output) throws IOException
    {
        for (Map.Entry<String, BaseData> entry : this.values.entrySet())
        {
            writeEntry(entry.getKey(), entry.getValue(), output);
        }

        output.writeByte(Constants.NBT.TAG_END);
    }

    public static CompoundData read(DataInput input, int depth, SizeTracker sizeTracker) throws IOException
    {
        if (depth > 512)
        {
            throw new IOException("Tried to read NBT tag with too high complexity, depth > 512");
        }

        LinkedHashMap<String, BaseData> values = new LinkedHashMap<>();

        while (true)
        {
            int tagType = input.readByte();
            sizeTracker.increment(1);

            if (tagType == Constants.NBT.TAG_END)
            {
                break;
            }

            String key = input.readUTF();
            sizeTracker.increment(2 + key.length());
	        BaseData data;

	        try
	        {
		        data = BaseData.createTag(tagType, input, depth + 1, sizeTracker);
	        }

	        catch (IOException e)
	        {
		        MaLiLib.LOGGER.warn("Failed to read data for compound member {}", key);
		        throw e;
	        }

            if (data == null)
            {
                throw new IOException("CompoundData: Failed to read entry named " + key);
            }

            values.put(key, data);
        }

        return new CompoundData(values);
    }

    public static String handleEscape(String str)
    {
        return SIMPLE_VALUE.matcher(str).matches() ? str : StringData.quoteAndEscape(str);
    }

    public static void writeEntry(String key, BaseData data, DataOutput output) throws IOException
    {
        output.writeByte(data.getType());

        if (data.getType() != Constants.NBT.TAG_END)
        {
            output.writeUTF(key);
            data.write(output);
        }
    }
}
