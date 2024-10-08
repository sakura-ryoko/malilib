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

    /*
    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onHandledScreenRenderLast(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if (this.focusedSlot != null && this.focusedSlot.hasStack())
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(context, this.focusedSlot.getStack(), mouseX, mouseY);
        }
    }
     */

    @Inject(method = "drawMouseoverTooltip",
            at = @At(value = "RETURN"))
            //target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/util/Identifier;)V"))
    private void malilib_onRenderMouseoverTooltip(DrawContext drawContext, int x, int y, CallbackInfo ci)
    {
        if (this.focusedSlot != null && this.focusedSlot.hasStack())
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(drawContext, this.focusedSlot.getStack(), x, y);
            drawContext.draw();
        }
    }
}
