package fi.dy.masa.malilib.sync.fe;

import net.minecraft.village.TradeOffer;

public interface IFakeMerchant
{
    void afterUsing(TradeOffer offer);

    void fillRecipes();
}
