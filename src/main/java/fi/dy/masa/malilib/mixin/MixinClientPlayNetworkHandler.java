package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.WorldLoadHandler;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Shadow private ClientWorld world;
    @Unique @Nullable private ClientWorld worldBefore;

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onPreJoinGameHead(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
        //MaLiLib.logger.error("CP#onPreJoinGameHead(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        this.worldBefore = this.world;
    }

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;joinWorld(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen$WorldEntryReason;)V",
            shift = At.Shift.BEFORE))
    private void onPreGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        //MaLiLib.logger.error("CP#onPreGameJoin(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, this.world, MinecraftClient.getInstance());
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        //MaLiLib.logger.error("CP#onPostGameJoin(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, this.world, MinecraftClient.getInstance());
        this.worldBefore = null;
    }
}