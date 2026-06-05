package fi.dy.masa.malilib.mixin.server;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.event.ServerHandler;

@Mixin(value = IntegratedServer.class, priority = 999)
public class MixinIntegratedServer
{
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "initServer", at = @At("RETURN"))
    private void malilib_setupServer(CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            ((ServerHandler) ServerHandler.getInstance()).onServerIntegratedSetup(this.minecraft.getSingleplayerServer());
        }
    }

    @Inject(method = "publishServer(Lnet/minecraft/server/MinecraftServer$MultiplayerScope;I)Z", at = @At("RETURN"))
    private void malilib_checkOpenToLan(MinecraftServer.MultiplayerScope scope, int port, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            ((ServerHandler) ServerHandler.getInstance()).onServerOpenToLan(this.minecraft.getSingleplayerServer());
        }
    }
}
