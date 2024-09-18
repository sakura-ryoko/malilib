package fi.dy.masa.malilib.sync.fe;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class FakeTamable extends FakeAnimal implements Tameable
{
    protected UUID owner;
    protected boolean tamed;
    protected boolean sitting;

    public FakeTamable(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
    }

    public FakeTamable(Entity input)
    {
        super(input);

        if (input instanceof TameableEntity)
        {
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    @Override
    public UUID getOwnerUuid()
    {
        return this.owner;
    }

    public void setOwnerUuid(UUID uuid)
    {
        this.owner = uuid;
    }

    public void setOwner(PlayerEntity player)
    {
        this.setTamed(true, true);
        this.setOwnerUuid(player.getUuid());
    }

    public boolean isOwner(LivingEntity entity)
    {
        return entity == this.getOwner();
    }

    public boolean canBeLeashed()
    {
        return true;
    }

    protected void updateAttributesForTamed() {}

    public boolean isTamed()
    {
        return this.tamed;
    }

    public void setTamed(boolean tamed, boolean updateAttributes)
    {
        this.tamed = tamed;

        if (updateAttributes)
        {
            this.updateAttributesForTamed();
        }
    }

    public boolean isSitting()
    {
        return this.sitting;
    }

    public void setSitting(boolean sitting)
    {
        this.sitting = sitting;
    }

    public final boolean cannotFollowOwner()
    {
        return this.isSitting() || this.hasVehicle() || this.mightBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
    }

    protected boolean canTeleportOntoLeaves()
    {
        return false;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        if (this.getOwnerUuid() != null)
        {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        nbt.putBoolean("Sitting", this.sitting);

        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);

        if (nbt.containsUuid("Owner"))
        {
            UUID uuid = nbt.getUuid("Owner");
            this.setOwnerUuid(uuid);
            this.setTamed(true, false);
        }

        this.sitting = nbt.getBoolean("Sitting");
    }
}
