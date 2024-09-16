package fi.dy.masa.malilib.sync.fe;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class FakeLlama extends FakeHorse implements VariantHolder<LlamaEntity.Variant>, RangedAttackMob
{
    private static final int MAX_STRENGTH = 5;
    private int strength;
    private int variant;
    private int despawnDelay = 47999;

    public FakeLlama(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public LlamaEntity.Variant getVariant()
    {
        return LlamaEntity.Variant.byId(this.variant);
    }

    public void setVariant(LlamaEntity.Variant variant)
    {
        this.variant = variant.getIndex();
    }

    private void initializeStrength(Random random)
    {
        int i = random.nextFloat() < 0.04F ? MAX_STRENGTH : 3;
        this.setStrength(1 + random.nextInt(i));
    }

    public int getStrength()
    {
        return this.strength;
    }

    private void setStrength(int strength)
    {
        this.strength = Math.max(1, Math.min(MAX_STRENGTH, strength));
    }

    public boolean isTrader()
    {
        return false;
    }

    public int getInventoryColumns()
    {
        return this.hasChest() ? this.getStrength() : 0;
    }

    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.isIn(ItemTags.LLAMA_FOOD);
    }

    public boolean isImmobile()
    {
        return this.isDead() || this.isEatingGrass();
    }

    public boolean canUseSlot(EquipmentSlot slot)
    {
        return true;
    }

    public boolean canBeSaddled()
    {
        return false;
    }

    public int getMaxTemper()
    {
        return 30;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getIndex());
        nbt.putInt("Strength", this.getStrength());
        nbt.putInt("DespawnDelay", this.despawnDelay);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        this.setStrength(nbt.getInt("Strength"));
        super.readCustomDataFromNbt(nbt);
        this.setVariant(LlamaEntity.Variant.byId(nbt.getInt("Variant")));
        if (nbt.contains("DespawnDelay", 99))
        {
            this.despawnDelay = nbt.getInt("DespawnDelay");
        }
    }

    public static class FakeLlamaData extends PassiveEntity.PassiveData
    {
        public final LlamaEntity.Variant variant;

        public FakeLlamaData(LlamaEntity.Variant variant)
        {
            super(true);
            this.variant = variant;
        }
    }
}
