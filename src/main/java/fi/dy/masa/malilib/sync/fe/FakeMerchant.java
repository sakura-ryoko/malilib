package fi.dy.masa.malilib.sync.fe;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.DataResult;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

public abstract class FakeMerchant extends FakePassive implements InventoryOwner, Merchant
{
    @Nullable
    private PlayerEntity customer;
    @Nullable
    protected TradeOfferList offers;
    private final SimpleInventory inventory = new SimpleInventory(8);

    public FakeMerchant(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public void setCustomer(@Nullable PlayerEntity customer)
    {
        this.customer = customer;
    }

    @Nullable
    public PlayerEntity getCustomer()
    {
        return this.customer;
    }

    public boolean hasCustomer()
    {
        return this.customer != null;
    }

    protected void resetCustomer()
    {
        this.setCustomer(null);
    }

    public TradeOfferList getOffers()
    {
        if (this.offers == null)
        {
            this.offers = new TradeOfferList();
            this.fillRecipes();
        }

        return this.offers;
    }

    protected abstract void afterUsing(TradeOffer offer);

    protected abstract void fillRecipes();

    public boolean isLeveledMerchant()
    {
        return true;
    }

    public boolean canBeLeashed()
    {
        return false;
    }

    public SimpleInventory getInventory()
    {
        return this.inventory;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        if (!this.getWorld().isClient)
        {
            TradeOfferList tradeOfferList = this.getOffers();

            if (!tradeOfferList.isEmpty())
            {
                nbt.put("Offers", TradeOfferList.CODEC.encodeStart(this.getRegistryManager().getOps(NbtOps.INSTANCE), tradeOfferList).getOrThrow());
            }
        }

        this.writeInventory(nbt, this.getRegistryManager());
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("Offers"))
        {
            DataResult<TradeOfferList> dr = TradeOfferList.CODEC.parse(this.getRegistryManager().getOps(NbtOps.INSTANCE), nbt.get("Offers"));
            dr.resultOrPartial().ifPresent((offers) -> this.offers = offers);
        }

        this.readInventory(nbt, this.getRegistryManager());
    }
}
