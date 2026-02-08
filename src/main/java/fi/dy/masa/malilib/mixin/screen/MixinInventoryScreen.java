package fi.dy.masa.malilib.mixin.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.render.GuiContext;

@Mixin(value = InventoryScreen.class, priority = 990)
public abstract class MixinInventoryScreen
{
	// Fix the Status Effects from overtaking the Tooltip rendering (Shulker Box Preview, etc.)
    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onPostInventoryStatusEffects(GuiGraphics context, int x, int y, float delta, CallbackInfo ci)
    {
        Slot focused = ((IMixinHandledScreen) this).malilib_getFocusedSlot();

        if (focused != null && focused.hasItem())
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(GuiContext.fromGuiGraphics(context), focused.getItem(), x, y);
        }
    }
}
