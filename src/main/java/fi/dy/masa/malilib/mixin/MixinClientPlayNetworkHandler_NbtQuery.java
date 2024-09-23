package fi.dy.masa.malilib.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler_NbtQuery
{
    /*
    @Inject(method = "onNbtQueryResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/DataQueryHandler;handleQueryResponse(ILnet/minecraft/nbt/NbtCompound;)Z"))
    private void onQueryResponse(NbtQueryResponseS2CPacket packet, CallbackInfo ci)
    {
        if (MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
        {
            SyncHandler.getInstance().onQueryResponse(packet.getTransactionId(), packet.getNbt() != null ? packet.getNbt() : new NbtCompound());
        }
    }
     */
}
