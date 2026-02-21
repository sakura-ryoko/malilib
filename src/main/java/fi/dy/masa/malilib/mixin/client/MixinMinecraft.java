package fi.dy.masa.malilib.mixin.client;

import java.nio.file.Path;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    @Shadow public ClientLevel level;
    @Unique private ClientLevel worldBefore;

    @Inject(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/LevelStorageSource;parseValidator(Ljava/nio/file/Path;)Lnet/minecraft/world/level/validation/DirectoryValidator;"))
    private void malilib_onPreGameInit(GameConfig args, CallbackInfo ci,
                                       @Local Path runDir)
    {
        // Register all mod handlers
        ((InitializationHandler) InitializationHandler.getInstance()).onPreGameInit(runDir);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/main/GameConfig;)V", at = @At("RETURN"))
    private void malilib_onInitComplete(GameConfig args, CallbackInfo ci)
    {
        // Register all mod handlers
        ((InitializationHandler) InitializationHandler.getInstance()).onGameInitDone();
    }

    @Inject(method = "doWorldLoad",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/progress/LevelLoadListener;compose(Lnet/minecraft/server/level/progress/LevelLoadListener;Lnet/minecraft/server/level/progress/LevelLoadListener;)Lnet/minecraft/server/level/progress/LevelLoadListener;",
            shift = At.Shift.BEFORE))
    private void malilib_onStartIntegratedServer(LevelStorageSource.LevelStorageAccess session, PackRepository dataPackManager, WorldStem saveLoader, boolean newWorld, CallbackInfo ci)
    {
        MaLiLib.debugLog("malilib_onStartIntegratedServer(): Get DynamicRegistry from IntegratedServer");
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadImmutable(saveLoader.registries().compositeAccess());
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void malilib_onPostKeyboardInput(CallbackInfo ci)
    {
        KeybindMulti.reCheckPressedKeys();
        TickHandler.getInstance().onClientTick((Minecraft)(Object) this);
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void malilib_onLoadWorldPre(ClientLevel worldClientIn, CallbackInfo ci)
    {
        // Only handle dimension changes/respawns here.
        // The initial join is handled in MixinClientPlayNetworkHandler onGameJoin

//        MaLiLib.LOGGER.error("MC#onLoadWorldPre(): world [{}], worldBefore [{}], worldClientIn [{}]", this.level != null, this.worldBefore != null, worldClientIn != null);
        if (this.level != null)
        {
            this.worldBefore = this.level;
            ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.level, worldClientIn, (Minecraft)(Object) this);
        }
    }

    @Inject(method = "setLevel", at = @At("RETURN"))
    private void malilib_onLoadWorldPost(ClientLevel worldClientIn, CallbackInfo ci)
    {
//        MaLiLib.LOGGER.error("MC#onLoadWorldPost(): world [{}], worldBefore [{}], worldClientIn [{}]", this.level != null, this.worldBefore != null, worldClientIn != null);
        if (this.worldBefore != null)
        {
            ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, worldClientIn, (Minecraft)(Object) this);
            this.worldBefore = null;
        }
    }

    @Inject(method = "clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    private void malilib_onReconfigurationPre(Screen screen, CallbackInfo ci)
    {
//        MaLiLib.LOGGER.error("MC#onReconfigurationPre(): world [{}], worldBefore [{}]", this.level != null, this.worldBefore != null);
        this.worldBefore = this.level;
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, null, (Minecraft)(Object) this);
    }

    @Inject(method = "clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("RETURN"))
    private void malilib_onReconfigurationPost(Screen screen, CallbackInfo ci)
    {
//        MaLiLib.LOGGER.error("MC#onReconfigurationPost(): world [{}], worldBefore [{}]", this.level != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, null, (Minecraft)(Object) this);
        this.worldBefore = null;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At("HEAD"))
    private void malilib_onDisconnectPre(Screen screen, boolean bl, boolean bl2, CallbackInfo ci)
    {
//        MaLiLib.LOGGER.error("MC#onDisconnectPre(): world [{}], worldBefore [{}]", this.level != null, this.worldBefore != null);
        this.worldBefore = this.level;
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, null, (Minecraft)(Object) this);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;ZZ)V", at = @At("RETURN"))
    private void malilib_onDisconnectPost(Screen screen, boolean bl, boolean bl2, CallbackInfo ci)
    {
//        MaLiLib.LOGGER.error("MC#onDisconnectPost(): world [{}], worldBefore [{}]", this.level != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, null, (Minecraft)(Object) this);
        this.worldBefore = null;
    }
}
