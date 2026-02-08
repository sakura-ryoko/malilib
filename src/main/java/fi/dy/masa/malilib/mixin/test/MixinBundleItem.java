package fi.dy.masa.malilib.mixin.test;

import java.util.Optional;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.GuiBase;

@Mixin(value = BundleItem.class, priority = 1001)
public class MixinBundleItem
{
    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void malilib_getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir)
    {
        if (!MaLiLibReference.DEBUG_MODE) return;

        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
        {
            cir.setReturnValue(Optional.empty());
        }
    }
}
