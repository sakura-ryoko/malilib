package fi.dy.masa.malilib.mixin.gui;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.render.GuiContext;

@Mixin(value = Gui.class, priority = 900)
public abstract class MixinGui
{
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void malilib_onGameOverlayPost(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci,
                                           @Local(name = "graphics") GuiGraphicsExtractor graphics)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).runExtractGuiOverlayPost(GuiContext.fromGuiGraphics(graphics), deltaTracker.getGameTimeDeltaPartialTick(false));
    }
}
