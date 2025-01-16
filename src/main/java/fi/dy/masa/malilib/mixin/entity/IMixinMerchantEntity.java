package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantEntity.class)
public interface IMixinMerchantEntity
{
    @Accessor("offers")
    TradeOfferList malilib_offers();
}
