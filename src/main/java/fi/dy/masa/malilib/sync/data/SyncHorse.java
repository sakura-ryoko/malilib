package fi.dy.masa.malilib.sync.data;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.Constants;
import fi.dy.masa.malilib.util.IEntityOwnedInventory;

public class SyncHorse extends SyncEquipment implements RideableInventory, InventoryChangedListener, Saddleable
{
    private static final String OWNER_KEY = "Owner";
    private static final String SADDLE_KEY = "SaddleItem";
    private static final String CHESTED_HORSE_KEY = "ChestedHorse";
    private static final String ITEMS_KEY = "Items";
    private static final String ITEMS_SLOT_KEY = "Slot";
    private static final int SADDLE_SLOT = 0;
    protected SimpleInventory items;
    protected float jumpStrength;
    @Nullable
    private UUID ownerUuid;
    private boolean chested;
    protected boolean saddled;
    private final Inventory armorInventory = new SingleStackInventory()
    {
        public void markDirty() {}

        public boolean canPlayerUse(PlayerEntity player) {return true;}

        public ItemStack getStack()
        {
            return SyncHorse.this.getBodyArmor();
        }

        public void setStack(ItemStack stack)
        {
            SyncHorse.this.equipBodyArmor(stack);
        }
    };

    public SyncHorse(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public SyncHorse(Entity entity)
    {
        super(entity);
        this.initAttributes(entity);
        this.copyNbtFromEntity(entity);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    protected void initAttributes(Entity entity)
    {
        super.initAttributes(entity);
        switch (entity)
        {
            case CamelEntity ignored -> this.buildAttributes(createCamelAttributes());
            case SkeletonHorseEntity ignored -> this.buildAttributes(createSkeletonHorseAttributes());
            case ZombieHorseEntity ignored -> this.buildAttributes(createZombieHorseAttributes());
            case AbstractDonkeyEntity ignored ->
            {
                this.buildAttributes(createAbstractDonkeyAttributes());
                this.onChestedStatusChanged();
            }
            case AbstractHorseEntity ignored -> this.buildAttributes(createBaseHorseAttributes());
            default ->
            {
            }
        }
    }

    public @Nullable UUID getOwnerUuid()
    {
        return this.ownerUuid;
    }

    public void setOwnerUuid(@Nullable UUID ownerUuid)
    {
        this.ownerUuid = ownerUuid;
    }

    public boolean canBeSaddled()
    {
        return !this.isSaddled();
    }

    public void saddle(ItemStack stack, @Nullable SoundCategory soundCategory)
    {
        // NO-OP
    }

    public boolean canUseSlot(EquipmentSlot slot)
    {
        return true;
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

    public void setJumpStrength(int strength)
    {
        if (this.isSaddled())
        {
            if (strength < 0)
            {
                strength = 0;
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

        //this.items.addListener(this);
        this.updateSaddledFlag();
        ((IEntityOwnedInventory) items).malilib$setSyncOwner(this);
    }

    private void updateSaddledFlag()
    {
        this.setSaddled(!this.items.getStack(0).isEmpty());
    }

    public void onInventoryChanged(Inventory sender)
    {
        // NO-OP
    }

    public void openInventory(PlayerEntity player)
    {
        // NO-OP
    }

    public boolean hasChest()
    {
        return this.chested;
    }

    public void setHasChest(boolean chested)
    {
        this.chested = chested;
    }

    public static DefaultAttributeContainer.Builder createBaseHorseAttributes()
    {
        return SyncEquipment.createAnimalAttributes().add(EntityAttributes.JUMP_STRENGTH, 0.7).add(EntityAttributes.MAX_HEALTH, 53.0).add(EntityAttributes.MOVEMENT_SPEED, 0.22499999403953552).add(EntityAttributes.STEP_HEIGHT, 1.0).add(EntityAttributes.SAFE_FALL_DISTANCE, 6.0).add(EntityAttributes.FALL_DAMAGE_MULTIPLIER, 0.5);
    }

    public static DefaultAttributeContainer.Builder createCamelAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 32.0).add(EntityAttributes.MOVEMENT_SPEED, 0.09000000357627869).add(EntityAttributes.JUMP_STRENGTH, 0.41999998688697815).add(EntityAttributes.STEP_HEIGHT, 1.5);
    }

    public static DefaultAttributeContainer.Builder createAbstractDonkeyAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.17499999701976776).add(EntityAttributes.JUMP_STRENGTH, 0.5);
    }

    public static DefaultAttributeContainer.Builder createSkeletonHorseAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 15.0).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    public static DefaultAttributeContainer.Builder createZombieHorseAttributes()
    {
        return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 15.0).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.readCustomDataFromNbt(nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        super.writeNbt(nbt);
        this.writeCustomDataToNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid(OWNER_KEY))
        {
            UUID uuid = nbt.getUuid(OWNER_KEY);
            this.setOwnerUuid(uuid);
        }

        if (nbt.contains(CHESTED_HORSE_KEY))
        {
            this.setHasChest(nbt.getBoolean(CHESTED_HORSE_KEY));
        }
        if (this.items == null)
        {
            this.onChestedStatusChanged();
        }
        if (nbt.contains(SADDLE_KEY, Constants.NBT.TAG_COMPOUND))
        {
            ItemStack itemStack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound(SADDLE_KEY)).orElse(ItemStack.EMPTY);

            if (itemStack.isOf(Items.SADDLE))
            {
                this.items.setStack(SADDLE_SLOT, itemStack);
                this.setSaddled(true);
            }
        }
        if (this.hasChest())
        {
            NbtList nbtList = nbt.getList(ITEMS_KEY, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < nbtList.size(); ++i)
            {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte(ITEMS_SLOT_KEY) & 255;
                if (j < this.items.size() - 1)
                {
                    this.items.setStack(j + 1, ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
                }
            }
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        if (this.getOwnerUuid() != null)
        {
            nbt.putUuid(OWNER_KEY, this.getOwnerUuid());
        }
        if (!this.items.getStack(SADDLE_SLOT).isEmpty())
        {
            nbt.put(SADDLE_KEY, this.items.getStack(SADDLE_SLOT).toNbt(this.getRegistryManager()));
        }
        if (this.hasChest())
        {
            nbt.putBoolean(CHESTED_HORSE_KEY, this.hasChest());
            NbtList nbtList = new NbtList();

            // Skips Saddle Slot
            for (int i = 1; i < this.items.size(); ++i)
            {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty())
                {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte(ITEMS_SLOT_KEY, (byte) (i - 1));
                    nbtList.add(itemStack.toNbt(this.getRegistryManager(), nbtCompound));
                }
            }

            nbt.put(ITEMS_KEY, nbtList);
        }

        super.writeCustomDataToNbt(nbt);
    }
}
