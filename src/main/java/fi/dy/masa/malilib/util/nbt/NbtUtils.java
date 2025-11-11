package fi.dy.masa.malilib.util.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.*;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;

/**
 * Post-ReWrite code
 */
public class NbtUtils
{
	@Nullable
	public static UUID readUUID(@Nonnull NbtCompound tag)
	{
		return readUUID(tag, "UUIDM", "UUIDL");
	}

	@Nullable
	public static UUID readUUID(@Nonnull NbtCompound tag, String keyM, String keyL)
	{
		if (tag.contains(keyM) && tag.contains(keyL))
		{
			return new UUID(tag.getLong(keyM, 0L), tag.getLong(keyL, 0L));
		}

		return null;
	}

	public static void writeUUID(@Nonnull NbtCompound tag, UUID uuid)
	{
		writeUUID(tag, uuid, "UUIDM", "UUIDL");
	}

	public static void writeUUID(@Nonnull NbtCompound tag, UUID uuid, String keyM, String keyL)
	{
		tag.putLong(keyM, uuid.getMostSignificantBits());
		tag.putLong(keyL, uuid.getLeastSignificantBits());
	}

	public static NbtCompound getOrCreateCompound(@Nonnull NbtCompound tagIn, String tagName)
	{
		NbtCompound nbt;

		if (tagIn.contains(tagName))
		{
			nbt = tagIn.getCompoundOrEmpty(tagName);
		}
		else
		{
			nbt = new NbtCompound();
			tagIn.put(tagName, nbt);
		}

		return nbt;
	}

	public static <T> NbtList asListTag(Collection<T> values, Function<T, NbtElement> tagFactory)
	{
		NbtList list = new NbtList();

		for (T val : values)
		{
			list.add(tagFactory.apply(val));
		}

		return list;
	}

	/**
	 * Get the Entity's UUID from NBT.
	 *
	 * @param nbt ()
	 * @return ()
	 */
	public static @Nullable UUID getUUIDCodec(@Nonnull NbtCompound nbt)
	{
		return getUUIDCodec(nbt, NbtKeys.UUID);
	}

	/**
	 * Get the Entity's UUID from NBT.
	 *
	 * @param nbt ()
	 * @param key ()
	 * @return ()
	 */
	public static @Nullable UUID getUUIDCodec(@Nonnull NbtCompound nbt, String key)
	{
		if (nbt.contains(key))
		{
			return nbt.get(key, Uuids.INT_STREAM_CODEC).orElse(null);
		}

		return null;
	}

	/**
	 * Get the Entity's UUID from NBT.
	 *
	 * @param nbtIn ()
	 * @param key ()
	 * @param uuid ()
	 * @return ()
	 */
	public static NbtCompound putUUIDCodec(@Nonnull NbtCompound nbtIn, @Nonnull UUID uuid, String key)
	{
		nbtIn.put(key, Uuids.INT_STREAM_CODEC, uuid);
		return nbtIn;
	}

	public static @Nonnull NbtCompound createBlockPos(@Nonnull BlockPos pos)
	{
		return writeBlockPos(pos, new NbtCompound());
	}

	public static @Nonnull NbtCompound createBlockPosTag(@Nonnull BlockPos pos)
	{
		return writeBlockPos(pos, new NbtCompound());
	}

	public static @Nonnull NbtCompound createBlockPosTag(@Nonnull Vec3i pos)
	{
		return putVec3i(new NbtCompound(), pos);
	}

	public static @Nonnull NbtCompound createVec3iTag(@Nonnull Vec3i pos)
	{
		return putVec3i(new NbtCompound(), pos);
	}

	public static @Nonnull NbtCompound createVec3iToArray(@Nonnull Vec3i pos, String tagName)
	{
		return writeBlockPosToArrayTag(pos, new NbtCompound(), tagName);
	}

	public static @Nonnull NbtCompound createVec3iToArrayTag(@Nonnull Vec3i pos, String tagName)
	{
		return writeBlockPosToArrayTag(pos, new NbtCompound(), tagName);
	}

