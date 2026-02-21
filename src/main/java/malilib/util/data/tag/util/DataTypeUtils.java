package malilib.util.data.tag.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import malilib.util.data.Constants;
import malilib.util.data.tag.BaseData;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.DataView;
import malilib.util.data.tag.DoubleData;
import malilib.util.data.tag.IntData;
import malilib.util.data.tag.ListData;
import malilib.util.data.tag.converter.DataConverterNbt;
import malilib.util.nbt.NbtKeys;
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

    @Nullable
    public static UUID readUuidFromIntArray(DataView tag)
    {
        return readUuidFromIntArray(tag, NbtKeys.UUID);
    }

    @Nullable
    public static UUID readUuidFromIntArray(DataView tag, String key)
    {
        if (tag.contains(key, Constants.NBT.TAG_INT_ARRAY))
        {
            final int[] bits = tag.getIntArray(key);
            return new UUID((long) bits[0] << 32 | bits[1] & 4294967295L, (long) bits[2] << 32 | bits[3] & 4294967295L);
        }

        return null;
    }

    public static void writeUuidToIntArray(CompoundData tag, UUID uuid)
    {
        writeUuidToIntArray(tag, uuid, NbtKeys.UUID);
    }

    public static void writeUuidToIntArray(CompoundData tag, UUID uuid, String key)
    {
        final long most = uuid.getMostSignificantBits();
        final long least = uuid.getLeastSignificantBits();
        tag.putIntArray(key, new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least});
    }

    public static void writeUuidToByteArray(CompoundData tag, UUID uuid)
    {
        writeUuidToByteArray(tag, uuid, NbtKeys.UUID);
    }

    public static void writeUuidToByteArray(CompoundData tag, UUID uuid, String key)
    {
        byte[] bits = new byte[16];
        ByteBuffer.wrap(bits).order(ByteOrder.BIG_ENDIAN).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        tag.putByteArray(key, bits);
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

    /**
     * Read the "Block Attached" BlockPos from Data Tags.
     *
     * @param tag -
     * @return -
     */
    @Nullable
    public static BlockPos readAttachedPosFromTag(@Nonnull DataView tag)
    {
        return readPrefixedPosFromTag(tag, "Tile");
    }

    /**
     * Write the "Block Attached" BlockPos to Data Tags.
     *
     * @param pos -
     * @param tag -
     * @return -
     */
    public static @Nonnull CompoundData writeAttachedPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundData tag)
    {
        return writePrefixedPosToTag(pos, tag, "Tile");
    }

    /**
     * Read a prefixed BlockPos from Data Tags.
     *
     * @param tag -
     * @param pre -
     * @return -
     */
    @Nullable
    public static BlockPos readPrefixedPosFromTag(@Nonnull DataView tag, String pre)
    {
        if (tag.contains(pre+"X", Constants.NBT.TAG_INT) &&
            tag.contains(pre+"Y", Constants.NBT.TAG_INT) &&
            tag.contains(pre+"Z", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInt(pre+"X"), tag.getInt(pre+"Y"), tag.getInt(pre+"Z"));
        }

        return null;
    }

    /**
     * Write a prefixed BlockPos to Data Tags.
     *
     * @param pos -
     * @param tag -
     * @param pre -
     * @return -
     */
    public static @Nonnull CompoundData writePrefixedPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundData tag, String pre)
    {
        tag.putInt(pre+"X", pos.getX());
        tag.putInt(pre+"Y", pos.getY());
        tag.putInt(pre+"Z", pos.getZ());

        return tag;
    }

    /**
     * Deserialize an ItemStack from a Data tag
     * @param data -
     * @return -
     * @implNote In the future, after ~1.21; the Registry / Data Ops is required here
     */
    public static Optional<ItemStack> toItemStack(@Nonnull CompoundData data)
    // @Nonnull RegistryAccess registry
    {
        if (data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
        {
            return Optional.of(new ItemStack(DataConverterNbt.toVanillaCompound(data)));
        }

        return Optional.empty();
    }

    /**
     * Serialize an ItemStack to a Data tag
     * @param stack -
     * @return -
     * @implNote In the future, after ~1.21; the Registry / Data Ops is required here
     */
    public static CompoundData fromItemStack(@Nonnull ItemStack stack)
    // @Nonnull RegistryAccess registry
    {
        CompoundData data = new CompoundData();

        if (!stack.isEmpty())
        {
            data.combine(DataConverterNbt.fromVanillaCompound(stack.writeToNBT(new NBTTagCompound())));

            if (data.contains(NbtKeys.ID, Constants.NBT.TAG_STRING))
            {
                return data;
            }
        }

        return new CompoundData();
    }

    /**
     * Deserialize a Data Tag that contains a {@link ITextComponent} as a JSON String, utilizing a key.
     * @param data -
     * @param key -
     * @return -
     * @implNote In the future, after ~1.21; the Registry / Data Ops is required here
     */
    public static Optional<ITextComponent> toTextComponent(@Nonnull CompoundData data, String key)
    // @Nonnull RegistryAccess registry
    {
        final String json =  data.getStringOrDefault(key, "");
        if (json.isEmpty()) { return Optional.empty(); }

        try
        {
            return Optional.ofNullable(ITextComponent.Serializer.jsonToComponent(json));
        }
        catch (Exception ignored) {}

        return Optional.empty();
    }

    /**
     * Deserialize a Data Tag that contains a {@link ITextComponent} as a JSON String.
     * @param jsonStr -
     * @return -
     * @implNote In the future, after ~1.21; the Registry / Data Ops is required here
     */
    public static Optional<ITextComponent> toTextComponent(@Nonnull final String jsonStr)
    // @Nonnull RegistryAccess registry
    {
        if (jsonStr.isEmpty()) { return Optional.empty(); }

        try
        {
            return Optional.ofNullable(ITextComponent.Serializer.jsonToComponent(jsonStr));
        }
        catch (Exception ignored) {}

        return Optional.empty();
    }

    /**
     * Serialize a {@link ITextComponent} into a Data Tag with a key param, and optionally, a source Data Tag
     * @param text -
     * @param key -
     * @param dataIn -
     * @return -
     * @implNote In the future, after ~1.21; the Registry / Data Ops is required here
     */
    public static CompoundData fromTextComponent(@Nonnull ITextComponent text, String key, @Nullable CompoundData dataIn)
    // @Nonnull RegistryAccess registry
    {
        CompoundData data = dataIn != null ? dataIn.copy() : new CompoundData();

        try
        {
            final String json = ITextComponent.Serializer.componentToJson(text);

            if (!json.isEmpty())
            {
                data.putString(key, json);
            }
        }
        catch (Exception ignored) {}

        return data;
    }
}
