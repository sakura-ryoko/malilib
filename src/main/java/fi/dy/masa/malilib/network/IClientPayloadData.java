package fi.dy.masa.malilib.network;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A Helper Interface for designing a Payload Encoder/Decoder class with some common functions
 * Using this interface is completely optional.
 */
public interface IClientPayloadData
{
    /**
     * Returns a numerical version for your Protocol
     * @return (The Version)
     */
    int getVersion();

    /**
     * Returns a common packet "Type" value
     * @return (The Packet Type)
     */
    int getPacketType();

    /**
     * Returns the total size (in bytes) of this Data
     * @return (The implementation's Data Allocation Footprint)
     */
    int getTotalSize();

    /**
     * Informs if this data is currently in use
     * @return (True/False)
     */
    boolean isEmpty();

    /**
     * PacketByteBuf Decoder -- How this Data is converted FROM a PacketByteBuf
     * [NOTE]: In order for this to work, it needs to call a static version of this.
     * This version is only for guidance.
     * -
     * @param input (Incoming Packet)
     * @return (A new instance of the implementation class)
     * @param <T> (The implementation class)
     */
    @Nullable
    static <T extends IClientPayloadData> T fromPacket(FriendlyByteBuf input) { return null; }

    /**
     * PacketByteBuf Encoder -- How this Data is converted TO a PacketByteBuf
     * @param output (A new Pooled Buffer containing this implementation's data)
     */
    void toPacket(FriendlyByteBuf output);

    /**
     * Clear / Reset any values that need to be cleared
     */
    void clear();
}
