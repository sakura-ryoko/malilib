package fi.dy.masa.malilib.util.nbt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.*;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;

/**
 * Post-ReWrite code
 */
public class NbtUtils
{
	@Nullable
	public static UUID readUUID(@Nonnull CompoundTag tag)
	{
		return readUUID(tag, "UUIDM", "UUIDL");
	}

	@Nullable
	public static UUID readUUID(@Nonnull CompoundTag tag, String keyM, String keyL)
	{
		if (tag.contains(keyM) && tag.contains(keyL))
		{
			return new UUID(tag.getLongOr(keyM, 0L), tag.getLongOr(keyL, 0L));
		}

		return null;
	}

	public static void writeUUID(@Nonnull CompoundTag tag, UUID uuid)
	{
		writeUUID(tag, uuid, "UUIDM", "UUIDL");
	}

	public static void writeUUID(@Nonnull CompoundTag tag, UUID uuid, String keyM, String keyL)
	{
		tag.putLong(keyM, uuid.getMostSignificantBits());
		tag.putLong(keyL, uuid.getLeastSignificantBits());
	}

	public static CompoundTag getOrCreateCompound(@Nonnull CompoundTag tagIn, String tagName)
	{
		CompoundTag nbt;

		if (tagIn.contains(tagName))
		{
			nbt = tagIn.getCompoundOrEmpty(tagName);
		}
		else
		{
			nbt = new CompoundTag();
			tagIn.put(tagName, nbt);
		}

		return nbt;
	}

	public static <T> ListTag asListTag(Collection<T> values, Function<T, Tag> tagFactory)
	{
		ListTag list = new ListTag();

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
	public static @Nullable UUID getUUIDCodec(@Nonnull CompoundTag nbt)
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
	public static @Nullable UUID getUUIDCodec(@Nonnull CompoundTag nbt, String key)
	{
		if (nbt.contains(key))
		{
			return nbt.read(key, UUIDUtil.CODEC).orElse(null);
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
	public static CompoundTag putUUIDCodec(@Nonnull CompoundTag nbtIn, @Nonnull UUID uuid, String key)
	{
		nbtIn.store(key, UUIDUtil.CODEC, uuid);
		return nbtIn;
	}

	public static @Nonnull CompoundTag createBlockPos(@Nonnull BlockPos pos)
	{
		return writeBlockPos(pos, new CompoundTag());
	}

	public static @Nonnull CompoundTag createBlockPosTag(@Nonnull BlockPos pos)
	{
		return writeBlockPos(pos, new CompoundTag());
	}

	public static @Nonnull CompoundTag createBlockPosTag(@Nonnull Vec3i pos)
	{
		return putVec3i(new CompoundTag(), pos);
	}

	public static @Nonnull CompoundTag createVec3iTag(@Nonnull Vec3i pos)
	{
		return putVec3i(new CompoundTag(), pos);
	}

	public static @Nonnull CompoundTag createVec3iToArray(@Nonnull Vec3i pos, String tagName)
	{
		return writeBlockPosToArrayTag(pos, new CompoundTag(), tagName);
	}

	public static @Nonnull CompoundTag createVec3iToArrayTag(@Nonnull Vec3i pos, String tagName)
	{
		return writeBlockPosToArrayTag(pos, new CompoundTag(), tagName);
	}

	public static @Nonnull CompoundTag createEntityPosition(@Nonnull Vec3 pos)
	{
		return createEntityPositionToTag(pos);
	}

	public static @Nonnull CompoundTag createEntityPositionToTag(@Nonnull Vec3 pos)
	{
		return writeVec3dToListTag(pos, new CompoundTag(), NbtKeys.POS);
	}

	public static @Nonnull CompoundTag putVec3i(@Nonnull CompoundTag tag, @Nonnull Vec3i pos)
	{
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());
		return tag;
	}

	public static @Nonnull CompoundTag putVec2fCodec(@Nonnull CompoundTag tag, @Nonnull Vec2 pos, String key)
	{
		tag.store(key, Vec2.CODEC, pos);
		return tag;
	}

	public static @Nonnull CompoundTag putVec3iCodec(@Nonnull CompoundTag tag, @Nonnull Vec3i pos, String key)
	{
		tag.store(key, Vec3i.CODEC, pos);
		return tag;
	}

	public static @Nonnull CompoundTag putVec3dCodec(@Nonnull CompoundTag tag, @Nonnull Vec3 pos, String key)
	{
		tag.store(key, Vec3.CODEC, pos);
		return tag;
	}

	public static @Nonnull CompoundTag putPosCodec(@Nonnull CompoundTag tag, @Nonnull BlockPos pos, String key)
	{
		tag.store(key, BlockPos.CODEC, pos);
		return tag;
	}

	public static Vec2 getVec2fCodec(@Nonnull CompoundTag tag, String key)
	{
		return tag.read(key, Vec2.CODEC).orElse(Vec2.ZERO);
	}

	public static Vec3i getVec3iCodec(@Nonnull CompoundTag tag, String key)
	{
		return tag.read(key, Vec3i.CODEC).orElse(Vec3i.ZERO);
	}

	public static Vec3 getVec3dCodec(@Nonnull CompoundTag tag, String key)
	{
		return tag.read(key, Vec3.CODEC).orElse(Vec3.ZERO);
	}

	public static BlockPos getPosCodec(@Nonnull CompoundTag tag, String key)
	{
		return tag.read(key, BlockPos.CODEC).orElse(BlockPos.ZERO);
	}

	public static @Nonnull CompoundTag writeBlockPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundTag tag)
	{
		return writeBlockPos(pos, tag);
	}

	public static @Nonnull CompoundTag writeBlockPos(@Nonnull BlockPos pos, @Nonnull CompoundTag tag)
	{
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());

		return tag;
	}

