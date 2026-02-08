package fi.dy.masa.malilib.mixin.hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.game.IGameHud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(value = Gui.class, priority = 990)
public abstract class MixinGui implements IGameHud
{
    @Shadow private int overlayMessageTime;

    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onGameOverlayPost(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(GuiContext.fromGuiGraphics(context), tickCounter.getGameTimeDeltaPartialTick(false));
    }

    @Override
    public void malilib$setOverlayRemaining(int ticks)
    {
        this.overlayMessageTime = ticks;
    }
}
