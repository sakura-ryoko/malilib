package fi.dy.masa.malilib.sync.fe;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FakeEntity
{
    private final EntityType<?> type;
    private int entityId;
    private World world;
    private Vec3d pos;
    private BlockPos blockPos;
    private ChunkPos chunkPos;
    @Nullable
    private FakeEntity vehicle;
    private Box boundingBox;
    private Vec3d velocity;
    private float pitch;
    private float yaw;

    private Text customName;
    private boolean nameVisible;
    private NbtCompound nbt;
    private NbtCompound subNbt;

    public FakeEntity(EntityType<?> type, World world, int entityId)
    {
        this.entityId = entityId;
        this.type = type;
        this.world = world;
        this.pos = Vec3d.ZERO;
        this.blockPos = BlockPos.ORIGIN;
        this.chunkPos = ChunkPos.ORIGIN;
    }

    public EntityType<?> getType()
    {
        return this.type;
    }

    public int getId()
    {
        return this.entityId;
    }

    public void setId(int id)
    {
        this.entityId = id;
    }

    public World getWorld()
    {
        return this.world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public DynamicRegistryManager getRegistryManager()
    {
        return this.getWorld().getRegistryManager();
    }

    public boolean equals(Object o)
    {
        if (o instanceof FakeEntity)
        {
            return ((FakeEntity) o).entityId == this.entityId;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.entityId;
    }

    public Vec3d getPos()
    {
        return this.pos;
    }

    public void setPos(Vec3d pos)
    {
        this.pos = pos;
    }

    public void setPosition(double x, double y, double z)
    {
        this.setPos(new Vec3d(x, y, z));
    }

    public void copyPositionAndRotation(Entity entity)
    {
        this.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
    }

    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch)
    {
        this.setPosition(x, y, z);
        this.setYaw(yaw);
        this.setPitch(pitch);
        //this.resetPosition();
        //this.refreshPosition();
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public float getYaw()
    {
        return this.yaw;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos pos)
    {
        this.blockPos = pos;
    }

    public ChunkPos getChunkPos()
    {
        return this.chunkPos;
    }

    public void setChunkPos(ChunkPos pos)
    {
        this.chunkPos = pos;
    }

    public Vec3d getVelocity()
    {
        return this.velocity;
    }

    public void setVelocity(Vec3d velocity)
    {
        this.velocity = velocity;
    }

    public void setVelocity(double x, double y, double z)
    {
        this.setVelocity(new Vec3d(x, y, z));
    }

    @Nullable
    public FakeEntity getVehicle()
    {
        return this.vehicle;
    }

    public void setVehicle(@Nullable FakeEntity entity)
    {
        this.vehicle = entity;
    }

    public boolean hasVehicle()
    {
        return this.getVehicle() != null;
    }

    public final Box getBoundingBox()
    {
        return this.boundingBox;
    }

    public final void setBoundingBox(Box boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    // Highly dumbed-down Custom Name handling
    public void setCustomName(@Nullable Text name)
    {
        this.customName = name;
    }

    private static Text removeClickEvents(Text textComponent)
    {
        MutableText mutableText = textComponent.copyContentOnly().setStyle(textComponent.getStyle().withClickEvent(null));

        for (Text text : textComponent.getSiblings())
        {
            mutableText.append(removeClickEvents(text));
        }

        return mutableText;
    }

    public Text getName()
    {
        Text text = this.getCustomName();
        return text != null ? removeClickEvents(text) : this.getDefaultName();
    }

    protected Text getDefaultName()
    {
        return this.type.getName();
    }

    public Text getDisplayName()
    {
        return this.getName();
    }

    @Nullable
    public Text getCustomName()
    {
        return this.customName;
    }

    public boolean hasCustomName()
    {
        return this.customName != null;
    }

    public void setCustomNameVisible(boolean visible)
    {
        this.nameVisible = visible;
    }

    public boolean isCustomNameVisible()
    {
        return this.nameVisible;
    }

    public boolean shouldRenderName()
    {
        return this.isCustomNameVisible();
    }

    // Plain stupid simple way to read / write the FE's NBT data.
    public void readNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.isEmpty())
        {
            this.nbt = new NbtCompound();
        }
        else
        {
            this.nbt.copyFrom(nbt);
        }
    }

    public NbtCompound writeNbt(@Nonnull NbtCompound nbt)
    {
        if (this.nbt.isEmpty())
        {
            nbt.copyFrom(new NbtCompound());
        }
        else
        {
            nbt.copyFrom(this.nbt);
        }

        return this.nbt;
    }

    public boolean saveSelfNbt(@Nonnull NbtCompound nbt)
    {
        String idString = this.getEntityId();

        if (idString == null)
        {
            return false;
        }

        nbt.putString("id", idString);
        this.writeNbt(nbt);
        return true;
    }

    @Nullable
    public final String getEntityId()
    {
        EntityType<?> type = this.getType();
        Identifier id = EntityType.getId(type);

        return type.isSaveable() && id != null ? id.toString() : null;
    }

    public boolean saveNbt(@Nonnull NbtCompound nbt)
    {
        if (this.hasVehicle())
        {
            return false;
        }

        return this.saveSelfNbt(nbt);
    }

    public void copyFrom(FakeEntity entity)
    {
        NbtCompound newNbt = entity.writeNbt(new NbtCompound());
        //newNbt.remove("Dimension");
        this.readNbt(newNbt);
    }

    public void copyFrom(Entity entity)
    {
        NbtCompound newNbt = entity.writeNbt(new NbtCompound());
        //newNbt.remove("Dimension");
        this.readNbt(newNbt);
    }

    protected abstract void readCustomDataFromNbt(NbtCompound nbt);

    protected abstract void writeCustomDataToNbt(NbtCompound nbt);

    public void readSubNbt(@Nonnull NbtCompound subNbt)
    {
        if (subNbt.isEmpty())
        {
            this.subNbt = new NbtCompound();
        }
        else
        {
            this.subNbt.copyFrom(subNbt);
        }
    }

    public void writeSubNbt(@Nonnull NbtCompound subNbt)
    {
        if (this.subNbt.isEmpty())
        {
            subNbt.copyFrom(new NbtCompound());
        }
        else
        {
            subNbt.copyFrom(this.subNbt);
        }

    }

    public boolean shouldRender(double distance)
    {
        return false;
    }

    protected void playStepSound(BlockPos pos, BlockState state)
    {
        // NO-OP
    }

    public boolean damage(DamageSource source, float amount)
    {
        return false;
    }

    public ActionResult interact(PlayerEntity player, Hand hand)
    {
        return ActionResult.FAIL;
    }

    public void handleStatus(byte status)
    {
        // NO-OP
    }

    @Nullable
    public ItemStack getWeaponStack()
    {
        return null;
    }

    @Nullable
    public ItemStack getPickBlockStack()
    {
        return null;
    }
}