	public static @Nonnull NbtCompound createEntityPosition(@Nonnull Vec3d pos)
	{
		return createEntityPositionToTag(pos);
	}

	public static @Nonnull NbtCompound createEntityPositionToTag(@Nonnull Vec3d pos)
	{
		return writeVec3dToListTag(pos, new NbtCompound(), NbtKeys.POS);
	}

	public static @Nonnull NbtCompound putVec3i(@Nonnull NbtCompound tag, @Nonnull Vec3i pos)
	{
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
		return tag;
	}

	public static @Nonnull NbtCompound putVec2fCodec(@Nonnull NbtCompound tag, @Nonnull Vec2f pos, String key)
	{
		tag.put(key, Vec2f.CODEC, pos);
		return tag;
	}

	public static @Nonnull NbtCompound putVec3iCodec(@Nonnull NbtCompound tag, @Nonnull Vec3i pos, String key)
	{
		tag.put(key, Vec3i.CODEC, pos);
		return tag;
	}

	public static @Nonnull NbtCompound putVec3dCodec(@Nonnull NbtCompound tag, @Nonnull Vec3d pos, String key)
	{
		tag.put(key, Vec3d.CODEC, pos);
		return tag;
	}

	public static @Nonnull NbtCompound putPosCodec(@Nonnull NbtCompound tag, @Nonnull BlockPos pos, String key)
	{
		tag.put(key, BlockPos.CODEC, pos);
		return tag;
	}

	public static Vec2f getVec2fCodec(@Nonnull NbtCompound tag, String key)
	{
		return tag.get(key, Vec2f.CODEC).orElse(Vec2f.ZERO);
	}

	public static Vec3i getVec3iCodec(@Nonnull NbtCompound tag, String key)
	{
		return tag.get(key, Vec3i.CODEC).orElse(Vec3i.ZERO);
	}

	public static Vec3d getVec3dCodec(@Nonnull NbtCompound tag, String key)
	{
		return tag.get(key, Vec3d.CODEC).orElse(Vec3d.ZERO);
	}

	public static BlockPos getPosCodec(@Nonnull NbtCompound tag, String key)
	{
		return tag.get(key, BlockPos.CODEC).orElse(BlockPos.ORIGIN);
	}

	public static @Nonnull NbtCompound writeBlockPosToTag(@Nonnull BlockPos pos, @Nonnull NbtCompound tag)
	{
		return writeBlockPos(pos, tag);
	}

	public static @Nonnull NbtCompound writeBlockPos(@Nonnull BlockPos pos, @Nonnull NbtCompound tag)
	{
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());

