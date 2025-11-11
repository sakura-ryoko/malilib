package fi.dy.masa.malilib.mixin.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.slot.Slot;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen
{
    // Fix the Status Effects from overtaking the Tooltip rendering (Shulker Box Preview, etc.)
    @Inject(method = "render", at = @At("TAIL"))
    private void malilib_onPostInventoryStatusEffects(DrawContext context, int x, int y, float delta, CallbackInfo ci)
    {
        Slot focused = ((IMixinHandledScreen) this).malilib_getFocusedSlot();

        if (focused != null && focused.hasStack())
        {
            ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipLast(context, focused.getStack(), x, y);
        }
    }
}
