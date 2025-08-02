package malilib.util.data.tag;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
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
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import malilib.MaLiLib;
import malilib.mixin.access.NBTTagLongArrayMixin;
import malilib.util.data.Constants;
import malilib.util.position.BlockPos;
import malilib.util.position.Vec3d;
import malilib.util.position.Vec3i;

public class DataTypeUtils
{
    @Nullable
    public static UUID readUuidFromLongs(DataView tag)
    {
        return readUuidFromLongs(tag, "UUIDM", "UUIDL");
    }

    @Nullable
    public static UUID readUuidFromLongs(DataView tag, String keyM, String keyL)
    {
        if (tag.contains(keyM, Constants.NBT.TAG_LONG) && tag.contains(keyL, Constants.NBT.TAG_LONG))
        {
            return new UUID(tag.getLong(keyM), tag.getLong(keyL));
        }

        return null;
    }

    public static void writeUuidToLongs(CompoundData tag, UUID uuid)
    {
        writeUuidToLongs(tag, uuid, "UUIDM", "UUIDL");
    }

    public static void writeUuidToLongs(CompoundData tag, UUID uuid, String keyM, String keyL)
    {
        tag.putLong(keyM, uuid.getMostSignificantBits());
        tag.putLong(keyL, uuid.getLeastSignificantBits());
    }

    public static CompoundData getOrCreateCompound(CompoundData tagIn, String tagName)
    {
        CompoundData tag;

        if (tagIn.contains(tagName, Constants.NBT.TAG_COMPOUND))
        {
            tag = tagIn.getCompound(tagName);
        }
        else
        {
            tag = new CompoundData();
            tagIn.put(tagName, tag);
        }

        return tag;
    }

    public static <T> ListData asListTag(Collection<T> values, Function<T, BaseData> tagFactory)
    {
        ListData list = null;

        for (T val : values)
        {
            BaseData entry = tagFactory.apply(val);

            if (list == null)
            {
                list = new ListData(entry.getType());
            }

            list.add(entry);
        }

        return list;
    }

    public static CompoundData createVec3iTag(Vec3i pos)
    {
        return putVec3i(new CompoundData(), pos);
    }

    public static CompoundData putVec3i(CompoundData tag, Vec3i pos)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        return tag;
    }

    @Nullable
    public static CompoundData writeVec3iToListTag(CompoundData tag, String tagName, Vec3i vec)
    {
        ListData tagList = new ListData(Constants.NBT.TAG_INT);

        tagList.add(new IntData(vec.getX()));
        tagList.add(new IntData(vec.getY()));
        tagList.add(new IntData(vec.getZ()));

        tag.put(tagName, tagList);

        return tag;
    }

    @Nullable
    public static CompoundData writeVec3iToArrayTag(CompoundData tag, String tagName, Vec3i vec)
    {
        int[] arr = new int[] { vec.getX(), vec.getY(), vec.getZ() };
        tag.putIntArray(tagName, arr);
        return tag;
    }

    public static Vec3i readVec3iOrDefault(DataView tag, String vecTagName, Vec3i defaultValue)
    {
        if (tag.contains(vecTagName, Constants.NBT.TAG_COMPOUND) == false)
        {
            return defaultValue;
        }

        DataView vecTag = tag.getCompound(vecTagName);

        if (vecTag.contains("x", Constants.NBT.TAG_INT) &&
            vecTag.contains("y", Constants.NBT.TAG_INT) &&
            vecTag.contains("z", Constants.NBT.TAG_INT))
        {
            return new Vec3i(vecTag.getInt("x"), vecTag.getInt("y"), vecTag.getInt("z"));
        }

        return defaultValue;
    }

    @Nullable
    public static BlockPos readBlockPos(DataView tag)
    {
        if (tag.contains("x", Constants.NBT.TAG_INT) &&
            tag.contains("y", Constants.NBT.TAG_INT) &&
            tag.contains("z", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromListTag(DataView tag, String tagName)
    {
        if (tag.containsList(tagName, Constants.NBT.TAG_INT))
        {
            ListData list = tag.getList(tagName, Constants.NBT.TAG_INT);

            if (list.size() == 3)
            {
                return new BlockPos(list.getIntAt(0),
                                    list.getIntAt(1),
                                    list.getIntAt(2));
            }
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromArrayTag(DataView tag, String tagName)
    {
        if (tag.contains(tagName, Constants.NBT.TAG_INT_ARRAY))
        {
            int[] pos = tag.getIntArray(tagName);

            if (pos.length == 3)
            {
                return new BlockPos(pos[0], pos[1], pos[2]);
            }
        }

        return null;
    }

    public static CompoundData removeBlockPosFromTag(CompoundData tag)
    {
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");

        return tag;
    }

    public static CompoundData writeVec3dToListTag(CompoundData tag, Vec3d pos)
    {
        return writeVec3dToListTag(tag, "Pos", pos);
    }

    public static CompoundData writeVec3dToListTag(CompoundData tag, String tagName, Vec3d pos)
    {
        ListData list = new ListData(Constants.NBT.TAG_DOUBLE);

        list.add(new DoubleData(pos.x));
        list.add(new DoubleData(pos.y));
        list.add(new DoubleData(pos.z));

        tag.put(tagName, list);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(DataView data)
    {
        if (data.contains("dx", Constants.NBT.TAG_DOUBLE) &&
            data.contains("dy", Constants.NBT.TAG_DOUBLE) &&
            data.contains("dz", Constants.NBT.TAG_DOUBLE))
        {
            return new Vec3d(data.getDouble("dx"),
                             data.getDouble("dy"),
                             data.getDouble("dz"));
        }

        return null;
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(DataView data)
    {
        return readVec3dFromListTag(data, "Pos");
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(DataView data, String tagName)
    {
        if (data.containsList(tagName, Constants.NBT.TAG_DOUBLE))
        {
            ListData list = data.getList(tagName, Constants.NBT.TAG_DOUBLE);

            if (list.size() == 3)
            {
                return new Vec3d(list.getDoubleAt(0),
                                 list.getDoubleAt(1),
                                 list.getDoubleAt(2));
            }
        }

        return null;
    }

    @Nullable
    public static BaseData convertTag(NBTBase vanillaTag)
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
            case Constants.NBT.TAG_BYTE_ARRAY:  return new ByteArrayData(((NBTTagByteArray) vanillaTag).getByteArray());
            case Constants.NBT.TAG_INT_ARRAY:   return new IntArrayData(((NBTTagIntArray) vanillaTag).getIntArray());
            case Constants.NBT.TAG_LONG_ARRAY:  return new LongArrayData(((NBTTagLongArrayMixin) vanillaTag).getArray());
            case Constants.NBT.TAG_COMPOUND:    return fromVanillaCompound((NBTTagCompound) vanillaTag);
            case Constants.NBT.TAG_LIST:        return fromVanillaList((NBTTagList) vanillaTag);
            default:
                MaLiLib.LOGGER.warn("DataTypeUtils.fromVanillaCompound: Unknown NBT tag id {}", vanillaTag.getId());
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
                MaLiLib.LOGGER.warn("DataTypeUtils.fromVanillaList: Got TAG_End in a list at index {}", index);
                return null;
            }

            BaseData convertedTag = convertTag(entry);

            if (convertedTag == null)
            {
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
            BaseData convertedTag = convertTag(vanillaCompound.getTag(key));

            if (convertedTag != null)
            {
                data.put(key, convertedTag);
            }
        }

        return data;
    }
}
