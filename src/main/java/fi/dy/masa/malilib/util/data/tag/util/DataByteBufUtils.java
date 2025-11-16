package fi.dy.masa.malilib.util.data.tag.util;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.UnpooledByteBufAllocator;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.tag.BaseData;
import fi.dy.masa.malilib.util.data.tag.EmptyData;

public class DataByteBufUtils
{
	/**
	 * Write to Data Tags from a {@link ByteBuf}
	 *
	 * @param byteBuf   The {@link ByteBuf} to read Data from
	 * @return          The {@link Optional} of the resulting data or empty
	 */
	public static Optional<BaseData> fromByteBuf(ByteBuf byteBuf)
	{
		try (ByteBufInputStream is = new ByteBufInputStream(byteBuf))
		{
			return Optional.ofNullable(DataFileUtils.readFromNbtStream(is));
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("Exception while reading data from ByteBuf; {}", e.getLocalizedMessage());
		}

		return Optional.empty();
	}

	/**
	 * Write Data Tags to a new Unpooled {@link ByteBuf}
	 *
	 * @param data          The Data Tags to write
	 * @param rootTagName   The Root Tag Name
	 * @return              The {@link ByteBuf} or an Unpooled Buffer
	 */
	public static ByteBuf toByteBuf(@Nullable BaseData data, String rootTagName)
	{
		ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
		return toByteBuf(byteBuf, data, rootTagName);
	}

	/**
	 * Write Data Tags to an existing {@link ByteBuf}
	 *
	 * @param byteBuf       The input {@link ByteBuf} to add data to.  Buffer must be allocated.
	 * @param data          The Data Tags to write
	 * @param rootTagName   The Root Tag Name
	 * @return              The {@link ByteBuf} or an Unpooled Buffer
	 */
	public static ByteBuf toByteBuf(@Nonnull ByteBuf byteBuf, @Nullable BaseData data, String rootTagName)
	{
		if (data == null || data.isEmpty())
		{
			data = EmptyData.INSTANCE;
		}

		try (ByteBufOutputStream os = new ByteBufOutputStream(byteBuf))
		{
			DataFileUtils.writeToNbtStream(os, data, rootTagName);
		}
		catch (Exception e)
		{
			MaLiLib.LOGGER.error("Exception while writing data to a ByteBuf; {}", e.getLocalizedMessage());
		}

		// Return remaining Buffer
		return byteBuf;
	}
}
