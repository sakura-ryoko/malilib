package fi.dy.masa.malilib.network;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * The Client Network Play handler
 * @param <T> (Payload)
 */
public class ClientPlayHandler<T extends CustomPacketPayload> implements IClientPlayHandler
{
    private static final ClientPlayHandler<CustomPacketPayload> INSTANCE = new ClientPlayHandler<>();
    private final ArrayListMultimap<Identifier, IPluginClientPlayHandler<T>> handlers = ArrayListMultimap.create();
    public static IClientPlayHandler getInstance()
    {
        return INSTANCE;
    }

    private ClientPlayHandler() {}

    @Override
    @SuppressWarnings("unchecked")
    public <P extends CustomPacketPayload> void registerClientPlayHandler(IPluginClientPlayHandler<P> handler)
    {
        Identifier channel = handler.getPayloadChannel();

        if (this.handlers.containsEntry(channel, handler) == false)
        {
            this.handlers.put(channel, (IPluginClientPlayHandler<T>) handler);
        }
    }

    @Override
    public <P extends CustomPacketPayload> void unregisterClientPlayHandler(IPluginClientPlayHandler<P> handler)
    {
        Identifier channel = handler.getPayloadChannel();

        if (this.handlers.remove(channel, handler))
        {
            handler.reset(channel);
            handler.unregisterPlayReceiver();
        }
    }

    @ApiStatus.Internal
    public void reset(Identifier channel)
    {
        if (this.handlers.isEmpty() == false)
        {
            for (IPluginClientPlayHandler<T> handler : this.handlers.get(channel))
            {
                handler.reset(channel);
            }
        }
    }
}
