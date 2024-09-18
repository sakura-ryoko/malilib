package fi.dy.masa.malilib.mixin.test;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.test.TestDataSync;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler_NbtQuery
{
    @Inject(method = "onNbtQueryResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/DataQueryHandler;handleQueryResponse(ILnet/minecraft/nbt/NbtCompound;)Z"))
    private void onQueryResponse(NbtQueryResponseS2CPacket packet, CallbackInfo ci)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            TestDataSync.getInstance().handleVanillaQueryNbt(packet.getTransactionId(), packet.getNbt() != null ? packet.getNbt() : new NbtCompound());
        }
    }
}
