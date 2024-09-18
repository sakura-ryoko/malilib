package fi.dy.masa.malilib.sync.fe;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.IEntityOwnedInventory;

public class FakeHorse extends FakeAnimal implements InventoryChangedListener, RideableInventory, Tameable, Saddleable
{
    protected SimpleInventory items;
    protected boolean tamed;
    protected int temper;
    protected float jumpStrength;
    protected boolean jumping;
    protected boolean inAir;
    protected boolean saddled;
    protected boolean bred;
    protected boolean eating;
    private boolean trapped;
    private int trapTime;
    @Nullable
    private UUID ownerUuid;
    private boolean chested;
    private long lastPoseTick;
    private final Inventory armorInventory = new SingleStackInventory()
    {
        public void markDirty() {}

        public boolean canPlayerUse(PlayerEntity player) {return true;}

        public ItemStack getStack()
        {
            return FakeHorse.this.getBodyArmor();
        }

        public void setStack(ItemStack stack)
        {
            FakeHorse.this.equipBodyArmor(stack);
        }
    };

    public FakeHorse(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.onChestedStatusChanged();
    }

    public FakeHorse(Entity input)
    {
        super(input);

        if (input instanceof CamelEntity)
        {
            this.buildAttributes(createCamelAttributes());
        }
        else if (input instanceof SkeletonHorseEntity)
        {
            this.buildAttributes(createSkeletonHorseAttributes());
        }
        else if (input instanceof ZombieHorseEntity)
        {
            this.buildAttributes(createZombieHorseAttributes());
        }
        else if (input instanceof AbstractDonkeyEntity)
        {
            if (!(input instanceof LlamaEntity))
            {
                this.buildAttributes(createAbstractDonkeyAttributes());
            }

            this.onChestedStatusChanged();
        }
        else if (input instanceof AbstractHorseEntity)
        {
            this.buildAttributes(createBaseHorseAttributes());
        }

        this.readCustomDataFromNbt(this.getNbt());
    }

    public boolean isTame()
    {
        return this.tamed;
    }

    public void setTame(boolean tamed)
    {
        this.tamed = tamed;
    }

    @Nullable
    public UUID getOwnerUuid()
    {
        return this.ownerUuid;
    }

    public void setOwnerUuid(@Nullable UUID ownerUuid)
    {
        this.ownerUuid = ownerUuid;
    }

    public boolean isInAir()
    {
        return this.inAir;
    }

    public void setInAir(boolean inAir)
    {
        this.inAir = inAir;
    }

    public boolean isEatingGrass()
    {
        return this.eating;
    }

    public void setEatingGrass(boolean eating)
    {
        this.eating = eating;
    }

    public boolean isAngry() {return false;}

    @Override
    public boolean isBreedingItem(ItemStack stack)
    {
        return stack.isIn(ItemTags.HORSE_FOOD);
    }

    public boolean isBred()
    {
        return this.bred;
    }

    public void setBred(boolean bred)
    {
        this.bred = bred;
    }

    public boolean canBeSaddled()
    {
        return this.isAlive() && !this.isBaby() && this.isTame();
    }

    @Override
    public void saddle(ItemStack stack, @Nullable SoundCategory soundCategory)
    {
        // NO-OP
    }

    public boolean isSaddled()
    {
        return this.saddled;
    }

    public void setSaddled(boolean saddle)
    {
        this.saddled = saddle;
    }

    public void equipHorseArmor(PlayerEntity player, ItemStack stack)
    {
        if (this.canEquip(stack, EquipmentSlot.BODY))
        {
            this.equipBodyArmor(stack.splitUnlessCreative(1, player));
        }
    }

    protected boolean canDispenserEquipSlot(EquipmentSlot slot)
    {
        return true;
    }

    public int getTemper()
    {
        return this.temper;
    }

    public void setTemper(int temper)
    {
        this.temper = temper;
    }

    public int addTemper(int difference)
    {
        int i = MathHelper.clamp(this.getTemper() + difference, 0, this.getMaxTemper());
        this.setTemper(i);
        return i;
    }

    public int getMaxTemper()
    {
        return 100;
    }

    public boolean canBreedWith(FakeAnimal other)
    {
        return false;
    }

