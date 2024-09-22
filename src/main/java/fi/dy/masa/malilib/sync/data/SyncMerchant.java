package fi.dy.masa.malilib.sync.data;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.DataResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.VillagerEntity;
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

import fi.dy.masa.malilib.util.IEntityOwnedInventory;

public class SyncMerchant extends SyncEquipment implements Merchant, InventoryOwner
{
    private static final String OFFERS_KEY = "Offers";
    private static final int MAX_INVENTORY = 8;
    protected @Nullable TradeOfferList offers;
    private SimpleInventory inventory;

    public SyncMerchant(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.initInventory();
    }

    public SyncMerchant(Entity entity)
    {
        super(entity);
        this.initInventory();
        this.initAttributes(entity);
        this.copyNbtFromEntity(entity);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    protected void initInventory()
    {
        super.initInventory();
        this.inventory = new SimpleInventory(MAX_INVENTORY);
        ((IEntityOwnedInventory) inventory).malilib$setSyncOwner(this);
    }

    protected void initAttributes(Entity entity)
    {
        super.initAttributes(entity);
        if (entity instanceof AllayEntity)
        {
            this.buildAttributes(createAllayAttributes());
        }
        else if (entity instanceof VillagerEntity)
        {
            this.buildAttributes(createVillagerAttributes());
        }
        else if (entity instanceof PiglinEntity)
        {
            this.buildAttributes(createPiglinAttributes());
        }
    }

    public SimpleInventory getInventory()
    {
        return this.inventory;
    }

    public void setInventory(SimpleInventory inv)
    {
        this.inventory = inv;
    }


    public void setCustomer(@Nullable PlayerEntity customer)
    {
        // NO-OP
    }

    public @Nullable PlayerEntity getCustomer()
    {
        return null;
    }

    public TradeOfferList getOffers()
    {
        if (this.offers == null)
        {
            this.offers = new TradeOfferList();
        }

        return this.offers;
    }

    public void setOffersFromServer(TradeOfferList offers)
    {
        this.offers = offers;
    }

    public void trade(TradeOffer offer)
    {
        // NO-OP
    }

    public void onSellingItem(ItemStack stack)
    {
        // NO-OP
    }

    public int getExperience()
    {
        return 0;
    }

    public void setExperienceFromServer(int experience)
    {
        // NO-OP
    }

    public boolean isLeveledMerchant()
    {
        return true;
    }

    public SoundEvent getYesSound()
    {
        return null;
    }

    public boolean isClient()
    {
        return false;
    }

    public static DefaultAttributeContainer.Builder createAllayAttributes()
    {
        return createMobAttributes().add(EntityAttributes.MAX_HEALTH, 20.0).add(EntityAttributes.FLYING_SPEED, 0.10000000149011612).add(EntityAttributes.MOVEMENT_SPEED, 0.10000000149011612).add(EntityAttributes.ATTACK_DAMAGE, 2.0);
    }

    public static DefaultAttributeContainer.Builder createVillagerAttributes()
    {
        return createMobAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.5);
    }

    public static DefaultAttributeContainer.Builder createPiglinAttributes()
    {
        return createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 16.0).add(EntityAttributes.MOVEMENT_SPEED, 0.3499999940395355).add(EntityAttributes.ATTACK_DAMAGE, 5.0);
    }

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains(InventoryOwner.INVENTORY_KEY))
        {
            if (this.inventory == null)
            {
                this.inventory = new SimpleInventory(MAX_INVENTORY);
            }

            this.readInventory(nbt, this.getRegistryManager());
        }

        if (nbt.contains(OFFERS_KEY))
        {
            DataResult<TradeOfferList> dr = TradeOfferList.CODEC.parse(this.getRegistryManager().getOps(NbtOps.INSTANCE), nbt.get(OFFERS_KEY));
            dr.resultOrPartial().ifPresent((offers) -> this.offers = offers);
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        if (!this.getWorld().isClient)
        {
            TradeOfferList tradeOfferList = this.getOffers();

            if (!tradeOfferList.isEmpty())
            {
                nbt.put(OFFERS_KEY, TradeOfferList.CODEC.encodeStart(this.getRegistryManager().getOps(NbtOps.INSTANCE), tradeOfferList).getOrThrow());
            }
        }

        this.writeInventory(nbt, this.getRegistryManager());
    }
}
