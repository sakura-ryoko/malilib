package fi.dy.masa.malilib.mixin.network;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;

@Mixin(ClientConfigurationNetworkHandler.class)
public class MixinClientConfigurationNetworkHandler
{
    @Inject(method = "onReady", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/ClientConnection;transitionInbound(Lnet/minecraft/network/state/NetworkState;Lnet/minecraft/network/listener/PacketListener;)V",
            shift = At.Shift.BEFORE)
    )
    private void malilib_onPlayLogin(ReadyS2CPacket packet, CallbackInfo ci, @Local DynamicRegistryManager.Immutable immutable)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadImmutable(immutable);
    }
}
