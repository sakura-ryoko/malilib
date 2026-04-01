package fi.dy.masa.malilib.mixin.screen;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.render.GuiContext;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;

@Mixin(value = AbstractContainerScreen.class, priority = 900)
public abstract class MixinAbstractContainerScreen
{
    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(method = "extractTooltip", at = @At(value = "TAIL"))
    private void malilib_onRenderMouseoverTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci)
    {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem())
        {
            if (!((AbstractContainerScreen<?>) (Object) this instanceof InventoryScreen))
            {
                ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(GuiContext.fromGuiGraphics(graphics), this.hoveredSlot.getItem(), mouseX, mouseY);
            }
        }
    }
}
