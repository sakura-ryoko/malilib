package fi.dy.masa.malilib.sync.fe;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class FakeItemEntity extends FakeEntity implements Ownable
{
    private ItemStack stack;
    private int itemAge;
    private int pickupDelay;
    private int health;
    @Nullable
    private UUID throwerUuid;
    @Nullable
    private Entity thrower;
    @Nullable
    private UUID owner;

    public FakeItemEntity(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.health = 5;
    }

    public FakeItemEntity(World world, int entityId, double x, double y, double z, ItemStack stack, double velocityX, double velocityY, double velocityZ)
    {
        this(EntityType.ITEM, world, entityId);
        this.setPosition(x, y, z);
        this.setVelocity(velocityX, velocityY, velocityZ);
        this.setStack(stack);
    }

    public FakeItemEntity(World world, int entityId, double x, double y, double z, ItemStack stack)
    {
        this(world, entityId, x, y, z, stack, world.random.nextDouble() * 0.2 - 0.1, 0.2, world.random.nextDouble() * 0.2 - 0.1);
    }

    private FakeItemEntity(ItemEntity entity)
    {
        super(entity.getType(), entity.getWorld(), entity.getId());
        this.setStack(entity.getStack().copy());
        this.copyPositionAndRotation(entity);
        this.itemAge = entity.getItemAge();
    }

    public FakeItemEntity(Entity input)
    {
        super(input);

        if (input instanceof ItemEntity ie)
        {
            this.setStack(ie.getStack());
            this.copyPositionAndRotation(ie);
            this.itemAge = ie.getItemAge();
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    @Override
    public @Nullable Entity getOwner()
    {
        if (this.thrower != null && !this.thrower.isRemoved())
        {
            return this.thrower;
        }
        else
        {
            return null;
        }
    }

    protected double getGravity()
    {
        return 0.04;
    }

    public Text getName()
    {
        Text text = this.getCustomName();
        return text != null ? text : this.getStack().getItemName();
    }

    public boolean isAttackable()
    {
        return false;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public void setOwner(@Nullable UUID owner)
    {
        this.owner = owner;
    }

    public UUID getOwnerUuid()
    {
        return this.owner;
    }

    public void setThrower(Entity thrower)
    {
        this.throwerUuid = thrower.getUuid();
        this.thrower = thrower;
    }

    public int getItemAge()
    {
        return this.itemAge;
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putShort("Health", (short) this.health);
        nbt.putShort("Age", (short) this.itemAge);
        nbt.putShort("PickupDelay", (short) this.pickupDelay);
        if (this.throwerUuid != null)
        {
            nbt.putUuid("Thrower", this.throwerUuid);
        }

        if (this.owner != null)
        {
            nbt.putUuid("Owner", this.owner);
        }

        if (!this.getStack().isEmpty())
        {
            nbt.put("Item", this.getStack().toNbt(this.getRegistryManager()));
        }

        super.writeNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        this.health = nbt.getShort("Health");
        this.itemAge = nbt.getShort("Age");
        if (nbt.contains("PickupDelay"))
        {
            this.pickupDelay = nbt.getShort("PickupDelay");
        }

        if (nbt.containsUuid("Owner"))
        {
            this.owner = nbt.getUuid("Owner");
        }

        if (nbt.containsUuid("Thrower"))
        {
            this.throwerUuid = nbt.getUuid("Thrower");
            this.thrower = null;
        }

        if (nbt.contains("Item", 10))
        {
            NbtCompound nbtCompound = nbt.getCompound("Item");
            this.setStack(ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
        }
        else
        {
            this.setStack(ItemStack.EMPTY);
        }
    }
}
