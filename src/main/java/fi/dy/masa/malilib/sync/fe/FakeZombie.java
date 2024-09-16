package fi.dy.masa.malilib.sync.fe;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class FakeZombie extends MobEntity
{
    private boolean baby;
    private int type;
    private boolean convertingInWater;
    private boolean canBreakDoors;
    private int inWaterTime;
    private int ticksUntilWaterConversion;

    protected FakeZombie(EntityType<? extends MobEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public boolean isConvertingInWater()
    {
        return this.convertingInWater;
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

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsBaby", this.isBaby());
        nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
        nbt.putInt("InWaterTime", this.isTouchingWater() ? this.inWaterTime : -1);
        nbt.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
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
