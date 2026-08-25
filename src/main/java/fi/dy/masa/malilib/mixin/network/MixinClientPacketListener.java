package fi.dy.masa.malilib.mixin.network;

import javax.annotation.Nullable;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener
{
    @Shadow private ClientLevel level;
    @Unique @Nullable private ClientLevel worldBefore;

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void malilib_onPreJoinGameHead(ClientboundLoginPacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
//        MaLiLib.LOGGER.error("CP#onPreJoinGameHead(): world [{}], worldBefore [{}]", this.level != null, this.worldBefore != null);
        this.worldBefore = this.level;
    }

    @Inject(method = "handleLogin",
			at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;)V",
            shift = At.Shift.BEFORE))
    private void malilib_onPreGameJoin(ClientboundLoginPacket packet, CallbackInfo ci)
    {
//        MaLiLib.LOGGER.error("CP#onPreGameJoin(): world [{}], worldBefore [{}]", this.level != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, this.level, Minecraft.getInstance());
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void malilib_onPostGameJoin(ClientboundLoginPacket packet, CallbackInfo ci)
    {
//        MaLiLib.LOGGER.error("CP#onPostGameJoin(): world [{}], worldBefore [{}]", this.level != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, this.level, Minecraft.getInstance());
        this.worldBefore = null;
    }

    @Inject(method = "handleSetTime", at = @At("RETURN"))
    private void malilib_onTimeUpdate(ClientboundSetTimePacket packet, CallbackInfo ci)
    {
        TickUtils.getInstance().updateNanoTick(packet.gameTime());
    }

    @Inject(method = "handleTickingState", at = @At("RETURN"))
    private void malilib_onUpdateTickRate(ClientboundTickingStatePacket packet, CallbackInfo ci)
    {
        TickUtils.getInstance().updateTickRate(packet.tickRate());
    }

    @WrapOperation(method = "handleOpenScreen",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/screens/MenuScreens;create(Lnet/minecraft/world/inventory/MenuType;Lnet/minecraft/client/Minecraft;ILnet/minecraft/network/chat/Component;)V"
                   )
    )
    private <T extends AbstractContainerMenu> void malilib_onOpenScreenPacket(MenuType<T> type, Minecraft minecraft, int containerId, Component title, Operation<Void> original)
    {
        original.call(type, minecraft, containerId, title);

        if (MaLiLibConfigs.Generic.ENABLE_CHEST_DATA_TRACKER.getBooleanValue())
        {
            minecraft.execute(() ->
                              {
                                  BlockPos pos = Registry.ENTITY_DATA_REGISTRY.chestTracker().getLastInteractPos();
                                  ClientLevel level = minecraft.level;
                                  if (pos == null || level == null) { return; }
                                  BlockState state =level.getBlockState(pos);

                                  if (state.hasBlockEntity())
                                  {
                                      if (!minecraft.hasSingleplayerServer() || state.getBlock() == Blocks.ENDER_CHEST)
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