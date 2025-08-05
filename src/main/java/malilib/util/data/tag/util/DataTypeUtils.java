package malilib.util.data.tag.util;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;

import malilib.util.data.Constants;
import malilib.util.data.tag.BaseData;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.DataView;
import malilib.util.data.tag.DoubleData;
import malilib.util.data.tag.IntData;
import malilib.util.data.tag.ListData;
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
        ListData list = new ListData(Constants.NBT.TAG_INT);

        list.add(new IntData(vec.getX()));
        list.add(new IntData(vec.getY()));
        list.add(new IntData(vec.getZ()));

        tag.put(tagName, list);

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

    public static BlockPos readBlockPosFromListTagOrDefault(DataView tag, String tagName, BlockPos defaultValue)
    {
        BlockPos pos = readBlockPosFromListTag(tag, tagName);
        return pos != null ? pos : defaultValue;
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

    public static BlockPos readBlockPosFromArrayTagOrDefault(DataView tag, String tagName, BlockPos defaultValue)
    {
        BlockPos pos = readBlockPosFromArrayTag(tag, tagName);
        return pos != null ? pos : defaultValue;
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
        return writeVec3dToListTag(tag, tagName, pos.x, pos.y, pos.z);
    }

    public static CompoundData writeVec3dToListTag(CompoundData tag, double x, double y, double z)
    {
        return writeVec3dToListTag(tag, "Pos", x, y, z);
    }

    public static CompoundData writeVec3dToListTag(CompoundData tag, String tagName, double x, double y, double z)
    {
        ListData list = new ListData(Constants.NBT.TAG_DOUBLE);

        list.add(new DoubleData(x));
        list.add(new DoubleData(y));
        list.add(new DoubleData(z));

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
}
