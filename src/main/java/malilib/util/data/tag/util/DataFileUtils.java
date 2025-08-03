package malilib.util.data.tag.util;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import javax.annotation.Nullable;

import malilib.MaLiLib;
import malilib.util.data.Constants;
import malilib.util.data.tag.BaseData;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.DataView;

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
                MaLiLib.LOGGER.warn("Failed to read (assumed uncompressed) NBT data from file '{}'", file.toAbsolutePath(), e2);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to read NBT data from file '{}'", file.toAbsolutePath(), e);
        }

        if (data instanceof CompoundData)
        {
            return (CompoundData) data;
        }

        return null;
    }

    public static boolean writeCompoundDataToCompressedNbtFile(Path fileOut, DataView dataIn)
    {
        return true;
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
            MaLiLib.LOGGER.warn("DataFileUtils.read: Exception while reading NBT data", e);
        }

        return null;
    }
}
