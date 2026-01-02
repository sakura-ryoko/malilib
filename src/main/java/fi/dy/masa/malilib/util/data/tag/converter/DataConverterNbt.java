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
    public static BaseData fromVanillaNbt(Tag vanillaTag)
    {
		if (vanillaTag == null) return EmptyData.INSTANCE;
//		LOGGER.debug("fromVanillaNbt: type: [{}]", vanillaTag.getType());

	    return switch (vanillaTag.getId())
	    {
		    case Constants.NBT.TAG_BYTE -> new ByteData(((ByteTag) vanillaTag).value());
		    case Constants.NBT.TAG_SHORT -> new ShortData(((ShortTag) vanillaTag).value());
		    case Constants.NBT.TAG_INT -> new IntData(((IntTag) vanillaTag).value());
		    case Constants.NBT.TAG_LONG -> new LongData(((LongTag) vanillaTag).value());
		    case Constants.NBT.TAG_FLOAT -> new FloatData(((FloatTag) vanillaTag).value());
		    case Constants.NBT.TAG_DOUBLE -> new DoubleData(((DoubleTag) vanillaTag).value());
		    case Constants.NBT.TAG_STRING -> new StringData(((StringTag) vanillaTag).value());
		    case Constants.NBT.TAG_BYTE_ARRAY -> new ByteArrayData(((ByteArrayTag) vanillaTag).getAsByteArray());
		    case Constants.NBT.TAG_INT_ARRAY -> new IntArrayData(((IntArrayTag) vanillaTag).getAsIntArray());
		    case Constants.NBT.TAG_LONG_ARRAY -> new LongArrayData(((LongArrayTag) vanillaTag).getAsLongArray());
		    case Constants.NBT.TAG_COMPOUND -> fromVanillaCompound(vanillaTag.asCompound().orElse(new CompoundTag()));
		    case Constants.NBT.TAG_LIST -> fromVanillaList(vanillaTag.asList().orElse(new ListTag()));
		    default -> EmptyData.INSTANCE;
	    };
    }

    public static ListData fromVanillaList(ListTag vanillaList)
    {
        ListData list = new ListData();

		if (vanillaList == null || vanillaList.isEmpty())
		{
			return list;
		}

        for (int index = 0; index < vanillaList.size(); index++)
        {
            Tag entry = vanillaList.get(index);

			if (entry != null)
			{
				if (entry.getId() == Constants.NBT.TAG_END)
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

    public static CompoundData fromVanillaCompound(CompoundTag vanillaCompound)
    {
	    CompoundData data = new CompoundData();

	    if (vanillaCompound == null || vanillaCompound.isEmpty())
		{
			return data;
		}

        for (String key : vanillaCompound.keySet())
        {
			Tag ele = vanillaCompound.get(key);

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
    public static Tag toVanillaNbt(BaseData data)
    {
		if (data == null)
		{
			return EndTag.INSTANCE;
		}

	    return switch (data.getType())
	    {
		    case Constants.NBT.TAG_BYTE -> ByteTag.valueOf(((ByteData) data).value);
		    case Constants.NBT.TAG_SHORT -> ShortTag.valueOf(((ShortData) data).value);
		    case Constants.NBT.TAG_INT -> IntTag.valueOf(((IntData) data).value);
		    case Constants.NBT.TAG_LONG -> LongTag.valueOf(((LongData) data).value);
		    case Constants.NBT.TAG_FLOAT -> FloatTag.valueOf(((FloatData) data).value);
		    case Constants.NBT.TAG_DOUBLE -> DoubleTag.valueOf(((DoubleData) data).value);
		    case Constants.NBT.TAG_STRING -> StringTag.valueOf(((StringData) data).value);
		    case Constants.NBT.TAG_BYTE_ARRAY -> new ByteArrayTag(((ByteArrayData) data).value);
		    case Constants.NBT.TAG_INT_ARRAY -> new IntArrayTag(((IntArrayData) data).value);
		    case Constants.NBT.TAG_LONG_ARRAY -> new LongArrayTag(((LongArrayData) data).value);
		    case Constants.NBT.TAG_COMPOUND -> toVanillaCompound((CompoundData) data);
		    case Constants.NBT.TAG_LIST -> toVanillaList((ListData) data);
		    default -> EndTag.INSTANCE;
	    };
    }

    public static ListTag toVanillaList(ListData listData)
    {
        ListTag list = new ListTag();

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

				Tag convertedTag = toVanillaNbt(entry);

				if (convertedTag != null)
				{
					list.add(convertedTag);
				}
			}
        }

        return list;
    }

    public static CompoundTag toVanillaCompound(CompoundData compoundData)
    {
        CompoundTag tag = new CompoundTag();

		if (compoundData == null || compoundData.isEmpty())
		{
			return tag;
		}

        for (String key : compoundData.getKeys())
        {
	        BaseData data = compoundData.getData(key).orElse(null);

	        if (data != null)
	        {
		        Tag convertedTag = toVanillaNbt(data);

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
