package fi.dy.masa.malilib.sync.fe;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class FakePassive extends FakeMob
{
    protected int breedingAge;
    protected int forcedAge;
    protected int happyTicksRemaining;

    public FakePassive(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public boolean isReadyToBreed()
    {
        return false;
    }

    public int getBreedingAge()
    {
        return this.breedingAge;
    }

    public void setBreedingAge(int age)
    {
        this.breedingAge = age;
    }

    public boolean isBaby()
    {
        return this.getBreedingAge() < 0;
    }

    public void setBaby(boolean baby)
    {
        this.setBreedingAge(baby ? -24000 : 0);
    }

    public static int toGrowUpAge(int breedingAge)
    {
        return (int) ((float) (breedingAge / 20) * 0.1F);
    }

    public int getForcedAge()
    {
        return this.forcedAge;
    }

    public int getHappyTicksRemaining()
    {
        return this.happyTicksRemaining;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Age", this.getBreedingAge());
        nbt.putInt("ForcedAge", this.forcedAge);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.setBreedingAge(nbt.getInt("Age"));
        this.forcedAge = nbt.getInt("ForcedAge");
    }
}
