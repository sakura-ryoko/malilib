package fi.dy.masa.malilib.sync.fe;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityLike;

import fi.dy.masa.malilib.MaLiLib;

public abstract class FakeEntity implements EntityLike
{
    private final static Logger LOGGER = MaLiLib.logger;
    private EntityType<?> type;
    private int entityId;
    private World world;
    private Vec3d pos;
    private BlockPos blockPos;
    private ChunkPos chunkPos;
    private ImmutableList<Entity> passengerList;
    @Nullable
    private Entity vehicle;
    private Box boundingBox;
    private boolean onGround;
    private Vec3d velocity;
    private float pitch;
    private float yaw;
    public float fallDistance;
    private int fireTicks;
    private int portalCooldown;
    private boolean invulnerable;
    protected UUID uuid;
    protected String uuidString;
    private boolean glowing;
    private Set<String> commandTags;
    private boolean hasVisualFire;
    private Text customName;
    private boolean nameVisible;
    private int air;
    private boolean silent;
    private boolean noGravity;
    private int ticksFrozen;
    private NbtCompound nbt;
    private boolean loaded;

    public FakeEntity(EntityType<?> type, World world, int entityId)
    {
        this.entityId = entityId;
        this.type = type;
        this.world = world;
        this.pos = Vec3d.ZERO;
        this.blockPos = BlockPos.ORIGIN;
        this.chunkPos = ChunkPos.ORIGIN;
        this.passengerList = ImmutableList.of();
        this.loaded = false;
    }

    public EntityType<?> getType()
    {
        return this.type;
    }

    public void setType(EntityType<?> type)
    {
        this.type = type;
    }

    @Override
    public int getId()
    {
        return this.entityId;
    }

    public void setId(int id)
    {
        this.entityId = id;
    }

    public boolean isLoaded()
    {
        return this.loaded;
    }

    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded && !this.nbt.isEmpty();
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

    public final double getX()
    {
        return this.pos.x;
    }

    public final double getY()
    {
        return this.pos.y;
    }

