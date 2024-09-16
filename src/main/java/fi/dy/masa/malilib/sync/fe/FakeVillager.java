package fi.dy.masa.malilib.sync.fe;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.village.*;
import net.minecraft.world.World;

public abstract class FakeVillager extends FakeMerchant implements VillagerDataContainer
{
    private VillagerData data;
    private final VillagerGossips gossip;
    private long lastGossipDecayTime;
    private int experience;
    private int foodLevel;
    private long lastRestockTime;
    private int restocksToday;
    private boolean natural;

    public FakeVillager(EntityType<?> type, World world, int entityId)
    {
        this(type, world, entityId, VillagerType.PLAINS);
    }

    public FakeVillager(EntityType<?> type, World world, int entityId, VillagerType villagerType)
    {
        super(type, world, entityId);
        this.gossip = new VillagerGossips();
        this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.NONE));
    }

    public VillagerData getVillagerData()
    {
        return this.data;
    }

    public void setVillagerData(VillagerData data)
    {
        this.data = data;
    }

    public boolean isNatural()
    {
        return this.natural;
    }

    public void setCustomer(@Nullable PlayerEntity customer)
    {
        boolean bl = this.getCustomer() != null && customer == null;
        super.setCustomer(customer);

        if (bl)
        {
            this.resetCustomer();
        }
    }

    protected void resetCustomer()
    {
        super.resetCustomer();
        this.clearSpecialPrices();
    }

    private void clearSpecialPrices()
    {
        if (!this.getWorld().isClient())
        {
            for (TradeOffer tradeOffer : this.getOffers())
            {
                tradeOffer.clearSpecialPrice();
            }
        }
    }

    public boolean canRefreshTrades()
    {
        return true;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        DataResult<NbtElement> dr = VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData());
        dr.resultOrPartial().ifPresent((nbtElement) -> nbt.put("VillagerData", nbtElement));
        nbt.putByte("FoodLevel", (byte) this.foodLevel);
        nbt.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE));
        nbt.putInt("Xp", this.experience);
        nbt.putLong("LastRestock", this.lastRestockTime);
        nbt.putLong("LastGossipDecay", this.lastGossipDecayTime);
        nbt.putInt("RestocksToday", this.restocksToday);
        if (this.natural)
        {
            nbt.putBoolean("AssignProfessionWhenSpawned", true);
        }

    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("VillagerData", 10))
        {
            DataResult<VillagerData> dr = VillagerData.CODEC.parse(NbtOps.INSTANCE, nbt.get("VillagerData"));
            dr.resultOrPartial().ifPresent(this::setVillagerData);
        }

        if (nbt.contains("FoodLevel", 1))
        {
            this.foodLevel = nbt.getByte("FoodLevel");
        }

        NbtList nbtList = nbt.getList("Gossips", 10);
        this.gossip.deserialize(new Dynamic<>(NbtOps.INSTANCE, nbtList));
        if (nbt.contains("Xp", 3))
        {
            this.experience = nbt.getInt("Xp");
        }

        this.lastRestockTime = nbt.getLong("LastRestock");
        this.lastGossipDecayTime = nbt.getLong("LastGossipDecay");
        this.restocksToday = nbt.getInt("RestocksToday");
        if (nbt.contains("AssignProfessionWhenSpawned"))
        {
            this.natural = nbt.getBoolean("AssignProfessionWhenSpawned");
        }
    }
}