    protected boolean canBreed()
    {
        return !this.hasVehicle() && this.isTame() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    public void setJumpStrength(int strength)
    {
        if (this.isSaddled())
        {
            if (strength < 0)
            {
                strength = 0;
            }
            else
            {
                this.jumping = true;
                //this.updateAnger();
            }

            if (strength >= 90)
            {
                this.jumpStrength = 1.0F;
            }
            else
            {
                this.jumpStrength = 0.4F + 0.4F * (float) strength / 90.0F;
            }

        }
    }

    public float getJumpStrength()
    {
        return this.jumpStrength;
    }

    public boolean canJump()
    {
        return this.isSaddled();
    }

    public boolean isClimbing()
    {
        return false;
    }

    protected void initAttributes(Random random) {}

    public boolean areInventoriesDifferent(Inventory inventory)
    {
        return this.items != inventory;
    }

    public final int getInventorySize()
    {
        return getInventorySize(this.getInventoryColumns());
    }

    public static int getInventorySize(int columns)
    {
        return columns * 3 + 1;
    }

    public final Inventory getHorseInventory()
    {
        return this.items;
    }

    public final Inventory getArmorInventory()
    {
        return this.armorInventory;
    }

    public int getInventoryColumns()
    {
        return this.hasChest() ? 5 : 0;
    }

    protected void onChestedStatusChanged()
    {
        SimpleInventory simpleInventory = this.items;
        this.items = new SimpleInventory(this.getInventorySize());

        if (simpleInventory != null)
        {
            simpleInventory.removeListener(this);
            int i = Math.min(simpleInventory.size(), this.items.size());

            for (int j = 0; j < i; ++j)
            {
                ItemStack itemStack = simpleInventory.getStack(j);

                if (!itemStack.isEmpty())
                {
                    this.items.setStack(j, itemStack.copy());
                }
            }
        }

        this.items.addListener(this);
        this.updateSaddledFlag();
        ((IEntityOwnedInventory) items).malilib$setFakeEntityOwner(this);
    }

    private void updateSaddledFlag()
    {
        this.setSaddled(!this.items.getStack(0).isEmpty());
    }

    @Override
    public void onInventoryChanged(Inventory sender)
    {

    }

    @Override
    public void openInventory(PlayerEntity player)
    {
        // NO-OP
    }

    public static DefaultAttributeContainer.Builder createBaseHorseAttributes()
    {
        return FakeAnimal.createAnimalAttributes().add(EntityAttributes.JUMP_STRENGTH, 0.7).add(EntityAttributes.MAX_HEALTH, 53.0).add(EntityAttributes.MOVEMENT_SPEED, 0.22499999403953552).add(EntityAttributes.STEP_HEIGHT, 1.0).add(EntityAttributes.SAFE_FALL_DISTANCE, 6.0).add(EntityAttributes.FALL_DAMAGE_MULTIPLIER, 0.5);
    }

    // Camel

    public long getLastPoseTick()
    {
        return this.lastPoseTick;
    }

    public void setLastPoseTick(long tick)
    {
        this.lastPoseTick = tick;
    }

    public static DefaultAttributeContainer.Builder createCamelAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 32.0).add(EntityAttributes.MOVEMENT_SPEED, 0.09000000357627869).add(EntityAttributes.JUMP_STRENGTH, 0.41999998688697815).add(EntityAttributes.STEP_HEIGHT, 1.5);
    }

    // Donkey
    public boolean hasChest()
    {
        return this.chested;
    }

    public void setHasChest(boolean chested)
    {
        this.chested = chested;
    }

    public static DefaultAttributeContainer.Builder createAbstractDonkeyAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.17499999701976776).add(EntityAttributes.JUMP_STRENGTH, 0.5);
    }

    // Skeleton Horse
    public boolean isTrapped()
    {
        return this.trapped;
    }

    public void setTrapped(boolean trapped)
    {
        this.trapped = trapped;
    }

    public static DefaultAttributeContainer.Builder createSkeletonHorseAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 15.0).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    // Zombie Horse
    public static DefaultAttributeContainer.Builder createZombieHorseAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 15.0).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.setEatingGrass(nbt.getBoolean("EatingHaystack"));
        this.setBred(nbt.getBoolean("Bred"));
        this.setTemper(nbt.getInt("Temper"));
        this.setTame(nbt.getBoolean("Tame"));
        if (nbt.containsUuid("Owner"))
        {
            UUID uuid = nbt.getUuid("Owner");
            this.setOwnerUuid(uuid);
        }

        if (nbt.contains("ChestedHorse"))
        {
            this.setHasChest(nbt.getBoolean("ChestedHorse"));
        }
        if (this.items == null)
        {
            this.onChestedStatusChanged();
        }

        if (nbt.contains("SaddleItem", 10))
        {
            ItemStack itemStack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);

            if (itemStack.isOf(Items.SADDLE))
            {
                this.items.setStack(0, itemStack);
                this.setSaddled(true);
            }
        }

        if (nbt.contains("LastPoseTick"))
        {
            this.setLastPoseTick(nbt.getLong("LastPoseTick"));
        }
        if (this.hasChest())
        {
            NbtList nbtList = nbt.getList("Items", 10);

            for (int i = 0; i < nbtList.size(); ++i)
            {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte("Slot") & 255;
                if (j < this.items.size() - 1)
                {
                    this.items.setStack(j + 1, ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
                }
            }
        }
        if (nbt.contains("SkeletonTrap"))
        {
            this.setTrapped(nbt.getBoolean("SkeletonTrap"));
        }
        if (this.isTrapped())
        {
            this.trapTime = nbt.getInt("SkeletonTrapTime");
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putBoolean("EatingHaystack", this.isEatingGrass());
        nbt.putBoolean("Bred", this.isBred());
        nbt.putInt("Temper", this.getTemper());
        nbt.putBoolean("Tame", this.isTame());
        if (this.getOwnerUuid() != null)
        {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        if (!this.items.getStack(0).isEmpty())
        {
            nbt.put("SaddleItem", this.items.getStack(0).toNbt(this.getRegistryManager()));
        }

        if (this.getLastPoseTick() > 0L)
        {
            nbt.putLong("LastPoseTick", this.getLastPoseTick());
        }
        if (this.hasChest())
        {
            nbt.putBoolean("ChestedHorse", this.hasChest());
            NbtList nbtList = new NbtList();

            for (int i = 1; i < this.items.size(); ++i)
            {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty())
                {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte) (i - 1));
                    nbtList.add(itemStack.toNbt(this.getRegistryManager(), nbtCompound));
                }
            }

            nbt.put("Items", nbtList);
        }
        if (this.isTrapped())
        {
            nbt.putBoolean("SkeletonTrap", this.isTrapped());
            nbt.putInt("SkeletonTrapTime", this.trapTime);
        }

        super.writeCustomDataToNbt(nbt);
    }
}
