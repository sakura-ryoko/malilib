package fi.dy.masa.malilib.sync.fe;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.DataResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sound.SoundEvent;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

public class FakeMerchant extends FakePassive implements InventoryOwner, Merchant, IFakeMerchant
{
    public static final int INVENTORY_SIZE = 8;
    @Nullable
    private PlayerEntity customer;
    @Nullable
    protected TradeOfferList offers;
    private SimpleInventory inventory;
    private int exp;

    public FakeMerchant(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.inventory = new SimpleInventory(INVENTORY_SIZE);
    }

    public FakeMerchant(Entity input)
    {
        super(input);

        if (input instanceof MerchantEntity)
        {
            this.inventory = new SimpleInventory(INVENTORY_SIZE);
            this.readCustomDataFromNbt(this.getNbt());
        }
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

    @Override
    public void setOffersFromServer(TradeOfferList offers)
    {
        this.offers = offers;
    }

    @Override
    public void trade(TradeOffer offer)
    {
        // NO-OP
    }

    @Override
    public void onSellingItem(ItemStack stack)
    {
        // NO-OP
    }

    @Override
    public int getExperience()
    {
        return this.exp;
    }

    @Override
    public void setExperienceFromServer(int experience)
    {
        this.exp = experience;
    }

    public void afterUsing(TradeOffer offer)
    {
        // NO-OP
    }

    public void fillRecipes()
    {
        // NO-OP
    }

    public boolean isLeveledMerchant()
    {
        return true;
    }

    @Override
    public SoundEvent getYesSound()
    {
        return null;
    }

    @Override
    public boolean isClient()
    {
        return false;
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
        if (!this.getWorld().isClient)
        {
            TradeOfferList tradeOfferList = this.getOffers();

            if (!tradeOfferList.isEmpty())
            {
                nbt.put("Offers", TradeOfferList.CODEC.encodeStart(this.getRegistryManager().getOps(NbtOps.INSTANCE), tradeOfferList).getOrThrow());
            }
        }

        this.writeInventory(nbt, this.getRegistryManager());
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("Offers"))
        {
            DataResult<TradeOfferList> dr = TradeOfferList.CODEC.parse(this.getRegistryManager().getOps(NbtOps.INSTANCE), nbt.get("Offers"));
            dr.resultOrPartial().ifPresent((offers) -> this.offers = offers);
        }

        if (this.inventory == null)
        {
            this.inventory = new SimpleInventory(INVENTORY_SIZE);
        }
        this.readInventory(nbt, this.getRegistryManager());
    }
}
