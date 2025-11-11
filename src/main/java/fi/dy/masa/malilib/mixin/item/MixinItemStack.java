package fi.dy.masa.malilib.mixin.item;

import fi.dy.masa.malilib.event.RenderEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    // This Goes before the Item Additional Tooltips.
    @Inject(method = "appendTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"))
    private void onGetTooltipComponentsFirst(Item.TooltipContext context, TooltipDisplayComponent displayComponent,
                                             PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertFirst(context, (ItemStack) (Object) this, textConsumer);
    }

    // This Goes after the Item Additional Tooltips.
    @Inject(method = "appendTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                     shift = At.Shift.AFTER))
    private void onGetTooltipComponentsMiddle(Item.TooltipContext context, TooltipDisplayComponent displayComponent,
                                              PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertMiddle(context, (ItemStack) (Object) this, textConsumer);
    }

    // This Goes before the Item durability, item id, and component count.
    @Inject(method = "appendTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/item/ItemStack;appendComponentTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                     ordinal = 21,
                     shift = At.Shift.AFTER))
    private void onGetTooltipComponentsLast(Item.TooltipContext context, TooltipDisplayComponent displayComponent,
                                            PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertLast(context, (ItemStack) (Object) this, textConsumer);
    }
}
