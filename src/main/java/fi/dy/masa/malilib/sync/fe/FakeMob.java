package fi.dy.masa.malilib.sync.fe;

import java.util.List;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FakeMob extends FakeLiving implements EquipmentHolder, Leashable
{
    protected final static Identifier RANDOM_SPAWN_BONUS_MODIFIER_ID = Identifier.ofVanilla("random_spawn_bonus");
    private DefaultedList<ItemStack> handItems;
    private DefaultedList<ItemStack> armorItems;
    private ItemStack bodyArmor;
    @Nullable
    private Leashable.LeashData leashData;
    private boolean leftHanded;

    public FakeMob(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.initItems();
    }

    public FakeMob(Entity input)
    {
        super(input);

        if (input instanceof MobEntity)
        {
            this.buildAttributes(createMobAttributes());
            this.initItems();
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    protected void initItems()
    {
        this.handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.bodyArmor = ItemStack.EMPTY;
    }

    protected void initGoals() {}

    public static DefaultAttributeContainer.Builder createMobAttributes()
    {
        return FakeLiving.createLivingAttributes().add(EntityAttributes.FOLLOW_RANGE, 16.0);
    }

    public Iterable<ItemStack> getHandItems()
    {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorItems()
    {
        return this.armorItems;
    }

    public ItemStack getBodyArmor()
    {
        return this.bodyArmor;
    }

    public boolean isWearingBodyArmor()
    {
        return !this.getEquippedStack(EquipmentSlot.BODY).isEmpty();
    }

    public void equipBodyArmor(ItemStack stack)
    {
        // NO-OP
    }

    public Iterable<ItemStack> getAllArmorItems()
    {
        return this.bodyArmor.isEmpty() ? this.armorItems : Iterables.concat(this.armorItems, List.of(this.bodyArmor));
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

    public void setEquipmentDropChance(EquipmentSlot slot, float dropChance)
    {
        // NO-OP
    }

    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {
        // NO-OP
    }

    public Arm getMainArm()
    {
        return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
    }

    public boolean isLeftHanded()
    {
        return this.leftHanded;
    }

    public void setLeftHanded(boolean leftHanded)
    {
        this.leftHanded = leftHanded;
    }

    @Nullable
    public Leashable.LeashData getLeashData()
    {
        return this.leashData;
    }

    public void setLeashData(@Nullable Leashable.LeashData leashData)
    {
        this.leashData = leashData;
    }

    public void detachLeash(boolean sendPacket, boolean dropItem)
    {
        // NO-OP
    }

    public void detachLeash()
    {
        // NO-OP
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
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

        nbt.put("ArmorItems", nbtList);
        NbtList nbtList3 = new NbtList();

        for (ItemStack itemStack2 : this.handItems)
        {
            if (!itemStack2.isEmpty())
            {
                nbtList3.add(itemStack2.toNbt(this.getRegistryManager()));
            }
            else
            {
                nbtList3.add(new NbtCompound());
            }
        }

        nbt.put("HandItems", nbtList3);
        if (!this.bodyArmor.isEmpty())
        {
            nbt.put("body_armor_item", this.bodyArmor.toNbt(this.getRegistryManager()));
        }

        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        NbtList nbtList;
        int i;
        NbtCompound nbtCompound;

        if (nbt.contains("ArmorItems", 9))
        {
            nbtList = nbt.getList("ArmorItems", 10);

            if (this.armorItems == null)
            {
                this.armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
            }
            for (i = 0; i < this.armorItems.size(); ++i)
            {
                nbtCompound = nbtList.getCompound(i);
                this.armorItems.set(i, ItemStack.fromNbtOrEmpty(this.getRegistryManager(), nbtCompound));
            }
        }
        if (nbt.contains("HandItems", 9))
        {
            nbtList = nbt.getList("HandItems", 10);

            if (this.handItems == null)
            {
                this.handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
            }
            for (i = 0; i < this.handItems.size(); ++i)
            {
                nbtCompound = nbtList.getCompound(i);
                this.handItems.set(i, ItemStack.fromNbtOrEmpty(this.getRegistryManager(), nbtCompound));
            }
        }
        if (nbt.contains("body_armor_item", 10))
        {
            this.bodyArmor = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("body_armor_item")).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.bodyArmor = ItemStack.EMPTY;
        }

        this.leashData = this.readLeashDataFromNbt(nbt);
    }
}
