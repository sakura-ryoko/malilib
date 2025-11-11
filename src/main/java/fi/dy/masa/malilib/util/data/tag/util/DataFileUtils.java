package fi.dy.masa.malilib.util.data.tag.util;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.Constants;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;

public class DataFileUtils
{
    @Nullable
    public static CompoundData readCompoundDataFromNbtFile(Path file)
    {
        if (Files.isReadable(file) == false)
        {
            return null;
        }

        BaseData data = null;

        try (DataInputStream is = new DataInputStream(new BufferedInputStream(new GZIPInputStream(Files.newInputStream(file)))))
        {
            data = readFromNbtStream(is);
        }
        catch (ZipException e)
        {
            // Maybe the file is uncompressed, attempt to read it as such
            try (DataInputStream is = new DataInputStream(new BufferedInputStream(Files.newInputStream(file))))
            {
                data = readFromNbtStream(is);
            }
            catch (Exception e2)
            {
                MaLiLib.LOGGER.warn("DataFileUtils.readCompoundDataFromNbtFile: Failed to read (assumed uncompressed) NBT data from file '{}'", file.toAbsolutePath(), e2);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("DataFileUtils.readCompoundDataFromNbtFile: Failed to read NBT data from file '{}'", file.toAbsolutePath(), e);
        }

        if (data instanceof CompoundData)
        {
            return (CompoundData) data;
        }

        return null;
    }

    public static boolean writeCompoundDataToCompressedNbtFile(Path file, BaseData data)
    {
        return writeCompoundDataToCompressedNbtFile(file, data, "");
    }

    public static boolean writeCompoundDataToCompressedNbtFile(Path file, BaseData data, String rootTagName)
    {
        try (DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(Files.newOutputStream(file)))))
        {
            return writeToNbtStream(os, data, rootTagName);
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("DataFileUtils.writeCompoundDataToCompressedNbtFile: Failed to write NBT data to file '{}'", file.toAbsolutePath(), e);
        }

        return false;
    }

    @Nullable
    public static BaseData readFromNbtStream(DataInput input)
    {
        try
        {
            byte tagType = input.readByte();

            if (tagType == Constants.NBT.TAG_END)
            {
                return null;
            }

            // Discard the name of the root tag
            input.readUTF();

            return BaseData.createTag(Constants.NBT.TAG_COMPOUND, input, 0, new SizeTracker(0L));
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("DataFileUtils.readFromNbtStream: Exception while reading NBT data", e);
        }

        return null;
    }

    public static boolean writeToNbtStream(DataOutput output, BaseData data, String tagName)
    {
        try
        {
            output.writeByte(data.getType());

            if (data.getType() != Constants.NBT.TAG_END)
            {
                output.writeUTF(tagName);
                data.write(output);
            }

            return true;
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("DataFileUtils.writeToNbtStream: Exception while writing NBT data", e);
        }

        return false;
    }

	public static CompoundData readFromFileUsingNbtIo(@Nonnull Path file)
	{
		return readFromFileUsingNbtIo(file, NbtSizeTracker.ofUnlimitedBytes());
	}

	public static CompoundData readFromFileUsingNbtIo(@Nonnull Path file, NbtSizeTracker tracker)
	{
		if (!Files.exists(file) || !Files.isReadable(file))
		{
			return null;
		}

		try
		{
			return DataConverterNbt.fromVanillaCompound(NbtIo.readCompressed(Files.newInputStream(file), tracker));
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.warn("DataFileUtils.readFromFileUsingNbtIo: Failed to read NBT data from file '{}'", file.toString());
		}

		return null;
	}

	public static void writeToFileUsingNbtIo(@Nonnull CompoundData tag, @Nonnull Path file)
	{
		try
		{
			NbtIo.writeCompressed(DataConverterNbt.toVanillaCompound(tag), file);
		}
		catch (Exception err)
		{
			MaLiLib.LOGGER.warn("DataFileUtils.writeToFileUsingNbtIo: Failed to write NBT data to file");
		}
	}
}
