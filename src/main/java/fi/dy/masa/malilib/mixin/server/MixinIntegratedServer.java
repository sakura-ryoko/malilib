package fi.dy.masa.malilib.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fi.dy.masa.malilib.event.ServerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.GameType;

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

    @Inject(method = "publishServer", at = @At("RETURN"))
    private void malilib_checkOpenToLan(GameType gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValue())
        {
            ((ServerHandler) ServerHandler.getInstance()).onServerOpenToLan(this.minecraft.getSingleplayerServer());
        }
    }
}
