package fi.dy.masa.malilib.mixin.render;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext
{
    @Shadow public abstract void draw();
    @Unique private float[] color;

    @Inject(method = "drawItemTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V", at = @At(value = "TAIL"))
    private void malilib_onRenderTooltip(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast((DrawContext) (Object) this, stack, x, y);
    }

    // Fix Text Color Tooltips from inheriting the Shulker Box color.
    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;getPosition(IIIIII)Lorg/joml/Vector2ic;"))
    private void malilib_fixTooltipTextColorPre(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, Identifier texture, CallbackInfo ci)
    {
        this.color = RenderSystem.getShaderColor();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void malilib_fixTooltipTextColorPost(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, Identifier texture, CallbackInfo ci)
    {
        RenderSystem.setShaderColor(this.color[0], this.color[1], this.color[2], this.color[3]);
    }
}