		return tag;
	}

	public static @Nonnull NbtCompound writeBlockPosToListTag(@Nonnull Vec3i pos, @Nonnull NbtCompound tag, String tagName)
	{
		NbtList tagList = new NbtList();

		tagList.add(NbtInt.of(pos.getX()));
		tagList.add(NbtInt.of(pos.getY()));
		tagList.add(NbtInt.of(pos.getZ()));
		tag.put(tagName, tagList);

		return tag;
	}

	public static @Nonnull NbtCompound writeVec3iToArray(@Nonnull Vec3i pos, @Nonnull NbtCompound tag, String tagName)
	{
		return writeBlockPosToArrayTag(pos, tag, tagName);
	}

	public static @Nonnull NbtCompound writeBlockPosToArrayTag(@Nonnull Vec3i pos, @Nonnull NbtCompound tag, String tagName)
	{
		int[] arr = new int[]{pos.getX(), pos.getY(), pos.getZ()};
		tag.putIntArray(tagName, arr);
		return tag;
	}

	@Nullable
	public static BlockPos readBlockPos(@Nullable NbtCompound tag)
	{
		if (tag != null &&
			tag.contains("x") &&
			tag.contains("y") &&
			tag.contains("z"))
		{
			return new BlockPos(tag.getInt("x", 0), tag.getInt("y", 0), tag.getInt("z", 0));
		}

		return null;
	}

	@Nullable
	public static Vec3i readVec3i(@Nullable NbtCompound tag)
	{
		return readVec3iFromTag(tag);
	}

	@Nullable
	public static Vec3i readVec3iFromTag(@Nullable NbtCompound tag)
	{
		if (tag != null &&
			tag.contains("x") &&
			tag.contains("y") &&
			tag.contains("z"))
		{
			return new Vec3i(tag.getInt("x", 0), tag.getInt("y", 0), tag.getInt("z", 0));
		}

		return null;
	}

	@Nullable
	public static BlockPos readBlockPosFromListTag(@Nonnull NbtCompound tag, String tagName)
	{
		if (tag.contains(tagName))
		{
			NbtList tagList = tag.getListOrEmpty(tagName);

			if (tagList.size() == 3)
			{
				return new BlockPos(tagList.getInt(0, 0), tagList.getInt(1, 0), tagList.getInt(2, 0));
			}
		}

		return null;
	}

	@Nullable
	public static BlockPos readBlockPosFromIntArray(@Nonnull NbtCompound nbt, String key)
	{
		return readBlockPosFromArrayTag(nbt, key);
	}

	@Nullable
	public static BlockPos readBlockPosFromArrayTag(@Nonnull NbtCompound tag, String tagName)
	{
		if (tag.contains(tagName))
		{
			int[] pos = tag.getIntArray(tagName).orElse(new int[0]);

			if (pos.length == 3)
			{
				return new BlockPos(pos[0], pos[1], pos[2]);
			}
		}

		return null;
	}

	@Nullable
	public static Vec3i readVec3iFromIntArray(@Nonnull NbtCompound nbt, String key)
	{
		return readVec3iFromIntArrayTag(nbt, key);
	}

	@Nullable
	public static Vec3i readVec3iFromIntArrayTag(@Nonnull NbtCompound tag, String tagName)
	{
		if (tag.contains(tagName))
		{
			int[] pos = tag.getIntArray(tagName).orElse(new int[0]);

			if (pos.length == 3)
			{
				return new Vec3i(pos[0], pos[1], pos[2]);
			}
		}

		return null;
	}

	public static @Nonnull NbtCompound removeBlockPos(@Nonnull NbtCompound tag)
	{
		return removeBlockPosFromTag(tag);
	}

	public static @Nonnull NbtCompound removeBlockPosFromTag(@Nonnull NbtCompound tag)
	{
		tag.remove("x");
		tag.remove("y");
		tag.remove("z");

		return tag;
	}

	public static @Nonnull NbtCompound writeEntityPosition(@Nonnull Vec3d pos, @Nonnull NbtCompound tag)
	{
		return writeVec3dToListTag(pos, tag, NbtKeys.POS);
	}

	public static @Nonnull NbtCompound writeEntityPositionToTag(@Nonnull Vec3d pos, @Nonnull NbtCompound tag)
	{
		return writeVec3dToListTag(pos, tag, NbtKeys.POS);
	}

	public static @Nonnull NbtCompound writeVec3dToListTag(@Nonnull Vec3d pos, @Nonnull NbtCompound tag)
	{
		return writeVec3dToListTag(pos, tag, NbtKeys.POS);
	}

	public static @Nonnull NbtCompound writeVec3dToListTag(@Nonnull Vec3d pos, @Nonnull NbtCompound tag, String tagName)
	{
		NbtList posList = new NbtList();

		posList.add(NbtDouble.of(pos.x));
		posList.add(NbtDouble.of(pos.y));
		posList.add(NbtDouble.of(pos.z));
		tag.put(tagName, posList);

		return tag;
	}

	@Nullable
	public static Vec3d readVec3d(@Nullable NbtCompound tag)
	{
		if (tag != null &&
			tag.contains("dx") &&
			tag.contains("dy") &&
			tag.contains("dz"))
		{
			return new Vec3d(tag.getDouble("dx", 0d), tag.getDouble("dy", 0d), tag.getDouble("dz", 0d));
		}

		return null;
	}

	@Nullable
	public static Vec3d readVec3dFromListTag(@Nullable NbtCompound tag)
	{
		return readVec3dFromListTag(tag, NbtKeys.POS);
	}

	@Nullable
	public static Vec3d readEntityPositionFromTag(@Nullable NbtCompound tag)
	{
		return readVec3dFromListTag(tag, NbtKeys.POS);
	}

	@Nullable
	public static Vec3d readVec3dFromListTag(@Nullable NbtCompound tag, String tagName)
	{
		if (tag != null && tag.contains(tagName))
		{
			NbtList tagList = tag.getListOrEmpty(tagName);

			if (tagList.getType() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
			{
				return new Vec3d(tagList.getDouble(0, 0d), tagList.getDouble(1, 0d), tagList.getDouble(2, 0d));
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
	public static BlockPos readAttachedPosFromTag(@Nonnull NbtCompound tag)
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
	public static @Nonnull NbtCompound writeAttachedPosToTag(@Nonnull BlockPos pos, @Nonnull NbtCompound tag)
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
	public static BlockPos readPrefixedPosFromTag(@Nonnull NbtCompound tag, String pre)
	{
		if (tag.contains(pre+"X") &&
			tag.contains(pre+"Y") &&
			tag.contains(pre+"Z"))
		{
			return new BlockPos(tag.getInt(pre+"X", 0), tag.getInt(pre+"Y", 0), tag.getInt(pre+"Z", 0));
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
	public static @Nonnull NbtCompound writePrefixedPosToTag(@Nonnull BlockPos pos, @Nonnull NbtCompound tag, String pre)
	{
		tag.putInt(pre+"X", pos.getX());
		tag.putInt(pre+"Y", pos.getY());
		tag.putInt(pre+"Z", pos.getZ());

		return tag;
	}

	@SuppressWarnings("deprecation")
	public static Direction readDirectionFromTag(@Nonnull NbtCompound tag, String key)
	{
		NbtElement ele = tag.get(key);

		if (ele != null)
		{
			if (ele.getType() == Constants.NBT.TAG_INT)
			{
				return tag.get(key, Direction.INDEX_CODEC).orElse(Direction.SOUTH);
			}
			else if (ele.getType() == Constants.NBT.TAG_STRING)
			{
				return tag.get(key, Direction.CODEC).orElse(Direction.SOUTH);
			}
		}

		return Direction.SOUTH;
	}

	@SuppressWarnings("deprecation")
	public static NbtCompound writeDirectionToTagAsInt(@Nonnull NbtCompound tagIn, String key, Direction direction)
	{
		tagIn.put(key, Direction.INDEX_CODEC, direction);

		return tagIn;
	}

	public static NbtCompound writeDirectionToTagAsString(@Nonnull NbtCompound tagIn, String key, Direction direction)
	{
		tagIn.put(key, Direction.CODEC, direction);

		return tagIn;
	}

	/**
	 * See {@link #readNbtFromFileAsPath}
	 */
	@Deprecated(forRemoval = true)
	@Nullable
	public static NbtCompound readNbtFromFile(@Nonnull File file)
	{
		return readNbtFromFile(file, NbtSizeTracker.ofUnlimitedBytes());
	}

	@Nullable
	public static NbtCompound readNbtFromFileAsPath(@Nonnull Path file)
	{
		return readNbtFromFileAsPath(file, NbtSizeTracker.ofUnlimitedBytes());
	}

	/**
	 * See {@link #readNbtFromFileAsPath}
	 */
	@Deprecated(forRemoval = true)
	@Nullable
	public static NbtCompound readNbtFromFile(@Nonnull File file, NbtSizeTracker tracker)
	{
		if (file.exists() == false || file.canRead() == false)
		{
			return null;
		}

		FileInputStream is;

		try
		{
			is = new FileInputStream(file);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("readNbtFromFile: Failed to read NBT data from file '{}' (failed to create the input stream)", file.getAbsolutePath());
			return null;
		}

		NbtCompound nbt = null;

		if (is != null)
		{
			try
			{
				nbt = NbtIo.readCompressed(is, tracker);
			}
			catch (Exception e)
			{
				try
				{
					is.close();
					is = new FileInputStream(file);
					nbt = NbtIo.read(file.toPath());
				}
				catch (Exception ignore)
				{
				}
			}

			try
			{
				is.close();
			}
			catch (Exception ignore)
			{
			}
		}

		if (nbt == null)
		{
			MaLiLib.LOGGER.warn("readNbtFromFile: Failed to read NBT data from file '{}'", file.getAbsolutePath());
		}

		return nbt;
	}

	@Nullable
	public static NbtCompound readNbtFromFileAsPath(@Nonnull Path file, NbtSizeTracker tracker)
	{
		if (!Files.exists(file) || !Files.isReadable(file))
		{
			return null;
		}

		try
		{
			return NbtIo.readCompressed(Files.newInputStream(file), tracker);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("readNbtFromFileAsPath: Failed to read NBT data from file '{}'", file.toString());
		}

		return null;
	}

	/**
	 * Write the compound tag, gzipped, to the output stream.
	 */
	public static void writeCompressed(@Nonnull NbtCompound tag, @Nonnull OutputStream outputStream)
    {
		try
		{
			NbtIo.writeCompressed(tag, outputStream);
		}
		catch (Exception err)
		{
			MaLiLib.LOGGER.warn("writeCompressed: Failed to write NBT data to output stream");
		}
	}

	public static void writeCompressed(@Nonnull NbtCompound tag, @Nonnull Path file)
	{
		try
		{
			NbtIo.writeCompressed(tag, file);
		}
		catch (Exception err)
		{
			MaLiLib.LOGGER.warn("writeCompressed: Failed to write NBT data to file");
		}
	}

	// todo this must have been an older method for this that no longer works
	/*
	public static void writeCompressed(@Nonnull NbtCompound tag, String tagName, @Nonnull OutputStream outputStream)
    {
		try
		{
			DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));
			int typeId = NbtWrap.getTypeId(tag);
			output.writeByte(typeId);

			if (typeId != 0)
			{
				output.writeUTF(tagName);
				tag.write(output);
			}
		}
		catch (Exception err)
		{
			MaLiLib.LOGGER.warn("writeCompressed: Failed to write NBT data to file");
		}
	}
	 */

	/**
	 * Reads in a Flat Map from NBT -- this way we don't need Mojang's code complexity
	 * @param <T> ()
	 * @param nbt ()
	 * @param mapCodec ()
	 * @return ()
	 */
	public static <T> Optional<T> readFlatMap(@Nonnull NbtCompound nbt, MapCodec<T> mapCodec)
	{
		DynamicOps<NbtElement> ops = NbtOps.INSTANCE;

		return switch (ops.getMap(nbt).flatMap(map -> mapCodec.decode(ops, map)))
		{
			case DataResult.Success<T> result -> Optional.of(result.value());
			case DataResult.Error<T> error -> error.partialValue();
			default -> Optional.empty();
        };
	}

	/**
	 * Writes a Flat Map to NBT -- this way we don't need Mojang's code complexity
	 * @param <T> ()
	 * @param mapCodec ()
	 * @param value ()
	 * @return ()
	 */
	public static <T> NbtCompound writeFlatMap(MapCodec<T> mapCodec, T value)
	{
		DynamicOps<NbtElement> ops = NbtOps.INSTANCE;
		NbtCompound nbt = new NbtCompound();

		switch (mapCodec.encoder().encodeStart(ops, value))
		{
			case DataResult.Success<NbtElement> result -> nbt.copyFrom((NbtCompound) result.value());
			case DataResult.Error<NbtElement> error -> error.partialValue().ifPresent(partial -> nbt.copyFrom((NbtCompound) partial));
		}

		return nbt;
	}
}
