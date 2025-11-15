package fi.dy.masa.malilib.util.data.tag.util;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.*;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.*;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

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

	/**
	 * Get the Entity's UUID from Data Tag.
	 *
	 * @param data ()
	 * @return ()
	 */
	public static @Nullable UUID getUUIDCodec(@Nonnull CompoundData data)
	{
		return getUUIDCodec(data, NbtKeys.UUID);
	}

	/**
	 * Get the Entity's UUID from Data Tag.
	 *
	 * @param data ()
	 * @param key ()
	 * @return ()
	 */
	public static @Nullable UUID getUUIDCodec(@Nonnull CompoundData data, String key)
	{
		if (data.contains(key, Constants.NBT.TAG_INT_ARRAY))
		{
			return data.getCodec(key, Uuids.INT_STREAM_CODEC).orElse(null);
		}

		return null;
	}

	/**
	 * Get the Entity's UUID from Data Tag.
	 *
	 * @param dataIn ()
	 * @param key ()
	 * @param uuid ()
	 * @return ()
	 */
	public static CompoundData putUUIDCodec(@Nonnull CompoundData dataIn, @Nonnull UUID uuid, String key)
	{
		return dataIn.putCodec(key, Uuids.INT_STREAM_CODEC, uuid);
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
                list = new ListData();
            }

            list.add(entry);
        }

        return list;
    }

	public static @Nonnull CompoundData createBlockPos(@Nonnull BlockPos pos)
	{
		return writeBlockPos(pos, new CompoundData());
	}

	public static @Nonnull CompoundData createBlockPosTag(@Nonnull Vec3i pos)
	{
		return putVec3i(new CompoundData(), pos);
	}

    public static CompoundData createVec3iTag(Vec3i pos)
    {
        return putVec3i(new CompoundData(), pos);
    }

	public static @Nonnull CompoundData createVec3iToArrayTag(@Nonnull Vec3i pos, String tagName)
	{
		return writeVec3iToArrayTag(new CompoundData(), tagName, pos);
	}

	public static @Nonnull CompoundData createEntityPosition(@Nonnull Vec3d pos)
	{
		return createEntityPositionToTag(pos);
	}

	public static @Nonnull CompoundData createEntityPositionToTag(@Nonnull Vec3d pos)
	{
		return writeVec3dToListTag(new CompoundData(), NbtKeys.POS, pos);
	}

    public static CompoundData putVec3i(CompoundData tag, Vec3i pos)
    {
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());

        return tag;
    }

	public static @Nonnull CompoundData putVec2fCodec(@Nonnull CompoundData tag, @Nonnull Vec2f pos, String key)
	{
		tag.putCodec(key, Vec2f.CODEC, pos);
		return tag;
	}

	public static @Nonnull CompoundData putVec3iCodec(@Nonnull CompoundData tag, @Nonnull Vec3i pos, String key)
	{
		tag.putCodec(key, Vec3i.CODEC, pos);
		return tag;
	}

	public static @Nonnull CompoundData putVec3dCodec(@Nonnull CompoundData tag, @Nonnull Vec3d pos, String key)
	{
		tag.putCodec(key, Vec3d.CODEC, pos);
		return tag;
	}

	public static @Nonnull CompoundData putPosCodec(@Nonnull CompoundData tag, @Nonnull BlockPos pos, String key)
	{
		tag.putCodec(key, BlockPos.CODEC, pos);
		return tag;
	}

	public static Vec2f getVec2fCodec(@Nonnull CompoundData tag, String key)
	{
		return tag.getCodec(key, Vec2f.CODEC).orElse(Vec2f.ZERO);
	}

	public static Vec3i getVec3iCodec(@Nonnull CompoundData tag, String key)
	{
		return tag.getCodec(key, Vec3i.CODEC).orElse(Vec3i.ZERO);
	}

	public static Vec3d getVec3dCodec(@Nonnull CompoundData tag, String key)
	{
		return tag.getCodec(key, Vec3d.CODEC).orElse(Vec3d.ZERO);
	}

	public static BlockPos getPosCodec(@Nonnull CompoundData tag, String key)
	{
		return tag.getCodec(key, BlockPos.CODEC).orElse(BlockPos.ORIGIN);
	}

	public static @Nonnull CompoundData writeBlockPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundData tag)
	{
		return writeBlockPos(pos, tag);
	}

	public static @Nonnull CompoundData writeBlockPos(@Nonnull BlockPos pos, @Nonnull CompoundData tag)
	{
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());

		return tag;
	}

	public static CompoundData writeVec3iToListTag(CompoundData tag, String tagName, Vec3i vec)
    {
        ListData list = new ListData();

        list.add(new IntData(vec.getX()));
        list.add(new IntData(vec.getY()));
        list.add(new IntData(vec.getZ()));
        tag.put(tagName, list);

        return tag;
    }

    public static CompoundData writeVec3iToArrayTag(CompoundData tag, String tagName, Vec3i vec)
    {
        int[] arr = new int[] { vec.getX(), vec.getY(), vec.getZ() };
        tag.putIntArray(tagName, arr);
        return tag;
    }

    public static Vec3i readVec3iOrDefault(DataView tag, String vecTagName, Vec3i defaultValue)
    {
        if (!tag.contains(vecTagName, Constants.NBT.TAG_COMPOUND))
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
            ListData list = tag.getList(tagName);

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

	@Nullable
	public static Vec3i readVec3iFromIntArray(@Nonnull DataView nbt, String key)
	{
		return readVec3iFromIntArrayTag(nbt, key);
	}

	@Nullable
	public static Vec3i readVec3iFromIntArrayTag(@Nonnull DataView tag, String tagName)
	{
		if (tag.contains(tagName, Constants.NBT.TAG_INT_ARRAY))
		{
			int[] pos = tag.getIntArray(tagName);

			if (pos.length == 3)
			{
				return new Vec3i(pos[0], pos[1], pos[2]);
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
        return writeVec3dToListTag(tag, NbtKeys.POS, pos);
    }

    public static CompoundData writeVec3dToListTag(CompoundData tag, String tagName, Vec3d pos)
    {
        return writeVec3dToListTag(tag, tagName, pos.x, pos.y, pos.z);
    }

    public static CompoundData writeVec3dToListTag(CompoundData tag, double x, double y, double z)
    {
        return writeVec3dToListTag(tag, NbtKeys.POS, x, y, z);
    }

    public static CompoundData writeVec3dToListTag(CompoundData tag, String tagName, double x, double y, double z)
    {
        ListData list = new ListData();

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
        return readVec3dFromListTag(data, NbtKeys.POS);
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(DataView data, String tagName)
    {
        if (data.containsList(tagName, Constants.NBT.TAG_DOUBLE))
        {
            ListData list = data.getList(tagName);

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
	 * Read the "BlockAttached" BlockPos from NBT.
	 *
	 * @param tag ()
	 * @return ()
	 */
	@Nullable
	public static BlockPos readAttachedPosFromTag(@Nonnull DataView tag)
	{
		return readPrefixedPosFromTag(tag, "Tile");
	}

	/**
	 * Write the "Block Attached" BlockPos to NBT.
	 *
	 * @param pos ()
	 * @param tag ()
	 * @return ()
	 */
	public static @Nonnull CompoundData writeAttachedPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundData tag)
	{
		return writePrefixedPosToTag(pos, tag, "Tile");
	}

	/**
	 * Read a prefixed BlockPos from NBT.
	 *
	 * @param tag ()
	 * @param pre ()
	 * @return ()
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
	 * Write a prefixed BlockPos to NBT.
	 *
	 * @param pos ()
	 * @param tag ()
	 * @param pre ()
	 * @return ()
	 */
	public static @Nonnull CompoundData writePrefixedPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundData tag, String pre)
	{
		tag.putInt(pre+"X", pos.getX());
		tag.putInt(pre+"Y", pos.getY());
		tag.putInt(pre+"Z", pos.getZ());

		return tag;
	}

	@SuppressWarnings("deprecation")
	public static Direction readDirectionFromTag(@Nonnull CompoundData tag, String key)
	{
		if (tag.contains(key, Constants.NBT.TAG_INT))
		{
			return tag.getCodec(key, Direction.INDEX_CODEC).orElse(Direction.SOUTH);
		}
		else if (tag.contains(key, Constants.NBT.TAG_STRING))
		{
			return tag.getCodec(key, Direction.CODEC).orElse(Direction.SOUTH);
		}

		return Direction.SOUTH;
	}

	@SuppressWarnings("deprecation")
	public static CompoundData writeDirectionToTagAsInt(@Nonnull CompoundData tagIn, String key, Direction direction)
	{
		return tagIn.putCodec(key, Direction.INDEX_CODEC, direction);
	}

	public static CompoundData writeDirectionToTagAsString(@Nonnull CompoundData tagIn, String key, Direction direction)
	{
		return tagIn.putCodec(key, Direction.CODEC, direction);
	}

	/**
	 * Reads in a Flat Map from Data Tag -- this way we don't need Mojang's code complexity
	 * @param <T> ()
	 * @param data ()
	 * @param mapCodec ()
	 * @return ()
	 */
	public static <T> Optional<T> readFlatMap(@Nonnull CompoundData data, MapCodec<T> mapCodec)
	{
		DynamicOps<NbtElement> ops = NbtOps.INSTANCE;
		NbtCompound nbt = DataConverterNbt.toVanillaCompound(data);

		return switch (ops.getMap(nbt).flatMap(map -> mapCodec.decode(ops, map)))
		{
			case DataResult.Success<T> result -> Optional.of(result.value());
			case DataResult.Error<T> error -> error.partialValue();
			default -> Optional.empty();
		};
	}

	/**
	 * Writes a Flat Map to Data Tag -- this way we don't need Mojang's code complexity
	 * @param <T> ()
	 * @param mapCodec ()
	 * @param value ()
	 * @return ()
	 */
	public static <T> CompoundData writeFlatMap(MapCodec<T> mapCodec, T value)
	{
		DynamicOps<NbtElement> ops = NbtOps.INSTANCE;
		NbtCompound nbt = new NbtCompound();

		switch (mapCodec.encoder().encodeStart(ops, value))
		{
			case DataResult.Success<NbtElement> result -> nbt.copyFrom((NbtCompound) result.value());
			case DataResult.Error<NbtElement> error -> error.partialValue().ifPresent(partial -> nbt.copyFrom((NbtCompound) partial));
		}

		return DataConverterNbt.fromVanillaCompound(nbt);
	}
}
