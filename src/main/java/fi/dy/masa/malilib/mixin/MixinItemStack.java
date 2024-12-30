package fi.dy.masa.malilib.mixin;

import java.util.List;

import javax.annotation.Nullable;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fi.dy.masa.malilib.event.RenderEventHandler;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    // This Goes before the Item Additional Tooltips.
    @Inject(method = "getTooltip",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/item/tooltip/TooltipType;)V",
                     shift = At.Shift.BEFORE))
    private void onGetTooltipComponentsFirst(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir,
                                             @Local List<Text> list)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertFirst(context, (ItemStack) (Object) this, list);
    }

    // This Goes after the Item Additional Tooltips.
    @Inject(method = "getTooltip",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                     ordinal = 0,
                    shift = At.Shift.BEFORE))
    private void onGetTooltipComponentsMiddle(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir,
                                               @Local List<Text> list)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertMiddle(context, (ItemStack) (Object) this, list);
    }

    // This Goes before the Item durability, item id, and component count.
    @Inject(method = "getTooltip",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                     ordinal = 6,
                     shift = At.Shift.AFTER))
    private void onGetTooltipComponentsLast(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir,
                                            @Local List<Text> list)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertLast(context, (ItemStack) (Object) this, list);
    }
}
