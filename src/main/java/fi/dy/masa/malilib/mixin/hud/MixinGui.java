package fi.dy.masa.malilib.mixin.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.render.GuiContext;
import fi.dy.masa.malilib.util.game.IGameHud;

@Mixin(value = Gui.class, priority = 990)
public abstract class MixinGui implements IGameHud
{
    @Shadow private int overlayMessageTime;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void malilib_onGameOverlayPost(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderGameOverlayPost(GuiContext.fromGuiGraphics(graphics), deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Override
    public void malilib$setOverlayRemaining(int ticks)
    {
        this.overlayMessageTime = ticks;
    }
}
