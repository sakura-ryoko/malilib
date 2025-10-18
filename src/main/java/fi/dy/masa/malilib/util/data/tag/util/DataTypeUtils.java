package fi.dy.masa.malilib.util.data.tag.util;

import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.*;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.nbt.NbtKeys;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;

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

	public static BlockPos getPosCodec(@Nonnull CompoundData tag, String key)
	{
		return tag.getCodec(key, BlockPos.CODEC).orElse(BlockPos.ORIGIN);
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
