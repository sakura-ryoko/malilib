package fi.dy.masa.malilib.sync.fe;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class FakeAnimal extends FakePassive
{
    private int loveTicks;
    @Nullable
    private UUID lovingPlayer;

    public FakeAnimal(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public abstract boolean isBreedingItem(ItemStack stack);

    public boolean canEat()
    {
        return this.loveTicks <= 0;
    }

    public void setLoveTicks(int loveTicks)
    {
        this.loveTicks = loveTicks;
    }

    public int getLoveTicks()
    {
        return this.loveTicks;
    }

    @Nullable
    public PlayerEntity getLovingPlayer()
    {
        if (this.lovingPlayer == null)
        {
            return null;
        }
        else
        {
            return this.getWorld().getPlayerByUuid(this.lovingPlayer);
        }
    }

    public boolean isInLove()
    {
        return this.loveTicks > 0;
    }

    public void resetLoveTicks()
    {
        this.loveTicks = 0;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("InLove", this.loveTicks);
        if (this.lovingPlayer != null)
        {
            nbt.putUuid("LoveCause", this.lovingPlayer);
        }
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.loveTicks = nbt.getInt("InLove");
        this.lovingPlayer = nbt.containsUuid("LoveCause") ? nbt.getUuid("LoveCause") : null;
    }
}
