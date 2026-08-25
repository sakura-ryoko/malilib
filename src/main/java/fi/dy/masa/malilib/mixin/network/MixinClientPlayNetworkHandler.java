package fi.dy.masa.malilib.mixin.network;

import javax.annotation.Nullable;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.time.TickUtils;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Shadow private ClientWorld world;
    @Unique @Nullable private ClientWorld worldBefore;

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void malilib_onPreJoinGameHead(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
        //MaLiLib.logger.error("CP#onPreJoinGameHead(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        this.worldBefore = this.world;
    }

    @Inject(method = "onGameJoin",
			at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;joinWorld(Lnet/minecraft/client/world/ClientWorld;)V",
            shift = At.Shift.BEFORE))
    private void malilib_onPreGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        //MaLiLib.logger.error("CP#onPreGameJoin(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, this.world, MinecraftClient.getInstance());
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void malilib_onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        //MaLiLib.logger.error("CP#onPostGameJoin(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, this.world, MinecraftClient.getInstance());
        this.worldBefore = null;
    }

    @Inject(method = "onWorldTimeUpdate", at = @At("RETURN"))
    private void malilib_onTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci)
    {
        TickUtils.getInstance().updateNanoTick(packet.time());
    }

    @Inject(method = "onUpdateTickRate", at = @At("RETURN"))
    private void malilib_onUpdateTickRate(UpdateTickRateS2CPacket packet, CallbackInfo ci)
    {
        TickUtils.getInstance().updateTickRate(packet.tickRate());
    }

    @WrapOperation(method = "onOpenScreen",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreens;open(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/client/MinecraftClient;ILnet/minecraft/text/Text;)V"
                   )
    )
    private <T extends ScreenHandler> void malilib_onOpenScreenPacket(ScreenHandlerType<T> type, MinecraftClient minecraft, int containerId, Text title, Operation<Void> original)
    {
        original.call(type, minecraft, containerId, title);

        if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
        {
            minecraft.execute(() ->
                              {
                                  BlockPos pos = Registry.ENTITY_DATA_REGISTRY.chestTracker().getLastInteractPos();
                                  ClientWorld level = minecraft.world;
                                  if (pos == null || level == null) { return; }
                                  BlockState state =level.getBlockState(pos);

                                  if (state.hasBlockEntity())
                                  {
                                      if (!minecraft.isIntegratedServerRunning() || state.getBlock() == Blocks.ENDER_CHEST)
                                      {
                                          BlockEntity te = level.getBlockEntity(pos);
                                          InventoryUtils.getInventory(level, pos);
                                          Registry.ENTITY_DATA_REGISTRY.chestTracker().onContainerMenuOpened(containerId, pos, te);
                                      }
                                  }
                              });
        }
    }
}