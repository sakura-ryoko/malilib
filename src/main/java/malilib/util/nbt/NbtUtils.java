package malilib.util.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import malilib.MaLiLib;
import malilib.mixin.access.NBTBaseMixin;
import malilib.util.data.Constants;
import malilib.util.game.wrap.NbtWrap;
import malilib.util.position.BlockPos;
import malilib.util.position.Vec3d;
import malilib.util.position.Vec3i;

public class NbtUtils
{
    @Nullable
    public static UUID readUUID(NBTTagCompound tag)
    {
        return readUUID(tag, "UUIDM", "UUIDL");
    }

    @Nullable
    public static UUID readUUID(NBTTagCompound tag, String keyM, String keyL)
    {
        if (NbtWrap.containsLong(tag, keyM) && NbtWrap.containsLong(tag, keyL))
        {
            return new UUID(NbtWrap.getLong(tag, keyM), NbtWrap.getLong(tag, keyL));
        }

        return null;
    }

    public static void writeUUID(NBTTagCompound tag, UUID uuid)
    {
        writeUUID(tag, uuid, "UUIDM", "UUIDL");
    }

    public static void writeUUID(NBTTagCompound tag, UUID uuid, String keyM, String keyL)
    {
        NbtWrap.putLong(tag, keyM, uuid.getMostSignificantBits());
        NbtWrap.putLong(tag, keyL, uuid.getLeastSignificantBits());
    }

    public static NBTTagCompound getOrCreateCompound(NBTTagCompound tagIn, String tagName)
    {
        NBTTagCompound nbt;

        if (NbtWrap.containsCompound(tagIn, tagName))
        {
            nbt = NbtWrap.getCompound(tagIn, tagName);
        }
        else
        {
            nbt = new NBTTagCompound();
            NbtWrap.putTag(tagIn, tagName, nbt);
        }

        return nbt;
    }

    public static <T> NBTTagList asListTag(Collection<T> values, Function<T, NBTBase> tagFactory)
    {
        NBTTagList list = new NBTTagList();

        for (T val : values)
        {
            NbtWrap.addTag(list, tagFactory.apply(val));
        }

        return list;
    }

    public static NBTTagCompound createBlockPosTag(Vec3i pos)
    {
        return putVec3i(new NBTTagCompound(), pos);
    }

    public static NBTTagCompound putVec3i(NBTTagCompound tag, Vec3i pos)
    {
        NbtWrap.putInt(tag, "x", pos.getX());
        NbtWrap.putInt(tag, "y", pos.getY());
        NbtWrap.putInt(tag, "z", pos.getZ());
        return tag;
    }

    @Nullable
    public static NBTTagCompound writeBlockPosToListTag(Vec3i pos, NBTTagCompound tag, String tagName)
    {
        NBTTagList tagList = new NBTTagList();

        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getX()));
        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getY()));
        NbtWrap.addTag(tagList, NbtWrap.asIntTag(pos.getZ()));
        NbtWrap.putTag(tag, tagName, tagList);

        return tag;
    }

    @Nullable
    public static NBTTagCompound writeBlockPosToArrayTag(Vec3i pos, NBTTagCompound tag, String tagName)
    {
        int[] arr = new int[] { pos.getX(), pos.getY(), pos.getZ() };

        NbtWrap.putIntArray(tag, tagName, arr);

        return tag;
    }

    @Nullable
    public static BlockPos readBlockPos(@Nullable NBTTagCompound tag)
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
    public static BlockPos readBlockPosFromListTag(NBTTagCompound tag, String tagName)
    {
        if (NbtWrap.containsList(tag, tagName))
        {
            NBTTagList tagList = NbtWrap.getList(tag, tagName, Constants.NBT.TAG_INT);

            if (NbtWrap.getListSize(tagList) == 3)
            {
                return new BlockPos(NbtWrap.getIntAt(tagList, 0), NbtWrap.getIntAt(tagList, 1), NbtWrap.getIntAt(tagList, 2));
            }
        }

        return null;
    }

    @Nullable
    public static BlockPos readBlockPosFromArrayTag(NBTTagCompound tag, String tagName)
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

    public static NBTTagCompound removeBlockPosFromTag(NBTTagCompound tag)
    {
        NbtWrap.remove(tag, "x");
        NbtWrap.remove(tag, "y");
        NbtWrap.remove(tag, "z");

        return tag;
    }

    public static NBTTagCompound writeVec3dToListTag(Vec3d pos, NBTTagCompound tag)
    {
        return writeVec3dToListTag(pos, tag, "Pos");
    }

    public static NBTTagCompound writeVec3dToListTag(Vec3d pos, NBTTagCompound tag, String tagName)
    {
        NBTTagList posList = new NBTTagList();

        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.x));
        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.y));
        NbtWrap.addTag(posList, NbtWrap.asDoubleTag(pos.z));
        NbtWrap.putTag(tag, tagName, posList);

        return tag;
    }

    @Nullable
    public static Vec3d readVec3d(@Nullable NBTTagCompound tag)
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
    public static Vec3d readVec3dFromListTag(@Nullable NBTTagCompound tag)
    {
        return readVec3dFromListTag(tag, "Pos");
    }

    @Nullable
    public static Vec3d readVec3dFromListTag(@Nullable NBTTagCompound tag, String tagName)
    {
        if (tag != null && NbtWrap.containsList(tag, tagName))
        {
            NBTTagList tagList = NbtWrap.getList(tag, tagName, Constants.NBT.TAG_DOUBLE);

            if (NbtWrap.getListStoredType(tagList) == Constants.NBT.TAG_DOUBLE && NbtWrap.getListSize(tagList) == 3)
            {
                return new Vec3d(NbtWrap.getDoubleAt(tagList, 0), NbtWrap.getDoubleAt(tagList, 1), NbtWrap.getDoubleAt(tagList, 2));
            }
        }

        return null;
    }

    @Nullable
    public static NBTTagCompound readNbtFromFile(Path file)
    {
        if (Files.isReadable(file) == false)
        {
            return null;
        }

        try (InputStream is = Files.newInputStream(file))
        {
            return CompressedStreamTools.readCompressed(is);
        }
        catch (ZipException e)
        {
            // Maybe the file is uncompressed, attempt to read it as such
            try (DataInputStream is = new DataInputStream(new BufferedInputStream(Files.newInputStream(file))))
            {
                return CompressedStreamTools.read(is);
            }
            catch (Exception e2)
            {
                MaLiLib.LOGGER.warn("Failed to read (assumed uncompressed) NBT data from file '{}'", file.toAbsolutePath(), e2);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read NBT data from file '{}'", file.toAbsolutePath(), e);
        }

        return null;
    }

    /**
     * Write the compound tag, gzipped, to the output stream.
     */
    public static void writeCompressed(NBTTagCompound tag, String tagName, OutputStream outputStream) throws IOException
    {
        try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream))))
        {
            writeTag(tag, tagName, dataoutputstream);
        }
    }

    private static void writeTag(NBTBase tag, String tagName, DataOutput output) throws IOException
    {
        int typeId = NbtWrap.getTypeId(tag);
        output.writeByte(typeId);

        if (typeId != 0)
        {
            output.writeUTF(tagName);
            ((NBTBaseMixin) tag).invokeWrite(output);
        }
    }
}
