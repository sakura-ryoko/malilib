package fi.dy.masa.malilib.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen
{
    @Shadow @Nullable protected Slot focusedSlot;

    @Inject(method = "drawMouseoverTooltip", at = @At(value = "TAIL"))
    private void malilib_onRenderMouseoverTooltip(DrawContext drawContext, int x, int y, CallbackInfo ci)
    {
        if (this.focusedSlot != null && this.focusedSlot.hasStack())
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(drawContext, this.focusedSlot.getStack(), x, y);
        }
    }
}
