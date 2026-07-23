package fi.dy.masa.malilib.util.data.tag.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.PooledByteBufAllocator;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.EmptyData;

public class DataByteBufUtils
{
	/**
	 * Read to Data Tags from a {@link ByteBuf}
	 *
	 * @param byteBuf   The {@link ByteBuf} to read Data from
	 * @return          The {@link Optional} of the resulting data or empty
	 * @implNote        Remember to release the ByteBuf when done
	 */
	public static Optional<BaseData> fromByteBuf(ByteBuf byteBuf)
	{
		final int length = byteBuf.readInt();
		ByteBuf slice = byteBuf.readSlice(length);

		try (ByteBufInputStream bis = new ByteBufInputStream(slice))
		{
			try (GZIPInputStream gis = new GZIPInputStream(bis);
			     DataInputStream dis = new DataInputStream(gis))
			{
				return Optional.ofNullable(DataFileUtils.readFromNbtStream(dis));
			}
		}
		catch (ZipException e)
		{
			slice.resetReaderIndex();

			try (ByteBufInputStream bis = new ByteBufInputStream(slice);
			     DataInputStream dis = new DataInputStream(bis))
			{
				return Optional.ofNullable(DataFileUtils.readFromNbtStream(dis));
			}
			catch (Exception e2)
			{
				MaLiLib.LOGGER.error("DataByteBufUtils: Exception while reading data from (Uncompressed) ByteBuf; {}", e2.getLocalizedMessage());
			}
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("DataByteBufUtils: Exception while reading data from ByteBuf; {}", e.getLocalizedMessage());
		}

		return Optional.empty();
	}

	/**
	 * Write Data Tags to a new Pooled Buffer {@link ByteBuf}
	 *
	 * @param data        The Data Tags to write
	 * @param rootTagName The Root Tag Name
	 * @return The {@link ByteBuf} or a Pooled Buffer
	 * @implNote          Remember to release the ByteBuf when done
	 */
	public static ByteBuf toByteBuf(@Nullable BaseData data, String rootTagName) throws IOException
	{
		ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();

		try
		{
			return toByteBuf(byteBuf, data, rootTagName);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("DataByteBufUtils: Exception while writing data to a (new) ByteBuf; {}", e.getLocalizedMessage());
			byteBuf.release();
			throw e;
		}
	}

	/**
	 * Write Data Tags to an existing {@link ByteBuf}
	 *
	 * @param byteBuf     The input {@link ByteBuf} to add data to.  Buffer must be allocated.
	 * @param data        The Data Tags to write
	 * @param rootTagName The Root Tag Name
	 * @return The {@link ByteBuf} or a Pooled Buffer
	 * @implNote          Remember to release the ByteBuf when done
	 */
	public static ByteBuf toByteBuf(@Nonnull ByteBuf byteBuf, @Nullable BaseData data, String rootTagName)
			throws IOException
	{
		if (data == null || data.isEmpty())
		{
			data = EmptyData.INSTANCE;
		}

		ByteBuf tempBuf = byteBuf.alloc().buffer();

		try
		{
			try (DataOutputStream os = new DataOutputStream(new GZIPOutputStream(new ByteBufOutputStream(tempBuf))))
			{
				DataFileUtils.writeToNbtStream(os, data, rootTagName);
			}

			byteBuf.writeInt(tempBuf.readableBytes());
			byteBuf.writeBytes(tempBuf);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("DataByteBufUtils: Exception while writing data to a ByteBuf; {}", e.getLocalizedMessage());
			throw e;
		}
		finally
		{
			tempBuf.release();
		}

		return byteBuf;
	}
}
