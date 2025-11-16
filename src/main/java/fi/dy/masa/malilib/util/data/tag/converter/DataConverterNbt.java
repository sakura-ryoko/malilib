package fi.dy.masa.malilib.util.data.tag.converter;

import javax.annotation.Nullable;

import net.minecraft.nbt.*;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.*;

public class DataConverterNbt
{
//	private static final AnsiLogger LOGGER = new AnsiLogger(DataConverterNbt.class, true, true);

    @Nullable
    public static BaseData fromVanillaNbt(NbtElement vanillaTag)
    {
	    if (vanillaTag == null) return EmptyData.INSTANCE;
//		LOGGER.debug("fromVanillaNbt: type: [{}]", vanillaTag.getType());

	    return switch (vanillaTag.getType())
	    {
		    case Constants.NBT.TAG_BYTE -> new ByteData(((NbtByte) vanillaTag).value());
		    case Constants.NBT.TAG_SHORT -> new ShortData(((NbtShort) vanillaTag).value());
		    case Constants.NBT.TAG_INT -> new IntData(((NbtInt) vanillaTag).value());
		    case Constants.NBT.TAG_LONG -> new LongData(((NbtLong) vanillaTag).value());
		    case Constants.NBT.TAG_FLOAT -> new FloatData(((NbtFloat) vanillaTag).value());
		    case Constants.NBT.TAG_DOUBLE -> new DoubleData(((NbtDouble) vanillaTag).value());
		    case Constants.NBT.TAG_STRING -> new StringData(((NbtString) vanillaTag).value());
		    case Constants.NBT.TAG_BYTE_ARRAY -> new ByteArrayData(((NbtByteArray) vanillaTag).getByteArray());
		    case Constants.NBT.TAG_INT_ARRAY -> new IntArrayData(((NbtIntArray) vanillaTag).getIntArray());
		    case Constants.NBT.TAG_LONG_ARRAY -> new LongArrayData(((NbtLongArray) vanillaTag).getLongArray());
		    case Constants.NBT.TAG_COMPOUND -> fromVanillaCompound(vanillaTag.asCompound().orElse(new NbtCompound()));
		    case Constants.NBT.TAG_LIST -> fromVanillaList(vanillaTag.asNbtList().orElse(new NbtList()));
		    default -> EmptyData.INSTANCE;
	    };
    }

    public static ListData fromVanillaList(NbtList vanillaList)
    {
        ListData list = new ListData();

		if (vanillaList == null || vanillaList.isEmpty())
		{
			return list;
		}

        for (int index = 0; index < vanillaList.size(); index++)
        {
	        NbtElement entry = vanillaList.get(index);

			if (entry != null)
			{
				if (entry.getType() == Constants.NBT.TAG_END)
				{
					MaLiLib.LOGGER.warn("DataConverterNbt.fromVanillaList: Got TAG_End in a list at index {}", index);
					return list;
				}

				BaseData convertedTag = fromVanillaNbt(entry);

				if (convertedTag != null)
				{
					list.add(convertedTag);
				}
			}
        }

        return list;
    }

    public static CompoundData fromVanillaCompound(NbtCompound vanillaCompound)
    {
	    CompoundData data = new CompoundData();

	    if (vanillaCompound == null || vanillaCompound.isEmpty())
		{
			return data;
		}

        for (String key : vanillaCompound.getKeys())
        {
	        NbtElement ele = vanillaCompound.get(key);

			if (ele != null)
			{
				BaseData convertedTag = fromVanillaNbt(ele);

				if (convertedTag != null)
				{
					data = data.put(key, convertedTag);
				}
			}
        }

//	    LOGGER.warn("fromVanillaCompound: data: [{}]", data.toString());
        return data;
    }

    @Nullable
    public static NbtElement toVanillaNbt(BaseData data)
    {
	    if (data == null)
	    {
		    return NbtEnd.INSTANCE;
	    }

	    return switch (data.getType())
	    {
		    case Constants.NBT.TAG_BYTE -> NbtByte.of(((ByteData) data).value);
		    case Constants.NBT.TAG_SHORT -> NbtShort.of(((ShortData) data).value);
		    case Constants.NBT.TAG_INT -> NbtInt.of(((IntData) data).value);
		    case Constants.NBT.TAG_LONG -> NbtLong.of(((LongData) data).value);
		    case Constants.NBT.TAG_FLOAT -> NbtFloat.of(((FloatData) data).value);
		    case Constants.NBT.TAG_DOUBLE -> NbtDouble.of(((DoubleData) data).value);
		    case Constants.NBT.TAG_STRING -> NbtString.of(((StringData) data).value);
		    case Constants.NBT.TAG_BYTE_ARRAY -> new NbtByteArray(((ByteArrayData) data).value);
		    case Constants.NBT.TAG_INT_ARRAY -> new NbtIntArray(((IntArrayData) data).value);
		    case Constants.NBT.TAG_LONG_ARRAY -> new NbtLongArray(((LongArrayData) data).value);
		    case Constants.NBT.TAG_COMPOUND -> toVanillaCompound((CompoundData) data);
		    case Constants.NBT.TAG_LIST -> toVanillaList((ListData) data);
		    default -> NbtEnd.INSTANCE;
	    };
    }

    public static NbtList toVanillaList(ListData listData)
    {
	    NbtList list = new NbtList();

		if (listData == null || listData.isEmpty())
		{
			return list;
		}

        for (int index = 0; index < listData.size(); index++)
        {
            BaseData entry = listData.get(index);

			if (entry != null)
			{
				if (entry.getType() == Constants.NBT.TAG_END)
				{
					MaLiLib.LOGGER.warn("DataConverterNbt.toVanillaList: Got TAG_End in a list at index {}", index);
					return list;
				}

				NbtElement convertedTag = toVanillaNbt(entry);

				if (convertedTag != null)
				{
					list.add(convertedTag);
				}
			}
        }

        return list;
    }

    public static NbtCompound toVanillaCompound(CompoundData compoundData)
    {
	    NbtCompound tag = new NbtCompound();

	    if (compoundData == null || compoundData.isEmpty())
	    {
		    return tag;
	    }

        for (String key : compoundData.getKeys())
        {
	        BaseData data = compoundData.getData(key).orElse(null);

	        if (data != null)
	        {
		        NbtElement convertedTag = toVanillaNbt(data);

		        if (convertedTag == null)
		        {
			        MaLiLib.LOGGER.warn("DataConverterNbt.toVanillaCompound:B: Got a null tag in a compound with key {}", key);
					continue;
		        }

		        tag.put(key, convertedTag);
	        }
        }

//		LOGGER.debug("toVanillaCompound: nbt [{}]", tag.toString());
        return tag;
    }
}
