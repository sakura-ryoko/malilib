package fi.dy.masa.malilib.util.nbt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.Constants;
import fi.dy.masa.malilib.util.game.wrap.NbtWrap;
import fi.dy.masa.malilib.util.position.Vec3d;
import fi.dy.masa.malilib.util.position.Vec3i;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
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
        if (NbtWrap.containsLong(tag, keyM) && NbtWrap.containsLong(tag, keyL))
        {
            return new UUID(NbtWrap.getLong(tag, keyM), NbtWrap.getLong(tag, keyL));
        }

        return null;
    }

    public static void writeUUID(@Nonnull NbtCompound tag, UUID uuid)
    {
        writeUUID(tag, uuid, "UUIDM", "UUIDL");
    }

    public static void writeUUID(@Nonnull NbtCompound tag, UUID uuid, String keyM, String keyL)
    {
        NbtWrap.putLong(tag, keyM, uuid.getMostSignificantBits());
        NbtWrap.putLong(tag, keyL, uuid.getLeastSignificantBits());
    }

    public static NbtCompound getOrCreateCompound(@Nonnull NbtCompound tagIn, String tagName)
    {
        NbtCompound nbt;

        if (NbtWrap.containsCompound(tagIn, tagName))
        {
            nbt = NbtWrap.getCompound(tagIn, tagName);
        }
        else
        {
            nbt = new NbtCompound();
            NbtWrap.putTag(tagIn, tagName, nbt);
        }

        return nbt;
    }

    public static <T> NbtList asListTag(Collection<T> values, Function<T, NbtElement> tagFactory)
    {
        NbtList list = new NbtList();

        for (T val : values)
        {
            NbtWrap.addTag(list, tagFactory.apply(val));
        }

        return list;
    }

    public static NbtCompound createBlockPosTag(Vec3i pos)
    {
        return putVec3i(new NbtCompound(), pos);
    }

    public static NbtCompound putVec3i(@Nonnull NbtCompound tag, @Nonnull Vec3i pos)
    {
        NbtWrap.putInt(tag, "x", pos.getX());
        NbtWrap.putInt(tag, "y", pos.getY());
        NbtWrap.putInt(tag, "z", pos.getZ());
        return tag;
    }

    public static @NotNull NbtCompound writeBlockPosToListTag(@Nonnull Vec3i pos, @Nonnull NbtCompound tag, String tagName)
    {
        NbtList tagList = new NbtList();

        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getX()));
        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getY()));
        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getZ()));
        NbtWrap.putTag(tag, tagName, tagList);

        return tag;
    }

    public static @NotNull NbtCompound writeBlockPosToArrayTag(@Nonnull Vec3i pos, @Nonnull NbtCompound tag, String tagName)
    {
        int[] arr = new int[] { pos.getX(), pos.getY(), pos.getZ() };

        NbtWrap.putIntArray(tag, tagName, arr);

        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            NbtWrap.containsInt(tag, "x") &&
            NbtWrap.containsInt(tag, "y") &&
            NbtWrap.containsInt(tag, "z"))
        {
            return new BlockPos(NbtWrap.getInt(tag, "x"), NbtWrap.getInt(tag, "y"), NbtWrap.getInt(tag, "z"));
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromListTag(@Nonnull NbtCompound tag, String tagName)
    {
        if (NbtWrap.containsList(tag, tagName))
        {
            NbtList tagList = NbtWrap.getList(tag, tagName, Constants.NBT.TAG_INT);

            if (NbtWrap.getListSize(tagList) == 3)
            {
                return new BlockPos(NbtWrap.getIntAt(tagList, 0), NbtWrap.getIntAt(tagList, 1), NbtWrap.getIntAt(tagList, 2));
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
        if (NbtWrap.containsIntArray(tag, tagName))
        {
            int[] pos = NbtWrap.getIntArray(tag, tagName);

            if (pos.length == 3)
            {
                return new BlockPos(pos[0], pos[1], pos[2]);
            }
        }

        return null;
    }

    public static NbtCompound removeBlockPosFromTag(@Nonnull NbtCompound tag)
    {
        NbtWrap.remove(tag, "x");
        NbtWrap.remove(tag, "y");
        NbtWrap.remove(tag, "z");

        return tag;
    }

    public static NbtCompound writeVec3dToListTag(@Nonnull Vec3d pos, @Nonnull NbtCompound tag)
    {
        return writeVec3dToListTag(pos, tag, NbtKeys.POS);
    }

    public static NbtCompound writeVec3dToListTag(@Nonnull Vec3d pos, @Nonnull NbtCompound tag, String tagName)
    {
        NbtList posList = new NbtList();

        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.x));
        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.y));
        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.z));
        NbtWrap.putTag(tag, tagName, posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable NbtCompound tag)
    {
        if (tag != null &&
            NbtWrap.containsDouble(tag, "dx") &&
            NbtWrap.containsDouble(tag, "dy") &&
            NbtWrap.containsDouble(tag, "dz"))
        {
            return new Vec3d(NbtWrap.getDouble(tag, "dx"), NbtWrap.getDouble(tag, "dy"), NbtWrap.getDouble(tag, "dz"));
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
        if (tag != null && NbtWrap.containsList(tag, tagName))
        {
            NbtList tagList = NbtWrap.getList(tag, tagName, Constants.NBT.TAG_DOUBLE);

            if (NbtWrap.getListStoredType(tagList) == Constants.NBT.TAG_DOUBLE && NbtWrap.getListSize(tagList) == 3)
            {
                return new Vec3d(NbtWrap.getDoubleAt(tagList, 0), NbtWrap.getDoubleAt(tagList, 1), NbtWrap.getDoubleAt(tagList, 2));
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
        if (tag.contains("TileX", Constants.NBT.TAG_INT) &&
            tag.contains("TileY", Constants.NBT.TAG_INT) &&
            tag.contains("TileZ", Constants.NBT.TAG_INT))
        {
            return new BlockPos(tag.getInt("TileX"), tag.getInt("TileY"), tag.getInt("TileZ"));
        }

        return null;
    }

    /**
     * Write the "Block Attached" BlockPos to NBT.
     *
     * @param pos ()
     * @param tag ()
     * @return ()
     */
    public static NbtCompound writeAttachedPosToTag(@Nonnull BlockPos pos, @Nonnull NbtCompound tag)
    {
        tag.putInt("TileX", pos.getX());
        tag.putInt("TileY", pos.getY());
        tag.putInt("TileZ", pos.getZ());

        return tag;
    }
    
    @Nullable
    public static NbtCompound readNbtFromFile(@Nonnull Path file)
    {
        return readNbtFromFile(file, NbtSizeTracker.ofUnlimitedBytes());
    }
    
    @Nullable
    public static NbtCompound readNbtFromFile(@Nonnull Path file, NbtSizeTracker tracker)
    {
        if (Files.isReadable(file) == false)
        {
            return null;
        }

        try (InputStream is = Files.newInputStream(file))
        {
            NbtIo.readCompressed(is, tracker);
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read NBT data from file '{}'", file.toAbsolutePath());
        }

        return null;
    }

    /**
     * Write the compound tag, gzipped, to the output stream.
     */
    public static void writeCompressed(@Nonnull NbtCompound tag, String tagName, @Nonnull OutputStream outputStream) throws IOException
    {
        try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream))))
        {
            writeTag(tag, tagName, dataoutputstream);
        }
    }
    
    private static void writeTag(NbtElement tag, String tagName, DataOutput output) throws IOException
    {
        int typeId = NbtWrap.getTypeId(tag);
        output.writeByte(typeId);

        if (typeId != 0)
        {
            output.writeUTF(tagName);
            tag.write(output);
        }
    }
}
