package fi.dy.masa.malilib.sync.fe;

import java.util.*;
import java.util.function.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FakeLiving extends FakeEntity implements Attackable, IFakeLiving
{
    private final DefaultedList<ItemStack> syncedHandStacks;
    private final DefaultedList<ItemStack> syncedArmorStacks;
    private final int defaultMaxHealth = 20;
    private float health;
    private ItemStack syncedBodyArmorStack = ItemStack.EMPTY;
    protected ItemStack activeItemStack;
    private BlockPos lastBlockPos;
    protected Brain<?> brain;
    private @Nullable LivingEntity attacker;
    public static final String ATTRIBUTES_NBT_KEY = "attributes";
    private AttributeContainer attributes;
    private final Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeStatusEffects = Maps.newHashMap();
    private boolean effectsChanged;
    private float absorptionAmount;

    public FakeLiving(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.syncedHandStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.syncedArmorStacks = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.activeItemStack = ItemStack.EMPTY;
        this.attributes = new AttributeContainer(createLivingAttributes().build());
        this.health = this.getMaxHealth();
        NbtOps nbtOps = NbtOps.INSTANCE;
        this.brain = this.deserializeBrain(new Dynamic<>(nbtOps, nbtOps.createMap(ImmutableMap.of(nbtOps.createString("memories"), nbtOps.emptyMap()))));
    }

    public FakeLiving(Entity input)
    {
        super(input);
        this.syncedHandStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.syncedArmorStacks = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.activeItemStack = ItemStack.EMPTY;

        if (input instanceof LivingEntity le)
        {
            this.attributes = le.getAttributes();
            this.readCustomDataFromNbt(this.getNbt());
        }
        else
        {
            this.attributes = new AttributeContainer(createLivingAttributes().build());
        }

        this.health = this.getMaxHealth();
        NbtOps nbtOps = NbtOps.INSTANCE;
        this.brain = this.deserializeBrain(new Dynamic<>(nbtOps, nbtOps.createMap(ImmutableMap.of(nbtOps.createString("memories"), nbtOps.emptyMap()))));
    }

    public Brain<?> getBrain()
    {
        return this.brain;
    }

    protected Brain.Profile<?> createBrainProfile()
    {
        return Brain.createProfile(ImmutableList.of(), ImmutableList.of());
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic)
    {
        return this.createBrainProfile().deserialize(dynamic);
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return DefaultAttributeContainer.builder().add(EntityAttributes.MAX_HEALTH).add(EntityAttributes.KNOCKBACK_RESISTANCE).add(EntityAttributes.MOVEMENT_SPEED).add(EntityAttributes.ARMOR).add(EntityAttributes.ARMOR_TOUGHNESS).add(EntityAttributes.MAX_ABSORPTION).add(EntityAttributes.STEP_HEIGHT).add(EntityAttributes.SCALE).add(EntityAttributes.GRAVITY).add(EntityAttributes.SAFE_FALL_DISTANCE).add(EntityAttributes.FALL_DAMAGE_MULTIPLIER).add(EntityAttributes.JUMP_STRENGTH).add(EntityAttributes.OXYGEN_BONUS).add(EntityAttributes.BURNING_TIME).add(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE).add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY).add(EntityAttributes.MOVEMENT_EFFICIENCY).add(EntityAttributes.ATTACK_KNOCKBACK);
    }

    @Nullable
    public EntityAttributeInstance getAttributeInstance(RegistryEntry<EntityAttribute> attribute)
    {
        return this.getAttributes().getCustomInstance(attribute);
    }

    public double getAttributeValue(RegistryEntry<EntityAttribute> attribute)
    {
        return this.getAttributes().getValue(attribute);
    }

    public double getAttributeBaseValue(RegistryEntry<EntityAttribute> attribute)
    {
        return this.getAttributes().getBaseValue(attribute);
    }

    public AttributeContainer getAttributes()
    {
        return this.attributes;
    }

    protected void buildAttributes(DefaultAttributeContainer.Builder builder)
    {
        this.attributes = new AttributeContainer(builder.build());
    }

    public boolean clearStatusEffects()
    {
        if (this.getWorld().isClient)
        {
            return false;
        }
        else if (this.activeStatusEffects.isEmpty())
        {
            return false;
        }
        else
        {
            Map<RegistryEntry<StatusEffect>, StatusEffectInstance> map = Maps.newHashMap(this.activeStatusEffects);
            this.activeStatusEffects.clear();
            this.onStatusEffectsRemoved(map.values());
            return true;
        }
    }

    protected void onStatusEffectsRemoved(Collection<StatusEffectInstance> effects)
    {
        this.effectsChanged = true;

        if (!this.getWorld().isClient)
        {

            for (StatusEffectInstance statusEffectInstance : effects)
            {
                statusEffectInstance.getEffectType().value().onRemoved(this.getAttributes());
            }

            this.updateAttributes();
        }
    }

    public Collection<StatusEffectInstance> getStatusEffects()
    {
        return this.activeStatusEffects.values();
    }

    public Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects()
    {
        return this.activeStatusEffects;
    }

    public boolean hasStatusEffect(RegistryEntry<StatusEffect> effect)
    {
        return this.activeStatusEffects.containsKey(effect);
    }

    @Nullable
    public StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect)
    {
        return this.activeStatusEffects.get(effect);
    }

    public final boolean addStatusEffect(StatusEffectInstance effect)
    {
        return this.addStatusEffect(effect, null);
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect)
    {
        if (this.getType().isIn(EntityTypeTags.IMMUNE_TO_INFESTED))
        {
            return !effect.equals(StatusEffects.INFESTED);
        }
        else if (this.getType().isIn(EntityTypeTags.IMMUNE_TO_OOZING))
        {
            return !effect.equals(StatusEffects.OOZING);
        }
        else if (!this.getType().isIn(EntityTypeTags.IGNORES_POISON_AND_REGEN))
        {
            return true;
        }
        else
        {
            return !effect.equals(StatusEffects.REGENERATION) && !effect.equals(StatusEffects.POISON);
        }
    }

    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable FakeEntity source)
    {
        if (!this.canHaveStatusEffect(effect))
        {
            return false;
        }
        else
        {
            StatusEffectInstance statusEffectInstance = this.activeStatusEffects.get(effect.getEffectType());
            boolean bl = false;

            if (statusEffectInstance == null)
            {
                this.activeStatusEffects.put(effect.getEffectType(), effect);
                this.onStatusEffectApplied(effect, source);
                bl = true;
            }
            else if (statusEffectInstance.upgrade(effect))
            {
                this.onStatusEffectUpgraded(statusEffectInstance, true, source);
                bl = true;
            }

            effect.getEffectType().value().onApplied(this.getAttributes(), effect.getAmplifier());
            return bl;
        }
    }

    protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable FakeEntity source)
    {
        this.effectsChanged = true;

        if (!this.getWorld().isClient)
        {
            effect.getEffectType().value().onApplied(this.getAttributes(), effect.getAmplifier());
            //this.sendEffectToControllingPlayer(effect);
        }
    }

    @Nullable
    public StatusEffectInstance removeStatusEffectInternal(RegistryEntry<StatusEffect> effect)
    {
        return this.activeStatusEffects.remove(effect);
    }

    public void removeStatusEffect(RegistryEntry<StatusEffect> effect)
    {
        StatusEffectInstance statusEffectInstance = this.removeStatusEffectInternal(effect);

        if (statusEffectInstance != null)
        {
            this.onStatusEffectsRemoved(List.of(statusEffectInstance));
        }
    }

    protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable FakeEntity source)
    {
        this.effectsChanged = true;

        if (reapplyEffect && !this.getWorld().isClient)
        {
            StatusEffect statusEffect = effect.getEffectType().value();
            statusEffect.onRemoved(this.getAttributes());
            statusEffect.onApplied(this.getAttributes(), effect.getAmplifier());
            this.updateAttributes();
        }
    }

    private void updateAttributes()
    {
        Set<EntityAttributeInstance> set = this.getAttributes().getPendingUpdate();

        for (EntityAttributeInstance entityAttributeInstance : set)
        {
            this.updateAttribute(entityAttributeInstance.getAttribute());
        }

        set.clear();
    }

    protected void updateAttribute(RegistryEntry<EntityAttribute> attribute)
    {
        float f;

        if (attribute.matches(EntityAttributes.MAX_HEALTH))
        {
            f = this.getMaxHealth();
            if (this.getHealth() > f)
            {
                this.setHealth(f);
            }
        }
        else if (attribute.matches(EntityAttributes.MAX_ABSORPTION))
        {
            f = this.getMaxAbsorption();
            if (this.getAbsorptionAmount() > f)
            {
                this.setAbsorptionAmount(f);
            }
        }
    }

    public float getAbsorptionAmount()
    {
        return this.absorptionAmount;
    }

    public final void setAbsorptionAmount(float absorptionAmount)
    {
        this.setAbsorptionAmountUnclamped(MathHelper.clamp(absorptionAmount, 0.0F, this.getMaxAbsorption()));
    }

    protected void setAbsorptionAmountUnclamped(float absorptionAmount)
    {
        this.absorptionAmount = absorptionAmount;
    }

    public final float getMaxHealth()
    {
        return (float) this.getAttributeValue(EntityAttributes.MAX_HEALTH);
    }

    public final float getMaxAbsorption()
    {
        return (float) this.getAttributeValue(EntityAttributes.MAX_ABSORPTION);
    }

    public float getHealth()
    {
        return this.health;
    }

    public void setHealth(float health)
    {
        this.health = MathHelper.clamp(health, 0.0F, this.getMaxHealth());
    }

    public boolean isAlive()
    {
        return this.getHealth() > 0.0F;
    }

    public boolean isDead()
    {
        return this.getHealth() <= 0.0F;
    }

    private ItemStack getSyncedArmorStack(EquipmentSlot slot)
    {
        return this.syncedArmorStacks.get(slot.getEntitySlotId());
    }

    private void setSyncedArmorStack(EquipmentSlot slot, ItemStack armor)
    {
        this.syncedArmorStacks.set(slot.getEntitySlotId(), armor);
    }

    private ItemStack getSyncedHandStack(EquipmentSlot slot)
    {
        return this.syncedHandStacks.get(slot.getEntitySlotId());
    }

    private void setSyncedHandStack(EquipmentSlot slot, ItemStack stack)
    {
        this.syncedHandStacks.set(slot.getEntitySlotId(), stack);
    }

    private void swapHandStacks()
    {
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.OFFHAND);
        this.equipStack(EquipmentSlot.OFFHAND, this.getEquippedStack(EquipmentSlot.MAINHAND));
        this.equipStack(EquipmentSlot.MAINHAND, itemStack);
    }

    public ItemStack getMainHandStack()
    {
        return this.getEquippedStack(EquipmentSlot.MAINHAND);
    }

    public ItemStack getOffHandStack()
    {
        return this.getEquippedStack(EquipmentSlot.OFFHAND);
    }

    public ItemStack getStackInArm(Arm arm)
    {
        return this.getMainArm() == arm ? this.getMainHandStack() : this.getOffHandStack();
    }

    @NotNull
    public ItemStack getWeaponStack()
    {
        return this.getMainHandStack();
    }

    public boolean isHolding(Item item)
    {
        return this.isHolding((stack) -> stack.isOf(item));
    }

    public boolean isHolding(Predicate<ItemStack> predicate)
    {
        return predicate.test(this.getMainHandStack()) || predicate.test(this.getOffHandStack());
    }

    @Nullable
    public ItemStack getStackInHand(Hand hand)
    {
        if (hand == Hand.MAIN_HAND)
        {
            return this.getEquippedStack(EquipmentSlot.MAINHAND);
        }
        else if (hand == Hand.OFF_HAND)
        {
            return this.getEquippedStack(EquipmentSlot.OFFHAND);
        }
        else
        {
            return null;
        }
    }

    public void setStackInHand(Hand hand, ItemStack stack)
    {
        if (hand == Hand.MAIN_HAND)
        {
            this.equipStack(EquipmentSlot.MAINHAND, stack);
        }
        else
        {
            if (hand != Hand.OFF_HAND)
            {
                return;
            }

            this.equipStack(EquipmentSlot.OFFHAND, stack);
        }

    }

    public ItemStack getSyncedBodyArmorStack()
    {
        return syncedBodyArmorStack;
    }

    public void setSyncedBodyArmorStack(ItemStack syncedBodyArmorStack)
    {
        this.syncedBodyArmorStack = syncedBodyArmorStack;
    }

    public boolean hasStackEquipped(EquipmentSlot slot)
    {
        return !this.getEquippedStack(slot).isEmpty();
    }

    public boolean canUseSlot(EquipmentSlot slot)
    {
        return false;
    }

    public final boolean canEquip(ItemStack stack, EquipmentSlot slot)
    {
        return true;
    }

    @Override
    public Iterable<ItemStack> getArmorItems()
    {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {
        // NO-OP
    }

    public Iterable<ItemStack> getHandItems()
    {
        return List.of();
    }

    public Iterable<ItemStack> getAllArmorItems()
    {
        return this.getArmorItems();
    }

    public Iterable<ItemStack> getEquippedItems()
    {
        return Iterables.concat(this.getHandItems(), this.getAllArmorItems());
    }

    protected void processEquippedStack(ItemStack stack)
    {
        stack.getItem().postProcessComponents(stack);
    }

    public boolean isBaby()
    {
        return false;
    }

    public float getScaleFactor()
    {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    public final float getScale()
    {
        AttributeContainer attributeContainer = this.getAttributes();
        return attributeContainer == null ? 1.0F : this.clampScale((float) attributeContainer.getValue(EntityAttributes.SCALE));
    }

    protected float clampScale(float scale)
    {
        return scale;
    }

    @Override
    public Arm getMainArm()
    {
        return Arm.RIGHT;
    }

    @Nullable
    public LivingEntity getAttacker()
    {
        return this.attacker;
    }

    public LivingEntity getLastAttacker()
    {
        return this.getAttacker();
    }

    public void setAttacker(@Nullable LivingEntity attacker)
    {
        this.attacker = attacker;
        //this.lastAttackedTime = this.age;
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        if (nbt.contains("attributes", 9) && this.getWorld() != null && !this.getWorld().isClient)
        {
            this.getAttributes().readNbt(nbt.getList("attributes", 10));
        }
        if (nbt.contains("active_effects", 9))
        {
            NbtList nbtList = nbt.getList("active_effects", 10);

            for (int i = 0; i < nbtList.size(); ++i)
            {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);

                if (statusEffectInstance != null)
                {
                    this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
                }
            }
        }
        if (nbt.contains("Health", 99))
        {
            this.setHealth(nbt.getFloat("Health"));
        }
        if (nbt.contains("Brain", 10))
        {
            this.brain = this.deserializeBrain(new Dynamic<>(NbtOps.INSTANCE, nbt.get("Brain")));
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putFloat("Health", this.getHealth());
        nbt.put("attributes", this.getAttributes().toNbt());

        if (!this.activeStatusEffects.isEmpty())
        {
            NbtList nbtList = new NbtList();

            for (StatusEffectInstance statusEffectInstance : this.activeStatusEffects.values())
            {
                nbtList.add(statusEffectInstance.writeNbt());
            }

            nbt.put("active_effects", nbtList);
        }
        DataResult<NbtElement> dataResult = this.brain.encode(NbtOps.INSTANCE);
        dataResult.resultOrPartial().ifPresent((brain) -> nbt.put("Brain", brain));

        super.writeNbt(nbt);
    }
}
