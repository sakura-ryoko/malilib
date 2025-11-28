package fi.dy.masa.malilib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IClientPlayHandler
{
    <P extends CustomPacketPayload> void registerClientPlayHandler(IPluginClientPlayHandler<P> handler);
    <P extends CustomPacketPayload> void unregisterClientPlayHandler(IPluginClientPlayHandler<P> handler);
}
