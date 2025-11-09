package fi.dy.masa.malilib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.test.ConfigTestEnum;
import fi.dy.masa.malilib.test.TestSelector;

import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient
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
        //MaLiLib.printDebug("malilib_onStartIntegratedServer(): Get DynamicRegistry from IntegratedServer");
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadImmutable(saveLoader.registries().compositeAccess());
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void onPostKeyboardInput(CallbackInfo ci)
    {
        KeybindMulti.reCheckPressedKeys();
        TickHandler.getInstance().onClientTick((Minecraft)(Object) this);
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void onLoadWorldPre(ClientLevel worldClientIn, CallbackInfo ci)
    {
        // Only handle dimension changes/respawns here.
        // The initial join is handled in MixinClientPlayNetworkHandler onGameJoin

        //MaLiLib.logger.error("MC#onLoadWorldPre(): world [{}], worldBefore [{}], worldClientIn [{}]", this.world != null, this.worldBefore != null, worldClientIn != null);
        if (this.level != null)
        {
            this.worldBefore = this.level;
            ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.level, worldClientIn, (Minecraft)(Object) this);
        }
    }

    @Inject(method = "setLevel", at = @At("RETURN"))
    private void onLoadWorldPost(ClientLevel worldClientIn, CallbackInfo ci)
    {
        //MaLiLib.logger.error("MC#onLoadWorldPost(): world [{}], worldBefore [{}], worldClientIn [{}]", this.world != null, this.worldBefore != null, worldClientIn != null);
        if (this.worldBefore != null)
        {
            ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, worldClientIn, (Minecraft)(Object) this);
            this.worldBefore = null;
        }
    }

    @Inject(method = "clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    private void onReconfigurationPre(Screen screen, CallbackInfo ci)
    {
        //MaLiLib.logger.error("MC#onReconfigurationPre(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        this.worldBefore = this.level;
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, null, (Minecraft)(Object) this);
    }

    @Inject(method = "clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("RETURN"))
    private void onReconfigurationPost(Screen screen, CallbackInfo ci)
    {
        //MaLiLib.logger.error("MC#onReconfigurationPost(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, null, (Minecraft)(Object) this);
        this.worldBefore = null;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("HEAD"))
    private void onDisconnectPre(Screen screen, boolean bl, CallbackInfo ci)
    {
        //MaLiLib.logger.error("MC#onDisconnectPre(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        this.worldBefore = this.level;
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, null, (Minecraft)(Object) this);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V", at = @At("RETURN"))
    private void onDisconnectPost(Screen screen, boolean bl, CallbackInfo ci)
    {
        //MaLiLib.logger.error("MC#onDisconnectPost(): world [{}], worldBefore [{}]", this.world != null, this.worldBefore != null);
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, null, (Minecraft)(Object) this);
        this.worldBefore = null;
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void onLeftClickMouse(CallbackInfoReturnable<Boolean> cir)
    {
        if (MaLiLibReference.DEBUG_MODE &&
            MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
            ConfigTestEnum.TEST_WALLS_HOTKEY.getBooleanValue())
        {
            TestSelector.INSTANCE.select(false);
            cir.cancel();
            return;
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void onRightClickMouse(CallbackInfo ci)
    {
        if (MaLiLibReference.DEBUG_MODE &&
            MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() &&
            ConfigTestEnum.TEST_WALLS_HOTKEY.getBooleanValue())
        {
            TestSelector.INSTANCE.select(true);
            ci.cancel();
            return;
        }
    }
}
