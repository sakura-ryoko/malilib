package fi.dy.masa.malilib.mixin.network;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;

@Mixin(value = ClientConfigurationPacketListenerImpl.class, priority = 990)
public class MixinClientConfigurationPacketListenerImpl
{
    @Inject(method = "handleConfigurationFinished",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/network/Connection;setupInboundProtocol(Lnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/PacketListener;)V"
            )
    )
    private void malilib_onPlayLogin(ClientboundFinishConfigurationPacket packet, CallbackInfo ci, @Local RegistryAccess.Frozen immutable)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadImmutable(immutable);
    }
}
