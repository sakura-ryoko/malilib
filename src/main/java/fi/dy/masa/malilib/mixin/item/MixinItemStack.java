package fi.dy.masa.malilib.mixin.item;

import fi.dy.masa.malilib.event.RenderEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    // This Goes before the Item Additional Tooltips.
    @Inject(method = "addDetailsToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"))
    private void onGetTooltipComponentsFirst(Item.TooltipContext context, TooltipDisplay displayComponent,
                                             Player player, TooltipFlag type, Consumer<Component> textConsumer, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertFirst(context, (ItemStack) (Object) this, textConsumer);
    }

    // This Goes after the Item Additional Tooltips.
    @Inject(method = "addDetailsToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V",
                     shift = At.Shift.AFTER))
    private void onGetTooltipComponentsMiddle(Item.TooltipContext context, TooltipDisplay displayComponent,
                                              Player player, TooltipFlag type, Consumer<Component> textConsumer, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertMiddle(context, (ItemStack) (Object) this, textConsumer);
    }

    // This Goes before the Item durability, item id, and component count.
    @Inject(method = "addDetailsToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V",
                     ordinal = 23,
                     shift = At.Shift.AFTER))
    private void onGetTooltipComponentsLast(Item.TooltipContext context, TooltipDisplay displayComponent,
                                            Player player, TooltipFlag type, Consumer<Component> textConsumer, CallbackInfo ci)
    {
        ((RenderEventHandler) RenderEventHandler.getInstance()).onRenderTooltipComponentInsertLast(context, (ItemStack) (Object) this, textConsumer);
    }
}
