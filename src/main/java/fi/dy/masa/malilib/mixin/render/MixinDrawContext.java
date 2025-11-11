package fi.dy.masa.malilib.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext
{
    @Inject(method = "drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "TAIL"))
    private void malilib_onRenderTooltip(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast((DrawContext) (Object) this, stack, x, y);
    }
}
