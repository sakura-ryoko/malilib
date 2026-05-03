package fi.dy.masa.malilib.mixin.gui;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.game.IGameHud;

@Mixin(value = Gui.class, priority = 900)
public abstract class MixinGui implements IGameHud
{
    @Shadow @Final public Hud hud;

    /*
     * 26.2 changed Gui.extractRenderState from taking GuiGraphicsExtractor as a parameter
     * to constructing it locally. Inject before applyCursor(...) and capture that local.
     */
    @Inject(method = "extractRenderState",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;applyCursor(Lcom/mojang/blaze3d/platform/Window;)V",
                     shift = At.Shift.BEFORE))
    private void malilib_onGameOverlayPost(DeltaTracker deltaTracker, boolean renderHud, boolean renderScreen, CallbackInfo ci,
                                           @Local(ordinal = 0) GuiGraphicsExtractor graphics)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runExtractGuiOverlayPost(GuiContext.fromGuiGraphics(graphics), deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Override
    public void malilib$setOverlayRemaining(int ticks)
    {
        ((IMixinHud) this.hud).malilib_setOverlayMessageTime(ticks);
    }
}
