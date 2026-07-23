package fi.dy.masa.malilib.network;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import fi.dy.masa.malilib.MaLiLib;

/**
 * Network packet splitter code from QuickCarpet by skyrising
 *
 * @author skyrising
 * -
 * Updated by Sakura to work with newer versions by changing the Reading Session keys,
 * and using the HANDLER interface to send packets via the Payload system
 */
public class PacketSplitter
{
	public static final int MAX_TOTAL_PER_PACKET_S2C = 1048576;
	public static final int MAX_PAYLOAD_PER_PACKET_S2C = MAX_TOTAL_PER_PACKET_S2C - 5;
	public static final int MAX_TOTAL_PER_PACKET_C2S = 32767;
	public static final int MAX_PAYLOAD_PER_PACKET_C2S = MAX_TOTAL_PER_PACKET_C2S - 5;
	public static final int DEFAULT_MAX_RECEIVE_SIZE_C2S = 16777216;         // 16mb Max Buffer -- Make smaller files
	public static final int DEFAULT_MAX_RECEIVE_SIZE_S2C = 16777216;

	private static final ConcurrentHashMap<Long, ReadingSession> READING_SESSIONS = new ConcurrentHashMap<>(16, 0.9f, 2);
	private static final long STALE_TIMEOUT_MS = 10000; // 10 seconds before eviction
	private static final ScheduledExecutorService CLEANER_EXECUTOR = Executors
			.newSingleThreadScheduledExecutor(r ->
			                                  {
				                                  Thread t = new Thread(r, "PacketSplitter-Cleaner");
				                                  t.setDaemon(true);
				                                  return t;
			                                  });

	// Periodically evict stale sessions to prevent memory leaks/DoS
	static
	{
		CLEANER_EXECUTOR
				.scheduleAtFixedRate(() ->
				                     {
					                     final long now = System.currentTimeMillis();
					                     var iterator = READING_SESSIONS.entrySet().iterator();

					                     while (iterator.hasNext())
					                     {
						                     var entry = iterator.next();

						                     if ((now - entry.getValue().lastReceivedTime) > STALE_TIMEOUT_MS)
						                     {
							                     MaLiLib.LOGGER.warn("Evicting reading session [{}]", entry.getKey());
							                     entry.getValue().release();
							                     iterator.remove();
						                     }
					                     }
				                     }, 5, 5, TimeUnit.SECONDS);
	}

	public static <T extends CustomPacketPayload> boolean send(IPluginClientPlayHandler<T> handler, FriendlyByteBuf packet, ClientPacketListener networkHandler)
	{
		return send(handler, packet, MAX_PAYLOAD_PER_PACKET_C2S, networkHandler);
	}

	private static <T extends CustomPacketPayload> boolean send(IPluginClientPlayHandler<T> handler, FriendlyByteBuf packet, int payloadLimit, ClientPacketListener networkHandler)
	{
		int len = packet.writerIndex();

		packet.resetReaderIndex();

		for (int offset = 0; offset < len; offset += payloadLimit)
		{
			int thisLen = Math.min(len - offset, payloadLimit);
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(thisLen));

			buf.resetWriterIndex();

			if (offset == 0)
			{
				buf.writeVarInt(len);
			}

			buf.writeBytes(packet, thisLen);
			handler.encodeWithSplitter(buf, networkHandler);
		}

		packet.release();

		return true;
	}

	public static <T extends CustomPacketPayload> FriendlyByteBuf receive(IPluginClientPlayHandler<T> handler,
	                                                                      long key,
	                                                                      FriendlyByteBuf buf)
	{
		return receive(handler.getPayloadChannel(), key, buf, DEFAULT_MAX_RECEIVE_SIZE_S2C);
	}

	@Nullable
	private static FriendlyByteBuf receive(Identifier channel,
	                                       long key,
	                                       FriendlyByteBuf buf,
	                                       int maxLength)
	{
		return READING_SESSIONS.computeIfAbsent(key, ReadingSession::new).receive(buf, maxLength);
	}

	/**
	 * I had to fix the `Pair.of` key mappings, because they were removed from MC;
	 * So I made it into a pre-shared random session 'key' between client and server.
	 * Generated using 'long key = Random.create(Util.getMeasuringTimeMs()).nextLong();'
	 * -
	 * It can be shared to the receiving end via a separate packet; or it can just be
	 * generated randomly on the receiving end per an expected Reading Session.
	 * It needs to be stored and changed for every unique session.
	 */
	private static class ReadingSession
	{
		private final long key;
		private int expectedSize = -1;
		private FriendlyByteBuf received;
		private long lastReceivedTime;

		private ReadingSession(long key)
		{
			this.key = key;
			this.lastReceivedTime = System.currentTimeMillis();
		}

		@Nullable
		private FriendlyByteBuf receive(FriendlyByteBuf data, int maxLength)
		{
			data.readerIndex(0);

			if (this.expectedSize < 0)
			{
				this.expectedSize = data.readVarInt();

				if (this.expectedSize > maxLength)
				{
					READING_SESSIONS.remove(this.key);
					throw new PacketSplitterException("Payload size " + this.expectedSize + " exceeds limit.");
				}

				if (this.expectedSize > 0 && data.readableBytes() == 0)
				{
					READING_SESSIONS.remove(this.key);
					throw new PacketSplitterException("Received size header but no data bytes.");
				}

				if (this.expectedSize == 0 && data.readableBytes() > 0)
				{
					READING_SESSIONS.remove(this.key);
					return null;
				}

				this.received = new FriendlyByteBuf(Unpooled.buffer(this.expectedSize));
			}

			if (this.received == null)
			{
				throw new NullPointerException("Receive Buffer is empty");
			}

			this.received.writeBytes(data);

			if (this.received.writerIndex() >= this.expectedSize)
			{
				READING_SESSIONS.remove(this.key);
				return this.received;
			}

			return null;
		}

		private void release()
		{
			if (this.received != null)
			{
				this.received.release();
				this.received = null;
			}
		}
	}
}
