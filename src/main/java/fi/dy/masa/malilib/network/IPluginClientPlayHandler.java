package fi.dy.masa.malilib.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.MaLiLib;

/**
 * Interface for ClientPlayHandler, for downstream mods.
 * @param <T> (Payload)
 */
public interface IPluginClientPlayHandler<T extends CustomPayload> extends ClientPlayNetworking.PlayPayloadHandler<T>
{
    int FROM_SERVER = 1;
    int TO_SERVER = 2;
    int BOTH_SERVER = 3;
    int TO_CLIENT = 4;
    int FROM_CLIENT = 5;
    int BOTH_CLIENT = 6;

    /**
     * Returns your HANDLER's CHANNEL ID
     * @return (Channel ID)
     */
    Identifier getPayloadChannel();

    /**
     * Returns if your Channel ID has been registered to your Play Payload.
     * @param channel (Your Channel ID)
     * @return (true / false)
     */
    boolean isPlayRegistered(Identifier channel);

    /**
     * Sets your HANDLER as registered.
     * @param channel (Your Channel ID)
     */
    void setPlayRegistered(Identifier channel);

    /**
     * Send your HANDLER a global reset() event, such as when the client is shutting down, or logging out.
     * @param channel (Your Channel ID)
     */
    void reset(Identifier channel);

    /**
     * Register your Payload with Fabric API.
     * See the fabric-networking-api-v1 Java Docs under PayloadTypeRegistry -> register()
     * for more information on how to do this.
     * -
     * @param id (Your Payload Id<T>)
     * @param codec (Your Payload's CODEC)
     * @param direction (Payload Direction)
     */
    default void registerPlayPayload(@Nonnull CustomPayload.Id<T> id, @Nonnull PacketCodec<? super RegistryByteBuf,T> codec, int direction)
    {
        if (this.isPlayRegistered(this.getPayloadChannel()) == false)
        {
            try
            {
                switch (direction)
                {
                    case TO_SERVER, FROM_CLIENT -> PayloadTypeRegistry.playC2S().register(id, codec);
                    case FROM_SERVER, TO_CLIENT -> PayloadTypeRegistry.playS2C().register(id, codec);
                    default ->
                    {
                        PayloadTypeRegistry.playC2S().register(id, codec);
                        PayloadTypeRegistry.playS2C().register(id, codec);
                    }
                }
            }
            catch (IllegalArgumentException e)
            {
                MaLiLib.logger.error("registerPlayPayload: channel ID [{}] is is already registered", this.getPayloadChannel());
            }

            this.setPlayRegistered(this.getPayloadChannel());
            return;
        }

        MaLiLib.logger.error("registerPlayPayload: channel ID [{}] is invalid, or it is already registered", this.getPayloadChannel());
    }

    /**
     * Register your Packet Receiver function.
     * You can use the HANDLER itself (Singleton method), or any other class that you choose.
     * See the fabric-network-api-v1 Java Docs under ClientPlayNetworking.registerGlobalReceiver()
     * for more information on how to do this.
     * -
     * @param id (Your Payload Id<T>)
     * @param receiver (Your Packet Receiver // if null, uses this::receivePlayPayload)
     * @return (True / False)
     */
    default boolean registerPlayReceiver(@Nonnull CustomPayload.Id<T> id, @Nullable ClientPlayNetworking.PlayPayloadHandler<T> receiver)
    {
        if (this.isPlayRegistered(this.getPayloadChannel()))
        {
            try
            {
                return ClientPlayNetworking.registerGlobalReceiver(id, Objects.requireNonNullElse(receiver, this::receivePlayPayload));
            }
            catch (IllegalArgumentException e)
            {
                MaLiLib.logger.error("registerPlayReceiver: Channel ID [{}] payload has not been registered", this.getPayloadChannel());
                return false;
            }
        }

        MaLiLib.logger.error("registerPlayReceiver: Channel ID [{}] is invalid, or not registered", this.getPayloadChannel());
        return false;
    }

