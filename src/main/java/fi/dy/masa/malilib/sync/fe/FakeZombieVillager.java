package fi.dy.masa.malilib.sync.fe;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;

public class FakeZombieVillager extends FakeZombie implements VillagerDataContainer
{
    private VillagerData data;
    private boolean converting;
    private int conversionTimer;
    @Nullable
    private UUID converter;
    @Nullable
    private NbtElement gossipData;
    @Nullable
    private TradeOfferList offerData;
    private int xp;

    public FakeZombieVillager(EntityType<? extends MobEntity> entityType, World world, int entityId)
    {
        super(entityType, world, entityId);
        this.setRandomProfession();
    }

    public FakeZombieVillager(Entity input)
    {
        super(input);

        if (input instanceof ZombieVillagerEntity zve)
        {
            this.setVillagerData(zve.getVillagerData());
            this.readCustomDataFromNbt(this.getNbt());
        }
        else
        {
            this.setRandomProfession();
        }
    }

    public VillagerData getVillagerData()
    {
        return this.data;
    }

    public void setVillagerData(VillagerData data)
    {
        this.data = data;
    }

    public void setRandomProfession()
    {
        Registries.VILLAGER_PROFESSION.getRandom(Random.create()).ifPresent((profession) -> this.setVillagerData(this.getVillagerData().withProfession(profession.value())));
    }

    protected boolean canConvertInWater()
    {
        return false;
    }

    public boolean canImmediatelyDespawn(double distanceSquared)
    {
        return !this.isConverting() && this.xp == 0;
    }

    public boolean isConverting()
    {
        return this.converting;
    }

    protected void setConverting(@Nullable UUID uuid, int delay)
    {
        this.converter = uuid;
        this.conversionTimer = delay;
        this.converting = true;
        this.removeStatusEffect(StatusEffects.WEAKNESS);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getWorld().getDifficulty().getId() - 1, 0)));
    }

    public int getConversionTimer()
    {
        return this.conversionTimer;
    }

    public void setConversionTimer(int conversionTimer)
    {
        this.conversionTimer = conversionTimer;
    }

    protected ItemStack getSkull()
    {
        return ItemStack.EMPTY;
    }

    public @Nullable TradeOfferList getOfferData()
    {
        return this.offerData;
    }

    public @Nullable NbtElement getGossipData()
    {
        return this.gossipData;
    }

    public void setOfferData(@Nullable TradeOfferList offerData)
    {
        this.offerData = offerData;
    }

    public void setGossipData(@Nullable NbtElement gossipData)
    {
        this.gossipData = gossipData;
    }

    public int getXp()
    {
        return this.xp;
    }

    public void setXp(int xp)
    {
        this.xp = xp;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        DataResult<NbtElement> dr = VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData());
        dr.resultOrPartial().ifPresent((villagerData) -> nbt.put("VillagerData", villagerData));
        if (this.offerData != null)
        {
            nbt.put("Offers", TradeOfferList.CODEC.encodeStart(this.getRegistryManager().getOps(NbtOps.INSTANCE), this.offerData).getOrThrow());
        }

        if (this.gossipData != null)
        {
            nbt.put("Gossips", this.gossipData);
        }

        nbt.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
        if (this.converter != null)
        {
            nbt.putUuid("ConversionPlayer", this.converter);
        }

        nbt.putInt("Xp", this.xp);
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("VillagerData", 10))
        {
            DataResult<VillagerData> dr = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("VillagerData")));
            dr.resultOrPartial().ifPresent(this::setVillagerData);
        }

        if (nbt.contains("Offers"))
        {
            DataResult<TradeOfferList> dr = TradeOfferList.CODEC.parse(this.getRegistryManager().getOps(NbtOps.INSTANCE), nbt.get("Offers"));
            dr.resultOrPartial().ifPresent((offerData) -> this.offerData = offerData);
        }

        if (nbt.contains("Gossips", 9))
        {
            this.gossipData = nbt.getList("Gossips", 10);
        }

        if (nbt.contains("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1)
        {
            this.setConverting(nbt.containsUuid("ConversionPlayer") ? nbt.getUuid("ConversionPlayer") : null, nbt.getInt("ConversionTime"));
        }

        if (nbt.contains("Xp", 3))
        {
            this.xp = nbt.getInt("Xp");
        }
    }
}
