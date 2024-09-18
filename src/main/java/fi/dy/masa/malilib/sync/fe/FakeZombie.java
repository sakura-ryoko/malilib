package fi.dy.masa.malilib.sync.fe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FakeZombie extends FakeHostile
{
    private boolean baby;
    private boolean convertingInWater;
    private boolean isTouchingWater;
    private boolean canBreakDoors;
    private int inWaterTime;
    private int ticksUntilWaterConversion;

    public FakeZombie(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public FakeZombie(Entity input)
    {
        super(input);

        if (input instanceof ZombieEntity)
        {
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    public boolean isConvertingInWater()
    {
        return this.convertingInWater;
    }

    public boolean isTouchingWater()
    {
        return this.isTouchingWater;
    }

    public boolean canBreakDoors()
    {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors)
    {
        this.canBreakDoors = canBreakDoors;
    }

    public boolean isBaby()
    {
        return this.baby;
    }

    public void setBaby(boolean baby)
    {
        this.baby = baby;
    }

    protected boolean canConvertInWater()
    {
        return true;
    }

    protected void convertInWater()
    {
        // Drowned
    }

    protected void convertTo(EntityType<? extends ZombieEntity> entityType)
    {
        // NO-OP
    }

    protected boolean burnsInDaylight()
    {
        return true;
    }

    public boolean canGather(ItemStack stack)
    {
        return true;
    }

    public int getInWaterTime()
    {
        return this.inWaterTime;
    }

    public void setInWaterTime(int inWaterTime)
    {
        this.inWaterTime = inWaterTime;
    }

    public int getTicksUntilWaterConversion()
    {
        return this.ticksUntilWaterConversion;
    }

    public void setTicksUntilWaterConversion(int i)
    {
        this.ticksUntilWaterConversion = i;
    }

    public static boolean shouldBeBaby(Random random)
    {
        return random.nextFloat() < 0.05F;
    }

    public static DefaultAttributeContainer.Builder createZombieAttributes()
    {
        return FakeHostile.createHostileAttributes().add(EntityAttributes.FOLLOW_RANGE, 35.0).add(EntityAttributes.MOVEMENT_SPEED, 0.23000000417232513).add(EntityAttributes.ATTACK_DAMAGE, 3.0).add(EntityAttributes.ARMOR, 2.0).add(EntityAttributes.SPAWN_REINFORCEMENTS);
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putBoolean("IsBaby", this.isBaby());
        nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
        nbt.putInt("InWaterTime", this.isTouchingWater() ? this.inWaterTime : -1);
        nbt.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.setBaby(nbt.getBoolean("IsBaby"));
        this.setCanBreakDoors(nbt.getBoolean("CanBreakDoors"));
        this.inWaterTime = nbt.getInt("InWaterTime");
        if (nbt.contains("DrownedConversionTime", 99) && nbt.getInt("DrownedConversionTime") > -1)
        {
            this.setTicksUntilWaterConversion(nbt.getInt("DrownedConversionTime"));
        }
    }
}
