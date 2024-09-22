package fi.dy.masa.malilib.sync.data;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.component.ComponentMap;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.Constants;

public class SyncEquipment extends SyncData implements EquipmentHolder, Attackable
{
    private static final String ATTRIBUTES_NBT_KEY = "attributes";
    private static final String ARMOR_ITEMS_NBT_KEY = "ArmorItems";
    private static final String BODY_ARMOR_NBT_KEY = "body_armor_item";
    private static final String HAND_ITEMS_NBT_KEY = "HandItems";
    private static final String HEALTH_NBT_KEY = "Health";
    private static final int DEFAULT_MAX_HEALTH = 20;
    private static final int HAND_SLOTS = 2;
    private static final int ARMOR_SLOTS = 4;
    private DefaultedList<ItemStack> handItems;
    private DefaultedList<ItemStack> armorItems;
    private ItemStack bodyArmor;
    private AttributeContainer attributes;
    private float health;
    private float absorptionAmount;

    public SyncEquipment(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.initInventory();
        this.attributes = new AttributeContainer(createLivingAttributes().build());
        this.health = DEFAULT_MAX_HEALTH;
    }

    public SyncEquipment(Entity entity)
    {
        super(entity);
        this.initInventory();
        this.initAttributes(entity);
        this.copyNbtFromEntity(entity);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    protected void initInventory()
    {
        this.handItems = DefaultedList.ofSize(HAND_SLOTS, ItemStack.EMPTY);
        this.armorItems = DefaultedList.ofSize(ARMOR_SLOTS, ItemStack.EMPTY);
        this.bodyArmor = ItemStack.EMPTY;
    }

    protected void initAttributes(Entity entity)
    {
        this.attributes = new AttributeContainer(createLivingAttributes().build());
        this.health = DEFAULT_MAX_HEALTH;

        if (entity instanceof AnimalEntity)
        {
            this.buildAttributes(createAnimalAttributes());
        }
        else if (entity instanceof MobEntity)
        {
            this.buildAttributes(createMobAttributes());
        }
        else if (entity instanceof HostileEntity)
        {
            this.buildAttributes(createHostileAttributes());
        }
    }

    public boolean canUseSlot(EquipmentSlot slot)
    {
        return slot != EquipmentSlot.BODY;
    }

    public boolean isWearingBodyArmor()
    {
        return !this.getEquippedStack(EquipmentSlot.BODY).isEmpty();
    }

    public final boolean canEquip(ItemStack stack, EquipmentSlot slot)
    {
        return true;
    }

    public void equipBodyArmor(ItemStack stack)
    {
        this.bodyArmor = stack;
    }

    public Iterable<ItemStack> getHandItems()
    {
        return this.handItems;
    }

    protected ItemStack getSyncedArmorStack(EquipmentSlot slot)
    {
        return this.armorItems.get(slot.getEntitySlotId());
    }

    protected void setSyncedArmorStack(EquipmentSlot slot, ItemStack armor)
    {
        this.armorItems.set(slot.getEntitySlotId(), armor);
    }

    protected ItemStack getSyncedHandStack(EquipmentSlot slot)
    {
        return this.handItems.get(slot.getEntitySlotId());
    }

    private void setSyncedHandStack(EquipmentSlot slot, ItemStack stack)
    {
        this.handItems.set(slot.getEntitySlotId(), stack);
    }

    public Iterable<ItemStack> getArmorItems()
    {
        return this.armorItems;
    }

    public ItemStack getBodyArmor()
    {
        return this.bodyArmor;
    }

    public Iterable<ItemStack> getEquippedItems()
    {
        return Iterables.concat(this.getHandItems(), this.getAllArmorItems());
    }

    public Iterable<ItemStack> getAllArmorItems()
    {
        return (this.bodyArmor.isEmpty() ? this.armorItems : Iterables.concat(this.armorItems, List.of(this.bodyArmor)));
    }

    public ItemStack getEquippedStack(EquipmentSlot slot)
    {
        ItemStack stack;

        switch (slot.getType())
        {
            case HAND -> stack = this.handItems.get(slot.getEntitySlotId());
            case HUMANOID_ARMOR -> stack = this.armorItems.get(slot.getEntitySlotId());
            case ANIMAL_ARMOR -> stack = this.bodyArmor;
            default -> stack = ItemStack.EMPTY;
        }

        return stack;
    }

    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {
        switch (slot.getType())
        {
            case HAND:
                this.onEquipStack(slot, this.handItems.set(slot.getEntitySlotId(), stack), stack);
                break;
            case HUMANOID_ARMOR:
                this.onEquipStack(slot, this.armorItems.set(slot.getEntitySlotId(), stack), stack);
                break;
            case ANIMAL_ARMOR:
                ItemStack itemStack = this.bodyArmor;
                this.bodyArmor = stack;
                this.onEquipStack(slot, itemStack, stack);
        }
    }

    public boolean isArmorSlot(EquipmentSlot slot)
    {
        return true;
    }

    public void onEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack)
    {
        // NO-OP
    }

    public void setEquipmentDropChance(EquipmentSlot slot, float dropChance)
    {
        // NO-OP
    }

    public ItemStack getMainHandStack()
    {
        return this.getEquippedStack(EquipmentSlot.MAINHAND);
    }

    public ItemStack getOffHandStack()
    {
        return this.getEquippedStack(EquipmentSlot.OFFHAND);
    }

    @NotNull
    public ItemStack getWeaponStack()
    {
        return this.getMainHandStack();
    }

