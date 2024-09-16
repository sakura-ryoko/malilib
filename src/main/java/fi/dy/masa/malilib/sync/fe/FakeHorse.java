package fi.dy.masa.malilib.sync.fe;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public abstract class FakeHorse extends FakeAnimal implements RideableInventory, Tameable, Saddleable
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

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData)
    {
        if (entityData == null)
        {
            entityData = new PassiveEntity.PassiveData(0.2F);
        }

        this.initAttributes(world.getRandom());
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

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

    public final Inventory getArmorInventory()
    {
        return this.armorInventory;
    }

    public int getInventoryColumns()
    {
        return this.hasChest() ? 5 : 0;
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

    // Skeleton Horse
    public boolean isTrapped() {
        return this.trapped;
    }

    public void setTrapped(boolean trapped)
    {
        this.trapped = trapped;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
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

        if (nbt.contains("SaddleItem", 10))
        {
            ItemStack itemStack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("SaddleItem")).orElse(ItemStack.EMPTY);

            if (itemStack.isOf(Items.SADDLE))
            {
                this.items.setStack(0, itemStack);
                this.setSaddled(true);
            }
        }

        if (nbt.contains("ChestedHorse"))
        {
            this.setHasChest(nbt.getBoolean("ChestedHorse"));
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
}
