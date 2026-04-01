package fi.dy.masa.malilib.mixin.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.render.GuiContext;

@Mixin(value = GuiGraphicsExtractor.class, priority = 900)
public abstract class MixinGuiGraphicsExtractor
{
    @Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value = "TAIL"))
    private void malilib_onRenderTooltip(Font font, ItemStack itemStack, int xo, int yo, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(GuiContext.fromGuiGraphics((GuiGraphicsExtractor) (Object) this), itemStack, xo, yo);
    }
}