	public static @Nonnull CompoundTag writeBlockPosToListTag(@Nonnull Vec3i pos, @Nonnull CompoundTag tag, String tagName)
	{
		ListTag tagList = new ListTag();

		tagList.add(IntTag.valueOf(pos.getX()));
		tagList.add(IntTag.valueOf(pos.getY()));
		tagList.add(IntTag.valueOf(pos.getZ()));
		tag.put(tagName, tagList);

		return tag;
	}

	public static @Nonnull CompoundTag writeVec3iToArray(@Nonnull Vec3i pos, @Nonnull CompoundTag tag, String tagName)
	{
		return writeBlockPosToArrayTag(pos, tag, tagName);
	}

	public static @Nonnull CompoundTag writeBlockPosToArrayTag(@Nonnull Vec3i pos, @Nonnull CompoundTag tag, String tagName)
	{
		int[] arr = new int[]{pos.getX(), pos.getY(), pos.getZ()};
		tag.putIntArray(tagName, arr);
		return tag;
	}

	@Nullable
	public static BlockPos readBlockPos(@Nullable CompoundTag tag)
	{
		if (tag != null &&
			tag.contains("x") &&
			tag.contains("y") &&
			tag.contains("z"))
		{
			return new BlockPos(tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0));
		}

		return null;
	}

	@Nullable
	public static Vec3i readVec3i(@Nullable CompoundTag tag)
	{
		return readVec3iFromTag(tag);
	}

	@Nullable
	public static Vec3i readVec3iFromTag(@Nullable CompoundTag tag)
	{
		if (tag != null &&
			tag.contains("x") &&
			tag.contains("y") &&
			tag.contains("z"))
		{
			return new Vec3i(tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0));
		}

		return null;
	}

	@Nullable
	public static BlockPos readBlockPosFromListTag(@Nonnull CompoundTag tag, String tagName)
	{
		if (tag.contains(tagName))
		{
			ListTag tagList = tag.getListOrEmpty(tagName);

			if (tagList.size() == 3)
			{
				return new BlockPos(tagList.getIntOr(0, 0), tagList.getIntOr(1, 0), tagList.getIntOr(2, 0));
			}
		}

		return null;
	}

	@Nullable
	public static BlockPos readBlockPosFromIntArray(@Nonnull CompoundTag nbt, String key)
	{
		return readBlockPosFromArrayTag(nbt, key);
	}

	@Nullable
	public static BlockPos readBlockPosFromArrayTag(@Nonnull CompoundTag tag, String tagName)
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
	public static Vec3i readVec3iFromIntArray(@Nonnull CompoundTag nbt, String key)
	{
		return readVec3iFromIntArrayTag(nbt, key);
	}

	@Nullable
	public static Vec3i readVec3iFromIntArrayTag(@Nonnull CompoundTag tag, String tagName)
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

	public static @Nonnull CompoundTag removeBlockPos(@Nonnull CompoundTag tag)
	{
		return removeBlockPosFromTag(tag);
	}

	public static @Nonnull CompoundTag removeBlockPosFromTag(@Nonnull CompoundTag tag)
	{
		tag.remove("x");
		tag.remove("y");
		tag.remove("z");

		return tag;
	}

	public static @Nonnull CompoundTag writeEntityPosition(@Nonnull Vec3 pos, @Nonnull CompoundTag tag)
	{
		return writeVec3dToListTag(pos, tag, NbtKeys.POS);
	}

	public static @Nonnull CompoundTag writeEntityPositionToTag(@Nonnull Vec3 pos, @Nonnull CompoundTag tag)
	{
		return writeVec3dToListTag(pos, tag, NbtKeys.POS);
	}

	public static @Nonnull CompoundTag writeVec3dToListTag(@Nonnull Vec3 pos, @Nonnull CompoundTag tag)
	{
		return writeVec3dToListTag(pos, tag, NbtKeys.POS);
	}

	public static @Nonnull CompoundTag writeVec3dToListTag(@Nonnull Vec3 pos, @Nonnull CompoundTag tag, String tagName)
	{
		ListTag posList = new ListTag();

		posList.add(DoubleTag.valueOf(pos.x));
		posList.add(DoubleTag.valueOf(pos.y));
		posList.add(DoubleTag.valueOf(pos.z));
		tag.put(tagName, posList);

		return tag;
	}

	@Nullable
	public static Vec3 readVec3d(@Nullable CompoundTag tag)
	{
		if (tag != null &&
			tag.contains("dx") &&
			tag.contains("dy") &&
			tag.contains("dz"))
		{
			return new Vec3(tag.getDoubleOr("dx", 0d), tag.getDoubleOr("dy", 0d), tag.getDoubleOr("dz", 0d));
		}

		return null;
	}

	@Nullable
	public static Vec3 readVec3dFromListTag(@Nullable CompoundTag tag)
	{
		return readVec3dFromListTag(tag, NbtKeys.POS);
	}

	@Nullable
	public static Vec3 readEntityPositionFromTag(@Nullable CompoundTag tag)
	{
		return readVec3dFromListTag(tag, NbtKeys.POS);
	}

	@Nullable
	public static Vec3 readVec3dFromListTag(@Nullable CompoundTag tag, String tagName)
	{
		if (tag != null && tag.contains(tagName))
		{
			ListTag tagList = tag.getListOrEmpty(tagName);

			if (tagList.getId() == Constants.NBT.TAG_DOUBLE && tagList.size() == 3)
			{
				return new Vec3(tagList.getDoubleOr(0, 0d), tagList.getDoubleOr(1, 0d), tagList.getDoubleOr(2, 0d));
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
	public static BlockPos readAttachedPosFromTag(@Nonnull CompoundTag tag)
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
	public static @Nonnull CompoundTag writeAttachedPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundTag tag)
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
	public static BlockPos readPrefixedPosFromTag(@Nonnull CompoundTag tag, String pre)
	{
		if (tag.contains(pre+"X") &&
			tag.contains(pre+"Y") &&
			tag.contains(pre+"Z"))
		{
			return new BlockPos(tag.getIntOr(pre+"X", 0), tag.getIntOr(pre+"Y", 0), tag.getIntOr(pre+"Z", 0));
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
	public static @Nonnull CompoundTag writePrefixedPosToTag(@Nonnull BlockPos pos, @Nonnull CompoundTag tag, String pre)
	{
		tag.putInt(pre+"X", pos.getX());
		tag.putInt(pre+"Y", pos.getY());
		tag.putInt(pre+"Z", pos.getZ());

		return tag;
	}

	@SuppressWarnings("deprecation")
	public static Direction readDirectionFromTag(@Nonnull CompoundTag tag, String key)
	{
		Tag ele = tag.get(key);

		if (ele != null)
		{
			if (ele.getId() == Constants.NBT.TAG_INT)
			{
				return tag.read(key, Direction.LEGACY_ID_CODEC).orElse(Direction.SOUTH);
			}
			else if (ele.getId() == Constants.NBT.TAG_STRING)
			{
				return tag.read(key, Direction.CODEC).orElse(Direction.SOUTH);
			}
		}

		return Direction.SOUTH;
	}

	@SuppressWarnings("deprecation")
	public static CompoundTag writeDirectionToTagAsInt(@Nonnull CompoundTag tagIn, String key, Direction direction)
	{
		tagIn.store(key, Direction.LEGACY_ID_CODEC, direction);

		return tagIn;
	}

	public static CompoundTag writeDirectionToTagAsString(@Nonnull CompoundTag tagIn, String key, Direction direction)
	{
		tagIn.store(key, Direction.CODEC, direction);

		return tagIn;
	}

	/**
	 * See {@link #readNbtFromFileAsPath}
	 */
	@Nullable
	public static CompoundTag readNbtFromFile(@Nonnull Path file)
	{
		return readNbtFromFile(file, NbtAccounter.unlimitedHeap());
	}

	@Nullable
	public static CompoundTag readNbtFromFileAsPath(@Nonnull Path file)
	{
		return readNbtFromFileAsPath(file, NbtAccounter.unlimitedHeap());
	}

	/**
	 * See {@link #readNbtFromFileAsPath}
	 */
	@ApiStatus.Experimental
	@Nullable
	public static CompoundTag readNbtFromFile(@Nonnull Path file, NbtAccounter tracker)
	{
		if (!Files.exists(file) || !Files.isReadable(file))
		{
			return null;
		}

		InputStream is;

		try
		{
			is = Files.newInputStream(file, StandardOpenOption.READ);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("readNbtFromFile: Failed to read NBT data from file '{}' (failed to create the input stream)", file.toAbsolutePath());
			return null;
		}

		CompoundTag nbt = null;

		if (is != null)
		{
			try
			{
				nbt = NbtIo.read(new DataInputStream(new BufferedInputStream(new GZIPInputStream(is))), tracker);
			}
			catch (Exception e)
			{
				try
				{
					is.close();
					is = Files.newInputStream(file, StandardOpenOption.READ);
					nbt = NbtIo.read(new DataInputStream(new BufferedInputStream(is)), tracker);
				}
				catch (Exception ignore) {}
			}

			try
			{
				is.close();
			}
			catch (Exception ignore) {}
		}

		if (nbt == null || nbt.getId() == Constants.NBT.TAG_END)
		{
			MaLiLib.LOGGER.warn("readNbtFromFile: Failed to read NBT data from file '{}'", file.toAbsolutePath());
		}

		return nbt;
	}

	@Nullable
	public static CompoundTag readNbtFromFileAsPath(@Nonnull Path file, NbtAccounter tracker)
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
	public static void writeCompressed(@Nonnull CompoundTag tag, @Nonnull OutputStream outputStream)
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

	public static void writeCompressed(@Nonnull CompoundTag tag, @Nonnull Path file)
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

	@ApiStatus.Experimental
	public static boolean writeCompoundTagToCompressedFile(@Nonnull CompoundTag tag, @Nonnull Path file)
	{
		return writeCompoundTagToCompressedFile(tag, file, "");
	}

	@ApiStatus.Experimental
	public static boolean writeCompoundTagToCompressedFile(@Nonnull CompoundTag tag, @Nonnull Path file, String tagName)
	{
		try (DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(Files.newOutputStream(file)))))
		{
//			NbtIo.write(tag, dos);
			return writeToNbtStream(tag, os, tagName);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("writeCompressedTest: Failed to write NBT data to file '{}'; {}", file.toAbsolutePath(), e.getLocalizedMessage());
		}

		return false;
	}

	@ApiStatus.Experimental
	public static boolean writeToNbtStream(@Nonnull Tag tag, @Nonnull DataOutput os)
	{
		return writeToNbtStream(tag, os, "");
	}

	@ApiStatus.Experimental
	public static boolean writeToNbtStream(@Nonnull Tag tag, @Nonnull DataOutput os, String tagName)
	{
		try
		{
			os.writeByte(tag.getId());

			if (tag.getId() != Constants.NBT.TAG_END)
			{
				os.writeUTF(tagName);
				tag.write(os);
			}

			return true;
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("writeToNbtStream: Exception while writing NBT data; {}", e.getLocalizedMessage());
		}

		return false;
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
	public static <T> Optional<T> readFlatMap(@Nonnull CompoundTag nbt, MapCodec<T> mapCodec)
	{
		DynamicOps<Tag> ops = NbtOps.INSTANCE;

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
	public static <T> CompoundTag writeFlatMap(MapCodec<T> mapCodec, T value)
	{
		DynamicOps<Tag> ops = NbtOps.INSTANCE;
		CompoundTag nbt = new CompoundTag();

		switch (mapCodec.encoder().encodeStart(ops, value))
		{
			case DataResult.Success<Tag> result -> nbt.merge((CompoundTag) result.value());
			case DataResult.Error<Tag> error -> error.partialValue().ifPresent(partial -> nbt.merge((CompoundTag) partial));
		}

		return nbt;
	}
}