    /**
     * Unregisters your Packet Receiver function.
     * You can use the HANDLER itself (Singleton method), or any other class that you choose.
     * See the fabric-network-api-v1 Java Docs under ClientPlayNetworking.unregisterGlobalReceiver()
     * for more information on how to do this.
     */
    default void unregisterPlayReceiver()
    {
        ClientPlayNetworking.unregisterGlobalReceiver(this.getPayloadChannel());
    }

    /**
     * Receive Payload by pointing static receive() method to this to convert Payload to its data decode() function.
     * -
     * @param payload (Payload to decode)
     * @param ctx (Fabric Context)
     */
    void receivePlayPayload(T payload, ClientPlayNetworking.Context ctx);

    /**
     * Receive Payload via the legacy "onCustomPayload" from a Network Handler Mixin interface.
     * -
     * @param payload (Payload to decode)
     * @param handler (Network Handler that received the data)
     * @param ci (Callbackinfo for sending ci.cancel(), if wanted)
     */
    default void receivePlayPayload(T payload, ClientPlayNetworkHandler handler, CallbackInfo ci) {}

    /**
     * Payload Decoder wrapper function.
     * Implements how the data is processed after being decoded from the receivePlayPayload().
     * You can ignore these and implement your own helper class/methods.
     * These are provided as an example, and can be used in your HANDLER directly.
     * -
     * @param channel (Channel)
     * @param data (Data Codec)
     */
    default void decodeNbtCompound(Identifier channel, NbtCompound data) {}
    default void decodeByteBuf(Identifier channel, MaLiLibBuf data) {}
    default <D> void decodeObject(Identifier channel, D data1) {}
    default <P extends IClientPayloadData> void decodeClientData(Identifier channel, P data) {}

    /**
     * Payload Encoder wrapper function.
     * Implements how to encode() your Payload, then forward complete Payload to sendPlayPayload().
     * -
     * @param data (Data Codec)
     */
    default void encodeNbtCompound(NbtCompound data) {}
    default void encodeByteBuf(MaLiLibBuf data) {}
    default <D> void encodeObject(D data1) {}
    default <P extends IClientPayloadData> void encodeClientData(P data) {}

    /**
     * Used as an iterative "wrapper" for Payload Splitter to send individual Packets
     * @param buf (Sliced Buffer to send)
     * @param handler (Network Handler as a fail-over option)
     */
    void encodeWithSplitter(PacketByteBuf buf, ClientPlayNetworkHandler handler);

    /**
     * Sends the Payload to the server using the Fabric-API interface.
     * -
     * @param payload (The Payload to send)
     * @return (true/false --> for error control)
     */
    default boolean sendPlayPayload(@Nonnull T payload)
    {
        if (payload.getId().id().equals(this.getPayloadChannel()) && this.isPlayRegistered(this.getPayloadChannel()))
        {
            if (ClientPlayNetworking.canSend(payload.getId()))
            {
                ClientPlayNetworking.send(payload);
                return true;
            }
        }
        else
        {
            MaLiLib.logger.warn("sendPlayPayload: [Fabric-API] error sending payload for channel: {}, check if channel is registered", payload.getId().id().toString());
        }

        return false;
    }

    /**
     * Sends the Payload to the player using the ClientPlayNetworkHandler interface.
     * @param handler (ClientPlayNetworkHandler)
     * @param payload (The Payload to send)
     * @return (true/false --> for error control)
     */
    default boolean sendPlayPayload(@Nonnull ClientPlayNetworkHandler handler, @Nonnull T payload)
    {
        if (payload.getId().id().equals(this.getPayloadChannel()) && this.isPlayRegistered(this.getPayloadChannel()))
        {
            Packet<?> packet = new CustomPayloadC2SPacket(payload);

            if (handler.accepts(packet))
            {
                handler.sendPacket(packet);
                return true;
            }
        }
        else
        {
            MaLiLib.logger.warn("sendPlayPayload: [NetworkHandler] error sending payload for channel: {}, check if channel is registered", payload.getId().id().toString());
        }

        return false;
    }
}
