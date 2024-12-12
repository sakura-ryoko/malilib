package fi.dy.masa.malilib.mixin.test;

import java.util.Optional;

import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.GuiBase;

@Mixin(BundleItem.class)
public class MixinBundleItem
{
    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void malilib_getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue() && GuiBase.isShiftDown())
        {
            cir.setReturnValue(Optional.empty());
        }
    }
}