    public @Nullable LivingEntity getLastAttacker()
    {
        return null;
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return DefaultAttributeContainer.builder().add(EntityAttributes.MAX_HEALTH).add(EntityAttributes.KNOCKBACK_RESISTANCE).add(EntityAttributes.MOVEMENT_SPEED).add(EntityAttributes.ARMOR).add(EntityAttributes.ARMOR_TOUGHNESS).add(EntityAttributes.MAX_ABSORPTION).add(EntityAttributes.STEP_HEIGHT).add(EntityAttributes.SCALE).add(EntityAttributes.GRAVITY).add(EntityAttributes.SAFE_FALL_DISTANCE).add(EntityAttributes.FALL_DAMAGE_MULTIPLIER).add(EntityAttributes.JUMP_STRENGTH).add(EntityAttributes.OXYGEN_BONUS).add(EntityAttributes.BURNING_TIME).add(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE).add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY).add(EntityAttributes.MOVEMENT_EFFICIENCY).add(EntityAttributes.ATTACK_KNOCKBACK);
    }

    public static DefaultAttributeContainer.Builder createMobAttributes()
    {
        return createLivingAttributes().add(EntityAttributes.FOLLOW_RANGE, 16.0);
    }

    public static DefaultAttributeContainer.Builder createAnimalAttributes()
    {
        return createMobAttributes().add(EntityAttributes.TEMPT_RANGE, 10.0);
    }

    public static DefaultAttributeContainer.Builder createHostileAttributes()
    {
        return createMobAttributes().add(EntityAttributes.ATTACK_DAMAGE);
    }

    public @Nullable EntityAttributeInstance getAttributeInstance(RegistryEntry<EntityAttribute> attribute)
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

    public void readNbt(@NotNull NbtCompound nbt)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt);
    }

    public void writeNbt(@NotNull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        NbtList nbtList;
        int i;
        NbtCompound nbtCompound;

        if (nbt.contains(ATTRIBUTES_NBT_KEY, Constants.NBT.TAG_LIST) && this.getWorld() != null && !this.getWorld().isClient)
        {
            this.getAttributes().readNbt(nbt.getList(ATTRIBUTES_NBT_KEY, Constants.NBT.TAG_COMPOUND));
        }
        if (nbt.contains(HEALTH_NBT_KEY, Constants.NBT.TAG_ANY_NUMERIC))
        {
            this.setHealth(nbt.getFloat(HEALTH_NBT_KEY));
        }
        if (nbt.contains(ARMOR_ITEMS_NBT_KEY, Constants.NBT.TAG_LIST))
        {
            nbtList = nbt.getList(ARMOR_ITEMS_NBT_KEY, Constants.NBT.TAG_COMPOUND);

            if (this.armorItems == null)
            {
                this.armorItems = DefaultedList.ofSize(ARMOR_SLOTS, ItemStack.EMPTY);
            }
            for (i = 0; i < this.armorItems.size(); ++i)
            {
                nbtCompound = nbtList.getCompound(i);
                this.armorItems.set(i, ItemStack.fromNbtOrEmpty(this.getRegistryManager(), nbtCompound));
            }
        }
        if (nbt.contains(HAND_ITEMS_NBT_KEY, Constants.NBT.TAG_LIST))
        {
            nbtList = nbt.getList(HAND_ITEMS_NBT_KEY, Constants.NBT.TAG_COMPOUND);

            if (this.handItems == null)
            {
                this.handItems = DefaultedList.ofSize(HAND_SLOTS, ItemStack.EMPTY);
            }
            for (i = 0; i < this.handItems.size(); ++i)
            {
                nbtCompound = nbtList.getCompound(i);
                this.handItems.set(i, ItemStack.fromNbtOrEmpty(this.getRegistryManager(), nbtCompound));
            }
        }
        if (nbt.contains(BODY_ARMOR_NBT_KEY, Constants.NBT.TAG_COMPOUND))
        {
            this.bodyArmor = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound(BODY_ARMOR_NBT_KEY)).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.bodyArmor = ItemStack.EMPTY;
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putFloat(HEALTH_NBT_KEY, this.getHealth());
        nbt.put(ATTRIBUTES_NBT_KEY, this.getAttributes().toNbt());

        if (!this.armorItems.isEmpty())
        {
            NbtList nbtList = new NbtList();

            for (ItemStack itemStack : this.armorItems)
            {
                if (!itemStack.isEmpty())
                {
                    nbtList.add(itemStack.toNbt(this.getRegistryManager()));
                }
                else
                {
                    nbtList.add(new NbtCompound());
                }
            }

            nbt.put(ARMOR_ITEMS_NBT_KEY, nbtList);
        }
        if (!this.handItems.isEmpty())
        {
            NbtList nbtList2 = new NbtList();

            for (ItemStack itemStack2 : this.handItems)
            {
                if (!itemStack2.isEmpty())
                {
                    nbtList2.add(itemStack2.toNbt(this.getRegistryManager()));
                }
                else
                {
                    nbtList2.add(new NbtCompound());
                }
            }

            nbt.put(HAND_ITEMS_NBT_KEY, nbtList2);
        }
        if (!this.bodyArmor.isEmpty())
        {
            nbt.put(BODY_ARMOR_NBT_KEY, this.bodyArmor.toNbt(this.getRegistryManager()));
        }
    }

    protected void readComponents(ComponentsAccess components)
    {
        // NO-OP
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        // NO-OP
    }
}