    public final double getZ()
    {
        return this.pos.z;
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

    @Override
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
    public Entity getVehicle()
    {
        return this.vehicle;
    }

    public void setVehicle(@Nullable Entity entity)
    {
        this.vehicle = entity;
    }

    public boolean hasVehicle()
    {
        return this.getVehicle() != null;
    }

    public final List<Entity> getPassengerList()
    {
        return this.passengerList;
    }

    @Nullable
    public Entity getFirstPassenger()
    {
        return this.passengerList.isEmpty() ? null : (Entity) this.passengerList.get(0);
    }

    public boolean hasPassenger(Entity passenger)
    {
        return this.passengerList.contains(passenger);
    }

    public boolean hasPassengers()
    {
        return !this.passengerList.isEmpty();
    }

    public void setPassengerList(List<Entity> passengers)
    {
        this.passengerList = ImmutableList.copyOf(passengers);
    }

    @Override
    public final Box getBoundingBox()
    {
        return this.boundingBox;
    }

    @Override
    public void setChangeListener(EntityChangeListener changeListener)
    {
        // NO-OP
    }

    @Override
    public Stream<FakeEntity> streamSelfAndPassengers()
    {
        return Stream.empty();
    }

    @Override
    public Stream<FakeEntity> streamPassengersAndSelf()
    {
        return Stream.empty();
    }

    @Override
    public void setRemoved(Entity.RemovalReason reason)
    {
        this.loaded = false;
    }

    @Override
    public boolean shouldSave()
    {
        return true;
    }

    @Override
    public boolean isPlayer()
    {
        return false;
    }

    public final void setBoundingBox(Box boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
        this.uuidString = this.uuid.toString();
    }

    @Override
    public UUID getUuid()
    {
        return this.uuid;
    }

    public String getUuidAsString()
    {
        return this.uuidString;
    }

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

    public Set<String> getCommandTags()
    {
        return this.commandTags;
    }

    public void setCommandTags(Set<String> commandTags)
    {
        this.commandTags.clear();
        this.commandTags.addAll(commandTags);
    }

    public int getAir()
    {
        return this.air;
    }

    public void setAir(int air)
    {
        this.air = air;
    }

    public boolean isSilent()
    {
        return this.silent;
    }

    public void setSilent(boolean silent)
    {
        this.silent = silent;
    }

    public boolean hasNoGravity()
    {
        return this.noGravity;
    }

    public void setNoGravity(boolean noGravity)
    {
        this.noGravity = noGravity;
    }

    public boolean isGlowing()
    {
        return this.glowing;
    }

    public void setGlowing(boolean glowing)
    {
        this.glowing = glowing;
    }

    public int getFrozenTicks()
    {
        return this.ticksFrozen;
    }

    public void setFrozenTicks(int ticksFrozen)
    {
        this.ticksFrozen = ticksFrozen;
    }

    public boolean isOnGround()
    {
        return this.onGround;
    }

    public void setOnGround(boolean toggle)
    {
        this.onGround = toggle;
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

    public void readNbtInternal(@Nonnull NbtCompound nbt)
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

    public void readNbt(NbtCompound nbt)
    {
        try
        {
            NbtList nbtList = nbt.getList("Pos", 6);
            NbtList nbtList2 = nbt.getList("Motion", 6);
            NbtList nbtList3 = nbt.getList("Rotation", 5);
            double d = nbtList2.getDouble(0);
            double e = nbtList2.getDouble(1);
            double f = nbtList2.getDouble(2);
            this.setVelocity(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
            double g = 3.0000512E7;
            this.setPosition(MathHelper.clamp(nbtList.getDouble(0), -3.0000512E7, 3.0000512E7), MathHelper.clamp(nbtList.getDouble(1), -2.0E7, 2.0E7), MathHelper.clamp(nbtList.getDouble(2), -3.0000512E7, 3.0000512E7));
            this.setYaw(nbtList3.getFloat(0));
            this.setPitch(nbtList3.getFloat(1));
            this.fallDistance = nbt.getFloat("FallDistance");
            this.fireTicks = nbt.getShort("Fire");
            if (nbt.contains("Air"))
            {
                this.setAir(nbt.getShort("Air"));
            }

            this.onGround = nbt.getBoolean("OnGround");
            this.invulnerable = nbt.getBoolean("Invulnerable");
            this.portalCooldown = nbt.getInt("PortalCooldown");
            if (nbt.containsUuid("UUID"))
            {
                this.uuid = nbt.getUuid("UUID");
                this.uuidString = this.uuid.toString();
            }

            if (nbt.contains("CustomName", 8))
            {
                String string = nbt.getString("CustomName");

                try
                {
                    this.setCustomName(Text.Serialization.fromJson(string, this.getRegistryManager()));
                }
                catch (Exception err)
                {
                    LOGGER.warn("FakeEntity#readNbt(): Error loading Nbt, Failed to parse entity custom name [{}], reason [{}]", string, err.getMessage());
                }
            }

            this.setCustomNameVisible(nbt.getBoolean("CustomNameVisible"));
            this.setSilent(nbt.getBoolean("Silent"));
            this.setNoGravity(nbt.getBoolean("NoGravity"));
            this.setGlowing(nbt.getBoolean("Glowing"));
            this.setFrozenTicks(nbt.getInt("TicksFrozen"));
            this.hasVisualFire = nbt.getBoolean("HasVisualFire");
            if (nbt.contains("Tags", 9))
            {
                this.commandTags.clear();
                NbtList nbtList4 = nbt.getList("Tags", 8);
                int i = Math.min(nbtList4.size(), 1024);

                for (int j = 0; j < i; ++j)
                {
                    this.commandTags.add(nbtList4.getString(j));
                }
            }

            this.readCustomDataFromNbt(nbt);
            this.readNbtInternal(nbt);
        }
        catch (Throwable err)
        {
            LOGGER.error("FakeEntity#readNbt(): Error loading Nbt, reason: [{}]", err.getMessage());
        }
    }

    @Nullable
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        try
        {
            if (this.vehicle != null)
            {
                nbt.put("Pos", this.toNbtList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
            }
            else
            {
                nbt.put("Pos", this.toNbtList(this.getX(), this.getY(), this.getZ()));
            }

            Vec3d vec3d = this.getVelocity();
            nbt.put("Motion", this.toNbtList(vec3d.x, vec3d.y, vec3d.z));
            nbt.put("Rotation", this.toNbtList(this.getYaw(), this.getPitch()));
            nbt.putFloat("FallDistance", this.fallDistance);
            nbt.putShort("Fire", (short) this.fireTicks);
            nbt.putShort("Air", (short) this.getAir());
            nbt.putBoolean("OnGround", this.isOnGround());
            nbt.putBoolean("Invulnerable", this.invulnerable);
            nbt.putInt("PortalCooldown", this.portalCooldown);
            nbt.putUuid("UUID", this.getUuid());
            Text text = this.getCustomName();
            if (text != null)
            {
                nbt.putString("CustomName", Text.Serialization.toJsonString(text, this.getRegistryManager()));
            }

            if (this.isCustomNameVisible())
            {
                nbt.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }

            if (this.isSilent())
            {
                nbt.putBoolean("Silent", this.isSilent());
            }

            if (this.hasNoGravity())
            {
                nbt.putBoolean("NoGravity", this.hasNoGravity());
            }

            if (this.glowing)
            {
                nbt.putBoolean("Glowing", true);
            }

            int i = this.getFrozenTicks();
            if (i > 0)
            {
                nbt.putInt("TicksFrozen", this.getFrozenTicks());
            }

            if (this.hasVisualFire)
            {
                nbt.putBoolean("HasVisualFire", this.hasVisualFire);
            }

            NbtList nbtList;
            Iterator<String> iterator;
            if (!this.commandTags.isEmpty())
            {
                nbtList = new NbtList();
                iterator = this.commandTags.iterator();

                while (iterator.hasNext())
                {
                    String string = iterator.next();
                    nbtList.add(NbtString.of(string));
                }

                nbt.put("Tags", nbtList);
            }

            this.writeCustomDataToNbt(nbt);
            if (this.hasPassengers())
            {
                nbtList = new NbtList();

                for (Entity entity : this.getPassengerList())
                {
                    NbtCompound nbtCompound = new NbtCompound();
                    if (entity.saveSelfNbt(nbtCompound))
                    {
                        nbtList.add(nbtCompound);
                    }
                }

                if (!nbtList.isEmpty())
                {
                    nbt.put("Passengers", nbtList);
                }
            }

            this.writeNbtInternal(nbt);
            return nbt;
        }
        catch (Throwable err)
        {
            LOGGER.error("FakeEntity#writeNbt(): Error writing Nbt, reason: [{}]", err.getMessage());
            return null;
        }
    }

    public void writeNbtInternal(@Nonnull NbtCompound nbt)
    {
        if (this.nbt.isEmpty())
        {
            nbt.copyFrom(new NbtCompound());
        }
        else
        {
            nbt.copyFrom(this.nbt);
        }
    }

    public boolean saveSelfNbt(@Nonnull NbtCompound nbt)
    {
        String idString = this.getEntityIdString();

        if (idString == null)
        {
            return false;
        }

        nbt.putString("id", idString);
        this.writeNbt(nbt);
        return true;
    }

    public boolean saveNbt(@Nonnull NbtCompound nbt)
    {
        if (this.hasVehicle())
        {
            return false;
        }

        return this.saveSelfNbt(nbt);
    }

    protected NbtList toNbtList(double... values)
    {
        NbtList nbtList = new NbtList();

        for (double d : values)
        {
            nbtList.add(NbtDouble.of(d));
        }

        return nbtList;
    }

    protected NbtList toNbtList(float... values)
    {
        NbtList nbtList = new NbtList();

        for (float f : values)
        {
            nbtList.add(NbtFloat.of(f));
        }

        return nbtList;
    }


    @Nullable
    public final String getEntityIdString()
    {
        EntityType<?> type = this.getType();
        Identifier id = EntityType.getId(type);

        return type.isSaveable() && id != null ? id.toString() : null;
    }

    protected abstract void readCustomDataFromNbt(NbtCompound nbt);

    protected abstract void writeCustomDataToNbt(NbtCompound nbt);

    @Nullable
    public FakeEntity createEmpty(EntityType<?> type, World world, int entityId)
    {
        return FakeEntity.this.createEmptyInternal(type, world, entityId);
    }

    private FakeEntity createEmptyInternal(EntityType<?> type, World world, int entityId)
    {
        this.clear();
        this.setType(type);
        this.setWorld(world);
        this.setId(entityId);

        return this;
    }

    @Nullable
    public FakeEntity createFromNbt(EntityType<?> type, int entityId, @Nonnull NbtCompound nbt)
    {
        this.setType(type);
        this.setId(entityId);
        Identifier identifier = EntityType.getId(type);

        if (identifier != null)
        {
            nbt.putString("id", identifier.toString());
        }
        this.readNbt(nbt);

        return this;
    }

    @Nullable
    public FakeEntity copyFromEntity(@Nonnull Entity entity)
    {
        Entity.RemovalReason reason = entity.getRemovalReason();
        if (reason != null && !reason.shouldSave())
        {
            return null;
        }

        EntityType<?> type = entity.getType();
        int id = entity.getId();
        NbtCompound nbt = entity.writeNbt(new NbtCompound());
        Identifier identifier = EntityType.getId(type);

        if (identifier != null)
        {
            nbt.putString("id", identifier.toString());
        }
        this.setType(type);
        this.setId(id);
        this.readNbt(nbt);

        return this;
    }

    public void onRemoved()
    {
        this.clear();
    }

    public void clear()
    {
        this.setLoaded(false);
        this.setId(-1);
        this.setUuid(Util.NIL_UUID);
        this.nbt.copyFrom(new NbtCompound());
        this.commandTags.clear();
        this.passengerList = ImmutableList.of();
        this.world = null;
        this.pos = Vec3d.ZERO;
        this.blockPos = BlockPos.ORIGIN;
        this.chunkPos = ChunkPos.ORIGIN;
    }
}
