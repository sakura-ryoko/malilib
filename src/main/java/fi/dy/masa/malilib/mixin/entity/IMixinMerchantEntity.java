package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractVillager.class)
public interface IMixinMerchantEntity
{
    @Accessor("offers")
    MerchantOffers malilib_offers();
}
