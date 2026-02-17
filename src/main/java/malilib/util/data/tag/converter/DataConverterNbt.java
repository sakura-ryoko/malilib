package malilib.util.data.tag.converter;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import malilib.MaLiLib;
import malilib.mixin.access.NBTTagLongArrayMixin;
import malilib.util.data.Constants;
import malilib.util.data.tag.BaseData;
import malilib.util.data.tag.ByteArrayData;
import malilib.util.data.tag.ByteData;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.DoubleData;
import malilib.util.data.tag.FloatData;
import malilib.util.data.tag.IntArrayData;
import malilib.util.data.tag.IntData;
import malilib.util.data.tag.ListData;
import malilib.util.data.tag.LongArrayData;
import malilib.util.data.tag.LongData;
import malilib.util.data.tag.ShortData;
import malilib.util.data.tag.StringData;

public class DataConverterNbt
{
    @Nullable
    public static BaseData fromVanillaNbt(NBTBase vanillaTag)
    {
        switch (vanillaTag.getId())
        {
            case Constants.NBT.TAG_BYTE:        return new ByteData(((NBTTagByte) vanillaTag).getByte());
            case Constants.NBT.TAG_SHORT:       return new ShortData(((NBTTagShort) vanillaTag).getShort());
            case Constants.NBT.TAG_INT:         return new IntData(((NBTTagInt) vanillaTag).getInt());
            case Constants.NBT.TAG_LONG:        return new LongData(((NBTTagLong) vanillaTag).getLong());
            case Constants.NBT.TAG_FLOAT:       return new FloatData(((NBTTagFloat) vanillaTag).getFloat());
            case Constants.NBT.TAG_DOUBLE:      return new DoubleData(((NBTTagDouble) vanillaTag).getDouble());
            case Constants.NBT.TAG_STRING:      return new StringData(((NBTTagString) vanillaTag).getString());
            case Constants.NBT.TAG_COMPOUND:    return fromVanillaCompound((NBTTagCompound) vanillaTag);
            case Constants.NBT.TAG_LIST:        return fromVanillaList((NBTTagList) vanillaTag);
            case Constants.NBT.TAG_BYTE_ARRAY:
            {
                byte[] arr = ((NBTTagByteArray) vanillaTag).getByteArray();
                return arr != null ? new ByteArrayData(arr) : null;
            }
            case Constants.NBT.TAG_INT_ARRAY:
            {
                int[] arr = ((NBTTagIntArray) vanillaTag).getIntArray();
                return arr != null ? new IntArrayData(arr) : null;
            }
            case Constants.NBT.TAG_LONG_ARRAY:
            {
                long[] arr = ((NBTTagLongArrayMixin) vanillaTag).getArray();
                return arr != null ? new LongArrayData(arr) : null;
            }
            default:
                MaLiLib.LOGGER.warn("DataConverterNbt.fromVanillaCompound: Unknown NBT tag id {}", vanillaTag.getId());
        }

        return null;
    }

    @Nullable
    public static ListData fromVanillaList(NBTTagList vanillaList)
    {
        ListData list = new ListData(vanillaList.getTagType());

        for (int index = 0; index < vanillaList.tagCount(); index++)
        {
            NBTBase entry = vanillaList.get(index);

            if (entry.getId() == Constants.NBT.TAG_END)
            {
                MaLiLib.LOGGER.warn("DataConverterNbt.fromVanillaList: Got TAG_End in a list at index {}", index);
                return null;
            }

            BaseData convertedTag = fromVanillaNbt(entry);

            if (convertedTag == null)
            {
                MaLiLib.LOGGER.warn("DataConverterNbt.fromVanillaList: Got a null tag in a list at index {}", index);
                return null;
            }

            list.add(convertedTag);
        }

        return list;
    }

    public static CompoundData fromVanillaCompound(NBTTagCompound vanillaCompound)
    {
        CompoundData data = new CompoundData();

        for (String key : vanillaCompound.getKeySet())
        {
            BaseData convertedTag = fromVanillaNbt(vanillaCompound.getTag(key));

            if (convertedTag == null)
            {
                MaLiLib.LOGGER.warn("DataConverterNbt.fromVanillaCompound: Got a null tag in a compound with key '{}'", key);
                continue;
            }

            data.put(key, convertedTag);
        }

        return data;
    }

    @Nullable
    public static NBTBase toVanillaNbt(BaseData data)
    {
        switch (data.getType())
        {
            case Constants.NBT.TAG_BYTE:        return new NBTTagByte(((ByteData) data).value);
            case Constants.NBT.TAG_SHORT:       return new NBTTagShort(((ShortData) data).value);
            case Constants.NBT.TAG_INT:         return new NBTTagInt(((IntData) data).value);
            case Constants.NBT.TAG_LONG:        return new NBTTagLong(((LongData) data).value);
            case Constants.NBT.TAG_FLOAT:       return new NBTTagFloat(((FloatData) data).value);
            case Constants.NBT.TAG_DOUBLE:      return new NBTTagDouble(((DoubleData) data).value);
            case Constants.NBT.TAG_STRING:      return new NBTTagString(((StringData) data).value);
            case Constants.NBT.TAG_BYTE_ARRAY:  return new NBTTagByteArray(((ByteArrayData) data).value);
            case Constants.NBT.TAG_INT_ARRAY:   return new NBTTagIntArray(((IntArrayData) data).value);
            case Constants.NBT.TAG_LONG_ARRAY:  return new NBTTagLongArray(((LongArrayData) data).value);
            case Constants.NBT.TAG_COMPOUND:    return toVanillaCompound((CompoundData) data);
            case Constants.NBT.TAG_LIST:        return toVanillaList((ListData) data);
            default:
                MaLiLib.LOGGER.warn("DataConverterNbt.toVanillaNbt: Unknown NBT tag id {}", data.getType());
        }

        return null;
    }

    @Nullable
    public static NBTTagList toVanillaList(ListData listData)
    {
        NBTTagList list = new NBTTagList();

        for (int index = 0; index < listData.size(); index++)
        {
            BaseData entry = listData.get(index);

            if (entry.getType() == Constants.NBT.TAG_END)
            {
                MaLiLib.LOGGER.warn("DataConverterNbt.toVanillaList: Got TAG_End in a list at index {}", index);
                return null;
            }

            NBTBase convertedTag = toVanillaNbt(entry);

            if (convertedTag == null)
            {
                MaLiLib.LOGGER.warn("DataConverterNbt.toVanillaList: Got a null tag in a list at index {}", index);
                return null;
            }

            list.appendTag(convertedTag);
        }

        return list;
    }

    public static NBTTagCompound toVanillaCompound(CompoundData compoundData)
    {
        NBTTagCompound tag = new NBTTagCompound();

        for (String key : compoundData.getKeys())
        {
            NBTBase convertedTag = toVanillaNbt(compoundData.getData(key).orElse(null));

            if (convertedTag == null)
            {
                MaLiLib.LOGGER.warn("DataConverterNbt.toVanillaCompound: Got a null tag in a compound with key '{}'", key);
                continue;
            }

            tag.setTag(key, convertedTag);
        }

        return tag;
    }
}
