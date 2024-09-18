package fi.dy.masa.malilib.sync.fe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.IEntityOwnedInventory;

public class FakePiglin extends FakeHostile implements IFakePiglin, InventoryOwner
{
    private final SimpleInventory inventory = new SimpleInventory(8);
    protected int timeInOverworld;
    protected boolean isImmuneToZombification;
    private boolean cannotHunt;
    private boolean baby;

    public FakePiglin(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        ((IEntityOwnedInventory) inventory).malilib$setFakeEntityOwner(this);
    }

    public FakePiglin(Entity input)
    {
        super(input);

        if (input instanceof AbstractPiglinEntity)
        {
            this.readCustomDataFromNbt(this.getNbt());
            this.buildAttributes(createPiglinAttributes());
            ((IEntityOwnedInventory) inventory).malilib$setFakeEntityOwner(this);
        }
    }

    public int getTimeInOverworld()
    {
        return this.timeInOverworld;
    }

    protected boolean isImmuneToZombification()
    {
        return this.isImmuneToZombification;
    }

    protected void setImmuneToZombification(boolean toggle)
    {
        this.isImmuneToZombification = toggle;
    }

    public boolean isAdult()
    {
        return !this.isBaby();
    }

    protected boolean isHoldingTool()
    {
        return this.getMainHandStack().contains(DataComponentTypes.TOOL);
    }

    // Piglin

    public SimpleInventory getInventory()
    {
        return this.inventory;
    }

    protected ItemStack addItem(ItemStack stack)
    {
        return this.inventory.addStack(stack);
    }

    protected boolean canInsertIntoInventory(ItemStack stack)
    {
        return this.inventory.canInsert(stack);
    }

    public static DefaultAttributeContainer.Builder createPiglinAttributes()
    {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 16.0).add(EntityAttributes.MOVEMENT_SPEED, 0.3499999940395355).add(EntityAttributes.ATTACK_DAMAGE, 5.0);
    }

    private void setCannotHunt(boolean cannotHunt) {
        this.cannotHunt = cannotHunt;
    }

    @Override
    public boolean canHunt() {
        return !this.cannotHunt;
    }

    @Override
    public PiglinActivity getActivity()
    {
        return null;
    }

    public boolean isBaby()
    {
        return this.baby;
    }

    public void setBaby(boolean toggle)
    {
        this.baby = toggle;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        if (this.isImmuneToZombification())
        {
            nbt.putBoolean("IsImmuneToZombification", true);
        }

        nbt.putInt("TimeInOverworld", this.timeInOverworld);

        if (this.isBaby())
        {
            nbt.putBoolean("IsBaby", true);
        }

        if (this.cannotHunt)
        {
            nbt.putBoolean("CannotHunt", true);
        }

        this.writeInventory(nbt, this.getRegistryManager());
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.setImmuneToZombification(nbt.getBoolean("IsImmuneToZombification"));
        this.timeInOverworld = nbt.getInt("TimeInOverworld");
        this.setBaby(nbt.getBoolean("IsBaby"));
        this.setCannotHunt(nbt.getBoolean("CannotHunt"));
        this.readInventory(nbt, this.getRegistryManager());
    }
}
