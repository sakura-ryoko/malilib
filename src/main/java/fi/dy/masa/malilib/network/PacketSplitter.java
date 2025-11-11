package fi.dy.masa.malilib.network;

import javax.annotation.Nullable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;
import io.netty.buffer.Unpooled;

/**
 * Network packet splitter code from QuickCarpet by skyrising
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
    public static final int DEFAULT_MAX_RECEIVE_SIZE_C2S = 1048576;
    public static final int DEFAULT_MAX_RECEIVE_SIZE_S2C = 67108864;

    private static final Map<Long, ReadingSession> READING_SESSIONS = new HashMap<>();

    public static <T extends CustomPayload> boolean send(IPluginClientPlayHandler<T> handler, PacketByteBuf packet, ClientPlayNetworkHandler networkHandler)
    {
        return send(handler, packet, MAX_PAYLOAD_PER_PACKET_C2S, networkHandler);
    }

    private static <T extends CustomPayload> boolean send(IPluginClientPlayHandler<T> handler, PacketByteBuf packet, int payloadLimit, ClientPlayNetworkHandler networkHandler)
    {
        int len = packet.writerIndex();

        packet.resetReaderIndex();

        for (int offset = 0; offset < len; offset += payloadLimit)
        {
            int thisLen = Math.min(len - offset, payloadLimit);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(thisLen));

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

    public static <T extends CustomPayload> PacketByteBuf receive(IPluginClientPlayHandler<T> handler,
                                                                  long key,
                                                                  PacketByteBuf buf)
    {
        return receive(handler.getPayloadChannel(), key, buf, DEFAULT_MAX_RECEIVE_SIZE_S2C);
    }

    @Nullable
    private static PacketByteBuf receive(Identifier channel,
                                         long key,
                                         PacketByteBuf buf,
                                         int maxLength)
    {
        return READING_SESSIONS.computeIfAbsent(key, ReadingSession::new).receive(buf, maxLength);
    }

    // Not needed
    /*
    public static PacketByteBuf readPayload(PacketByteBuf byteBuf)
    {
        PacketByteBuf newBuf = new PacketByteBuf(Unpooled.buffer());
        newBuf.writeBytes(byteBuf.copy());
        byteBuf.skipBytes(byteBuf.readableBytes());
        return newBuf;
    }

    **
     * Sends a packet type ID as a VarInt, and then the given Compound tag.
     *
    public static <T extends CustomPayload> void sendPacketTypeAndCompound(IPluginClientPlayHandler<T> handler, int packetType, NbtCompound data, ClientPlayNetworkHandler networkHandler)
    {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(packetType);
        buf.writeNbt(data);

        send(handler, buf, networkHandler);
    }
     */

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
        private PacketByteBuf received;

        private ReadingSession(long key)
        {
            this.key = key;
        }

        @Nullable
        private PacketByteBuf receive(PacketByteBuf data, int maxLength)
        {
            data.readerIndex(0);
            //data = PacketUtils.slice(data);

            if (this.expectedSize < 0)
            {
                this.expectedSize = data.readVarInt();

                if (this.expectedSize > maxLength)
                {
                    throw new IllegalArgumentException("Payload too large");
                }

                this.received = new PacketByteBuf(Unpooled.buffer(this.expectedSize));
            }

            if (this.received == null)
            {
                throw new RuntimeException("Receive Buffer is empty");
            }

            this.received.writeBytes(data.copy());

            if (this.received.writerIndex() >= this.expectedSize)
            {
                READING_SESSIONS.remove(this.key);
                return this.received;
            }

            return null;
        }
    }
}
