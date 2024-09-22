package fi.dy.masa.malilib.sync.data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.Constants;

public abstract class SyncData
{
    // Generic
    protected final static Logger LOGGER = MaLiLib.logger;
    private static final String BLOCK_ENTITY_KEY = "components";
    private static final String CUSTOM_NAME_KEY = "CustomName";
    private static final String UUID_KEY = "UUID";
    private static final String POS_KEY = "Pos";
    private static final String ID_KEY = "id";
    private World world;
    private Text customName;
    private ComponentMap components;
    private NbtCompound nbt;
    private boolean loaded;
    private final long entry;

    // Block
    private BlockEntityType<?> blockType;
    private BlockPos blockPos;
    private BlockState blockState;

    // Entity
    private EntityType<?> entityType;
    private int entityId;
    private UUID uuid;
    private Vec3d entityPos;
    private ChunkPos chunkPos;

    public SyncData(BlockEntityType<?> type, BlockPos pos, BlockState state, World world)
    {
        this.entityType = null;
        this.blockType = type;
        this.entityId = -1;
        this.world = world;
        this.blockPos = pos;
        this.blockState = state;
        this.entityPos = Vec3d.ZERO;
        this.chunkPos = ChunkPos.ORIGIN;
        this.uuid = Util.NIL_UUID;
        this.customName = Text.empty();
        this.components = ComponentMap.EMPTY;
        this.nbt = new NbtCompound();
        this.loaded = false;
        this.entry = Util.getMeasuringTimeMs();
    }

    public SyncData(BlockEntity be)
    {
        this(be.getType(), be.getPos(), be.getCachedState(), be.getWorld());
        System.out.printf("SyncData: new block entity type [%s], pos: [%s], state [%s]\n", be.getType().getClass().getTypeName(), be.getPos().toShortString(), be.getCachedState().toString());
    }

    public SyncData(EntityType<?> type, World world, int entityId)
    {
        this.blockType = null;
        this.entityType = type;
        this.entityId = entityId;
        this.world = world;
        this.entityPos = Vec3d.ZERO;
        this.blockPos = BlockPos.ORIGIN;
        this.chunkPos = ChunkPos.ORIGIN;
        this.blockState = null;
        this.uuid = Util.NIL_UUID;
        this.customName = Text.empty();
        this.components = ComponentMap.EMPTY;
        this.nbt = new NbtCompound();
        this.loaded = false;
        this.entry = Util.getMeasuringTimeMs();
    }

    public SyncData(Entity entity)
    {
        this(entity.getType(), entity.getWorld(), entity.getId());
        System.out.printf("SyncData: new entity type [%s], pos: [%s], id: [%d]\n", entity.getType().getUntranslatedName(), entity.getBlockPos().toShortString(), entity.getId());
    }

    public boolean isLoaded()
    {
        return this.loaded;
    }

    public long getEntryTime()
    {
        return this.entry;
    }

    public @Nullable EntityType<?> getEntityType()
    {
        return this.entityType;
    }

    public @Nullable BlockEntityType<?> getBlockType()
    {
        return this.blockType;
    }

    private void checkWorld()
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (this.world == null && mc.world != null)
        {
            this.world = mc.world;
        }
    }

    public World getWorld()
    {
        this.checkWorld();
        return this.world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public boolean hasWorld()
    {
        return this.world != null;
    }

    public DynamicRegistryManager getRegistryManager()
    {
        return this.getWorld().getRegistryManager();
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos pos)
    {
        this.blockPos = pos;
    }

    public @Nullable BlockState getState()
    {
        return this.blockState;
    }

    public void setState(BlockState state)
    {
        this.blockState = state;
    }

    public ChunkPos getChunkPos()
    {
        return this.chunkPos;
    }

    public void setChunkPos(ChunkPos pos)
    {
        this.chunkPos = pos;
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public void setCustomName(@Nullable Text name)
    {
        this.customName = name;
    }

    public Text getDefaultName()
    {
        if (this.entityType != null)
        {
            return this.entityType.getName();
        }
        if (this.blockType != null)
        {
            return Text.of(this.blockType.getClass().getTypeName());
        }

        return Text.empty();
    }

    public @Nullable Text getCustomName()
    {
        return this.customName;
    }

    public ComponentMap getComponents()
    {
        return this.components;
    }

    public void setComponents(ComponentMap components)
    {
        this.components = components;
    }

    protected abstract void initInventory();

    protected abstract void initAttributes(Entity entity);

    public void copyNbtFromBlockEntity(BlockEntity be)
    {
        NbtCompound nbt = be.createNbtWithIdentifyingData(this.getRegistryManager());
        this.blockType = be.getType();
        this.setBlockPos(be.getPos());
        this.writeBlockPos(nbt);
        this.nbt = new NbtCompound();
        this.nbt.copyFrom(nbt);
        this.readNbt(nbt);
        this.loaded = true;
    }

    public void copyNbtFromEntity(Entity entity)
    {
        NbtCompound nbt = new NbtCompound();

        if (!entity.saveSelfNbt(nbt))
        {
            LOGGER.error("SyncData: Failed to save NBT data from entity [{}]", entity.getName());
        }
        else
        {
            this.entityType = entity.getType();
            this.entityPos = entity.getPos();
            this.world = entity.getWorld();
            this.writeVec3dPos(nbt);
            this.nbt = new NbtCompound();
            this.nbt.copyFrom(nbt);
            this.readUUIDFromNbt(nbt);
            this.readCustomNameFromNbt(nbt, this.getRegistryManager());
            this.readNbt(nbt);
            this.loaded = true;
        }
    }

    public final void readBlockNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        this.readBlockPos(nbt);
        SyncData.Components.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt).resultOrPartial((error) ->
                            LOGGER.warn("Failed to load components: {}", error)).ifPresent((components) -> this.components = components);
        this.readNbt(nbt);
        this.loaded = true;
    }

    public final void readEntityNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        this.readVec3dPos(nbt);
        this.readUUIDFromNbt(nbt);
        this.readCustomNameFromNbt(nbt, registry);
        this.readNbt(nbt);
        this.loaded = true;
    }

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        this.readCustomDataFromNbt(nbt);

        if (nbt.isEmpty())
        {
            this.nbt = new NbtCompound();
        }
        else
        {
            this.nbt = new NbtCompound();
            this.nbt.copyFrom(nbt);
        }
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);

        if (this.nbt == null || this.nbt.isEmpty())
        {
            nbt.copyFrom(new NbtCompound());
        }
        else
        {
            nbt = new NbtCompound();
            nbt.copyFrom(this.nbt);
        }
    }

    public NbtCompound getNbt()
    {
        if (this.nbt == null)
        {
            this.nbt = new NbtCompound();
        }

        return this.nbt;
    }

    public final NbtCompound createNbt(RegistryWrapper.WrapperLookup registry)
    {
        if (this.blockType == null)
        {
            return new NbtCompound();
        }
        NbtCompound nbtOut = new NbtCompound();
        this.writeNbt(nbtOut);
        Components.CODEC.encodeStart(registry.getOps(NbtOps.INSTANCE), this.components).resultOrPartial((snbt) ->
                   LOGGER.warn("Failed to save components: {}", snbt)).ifPresent((nbt) -> nbtOut.copyFrom((NbtCompound)nbt));

        return nbtOut;
    }

    public final NbtCompound createNbtWithID(RegistryWrapper.WrapperLookup registry)
    {
        if (this.blockType == null)
        {
            return new NbtCompound();
        }
        NbtCompound nbtCompound = this.createNbt(registry);
        this.writeBlockIdToNbt(nbtCompound);
        return nbtCompound;
    }

    public boolean saveSelfNbt(NbtCompound nbt)
    {
        if (this.entityType == null)
        {
            return false;
        }
        this.writeEntityIdToNbt(nbt);
        this.writeNbt(nbt);
        return true;
    }

    public boolean saveNbt(NbtCompound nbt)
    {
        if (this.entityType == null)
        {
            return false;
        }

        return this.saveSelfNbt(nbt);
    }

    public final NbtCompound createBasicNbt(RegistryWrapper.WrapperLookup registry)
    {
        if (this.blockType == null)
        {
            return new NbtCompound();
        }
        NbtCompound newNbt = new NbtCompound();
        this.writeNbt(newNbt);
        return newNbt;
    }

    public final NbtCompound createBasicNbtWithID(RegistryWrapper.WrapperLookup registry)
    {
        if (this.blockType == null)
        {
            return new NbtCompound();
        }
        NbtCompound newNbt = createBasicNbt(registry);
        this.writeBlockIdToNbt(newNbt);
        return newNbt;
    }

    public void setStackNbt(ItemStack stack, RegistryWrapper.WrapperLookup registry)
    {
        if (this.blockType == null)
        {
            return;
        }
        NbtCompound newNbt = this.createBasicNbt(registry);
        stack.clearComponentChanges();
        BlockItem.setBlockEntityData(stack, this.getBlockType(), newNbt);
        stack.applyComponentsFrom(this.createComponentMap());
    }

    public void readUUIDFromNbt(@Nonnull NbtCompound nbt)
    {
        if (nbt.containsUuid(UUID_KEY))
        {
            this.uuid = nbt.getUuid(UUID_KEY);
        }
    }

    public void writeUUIDToNbt(@Nonnull NbtCompound nbt)
    {
        nbt.putUuid(UUID_KEY, this.getUuid());
    }

    public void readCustomNameFromNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        if (!nbt.contains(CUSTOM_NAME_KEY))
        {
            return;
        }
        String string = nbt.getString(CUSTOM_NAME_KEY);

        try
        {
            this.setCustomName(toCustomName(string, registry));
        }
        catch (Exception err)
        {
            LOGGER.warn("readCustomNameFromNbt(): Error loading Nbt, Failed to parse entity custom name [{}], reason [{}]", string, err.getMessage());
        }
    }

    public void writeCustomNameToNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        if (this.customName != null)
        {
            nbt.putString(CUSTOM_NAME_KEY, fromCustomName(this.customName, registry));
        }
    }

    public void writeBlockIdToNbt(@Nonnull NbtCompound nbt)
    {
        if (this.blockType == null)
        {
            return;
        }

        Identifier identifier = BlockEntityType.getId(this.blockType);

        if (identifier == null)
        {
            LOGGER.error("writeBlockIdToNbt():  Error, invalid block entity type!");
            return;
        }

        this.putIdString(nbt, identifier.toString());
    }

    public void writeEntityIdToNbt(@Nonnull NbtCompound nbt)
    {
        if (this.entityType == null)
        {
            return;
        }

        Identifier id = EntityType.getId(this.entityType);

        if (id == null)
        {
            LOGGER.error("writeEntityIdToNbt():  Error, invalid entity type!");
            return;
        }

        this.putIdString(nbt, id.toString());
    }

    public void putIdString(@Nonnull NbtCompound nbt, String id)
    {
        nbt.putString(ID_KEY, id);
    }

    public void readBlockPos(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z"))
        {
            this.setBlockPos(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")).toImmutable());
        }
    }

    public void writeBlockPos(@Nonnull NbtCompound nbt)
    {
        if (!nbt.contains(ID_KEY))
        {
            this.writeBlockIdToNbt(nbt);
        }
        nbt.putInt("x", this.blockPos.getX());
        nbt.putInt("y", this.blockPos.getY());
        nbt.putInt("z", this.blockPos.getZ());
    }

    public void readVec3dPos(@Nonnull NbtCompound nbt)
    {
        if (!nbt.contains(POS_KEY))
        {
            return;
        }
        NbtList list = nbt.getList(POS_KEY, Constants.NBT.TAG_DOUBLE);

        double x = MathHelper.clamp(list.getDouble(0), -3.0000512E7, 3.0000512E7);
        double y = MathHelper.clamp(list.getDouble(1), -2.0E7, 2.0E7);
        double z = MathHelper.clamp(list.getDouble(2), -3.0000512E7, 3.0000512E7);

        this.entityPos = new Vec3d(x, y, z);
    }

    public void writeVec3dPos(@Nonnull NbtCompound nbt)
    {
        NbtList list = new NbtList();

        list.add(NbtDouble.of(this.entityPos.getX()));
        list.add(NbtDouble.of(this.entityPos.getY()));
        list.add(NbtDouble.of(this.entityPos.getZ()));

        nbt.put(POS_KEY, list);
    }

    public static @Nullable Text toCustomName(String json, RegistryWrapper.WrapperLookup registry)
    {
        try
        {
            return Text.Serialization.fromJson(json, registry);
        }
        catch (Exception e)
        {
            LOGGER.warn("Failed to parse custom name from [{}], discarding [{}]", json, e.getMessage());
            return null;
        }
    }

    public static String fromCustomName(Text name, RegistryWrapper.WrapperLookup registry)
    {
        return Text.Serialization.toJsonString(name, registry);
    }

    public void readComponents(ItemStack stack)
    {
        this.readComponents(stack.getDefaultComponents(), stack.getComponentChanges());
    }

    protected abstract void readCustomDataFromNbt(NbtCompound nbt);

    protected abstract void writeCustomDataToNbt(NbtCompound nbt);

    protected void readComponents(ComponentsAccess components) {}

    protected void addComponents(ComponentMap.Builder builder) {}

    public final void readComponents(ComponentMap defaults, ComponentChanges changes)
    {
        final Set<ComponentType<?>> set = new HashSet<>();
        final ComponentMap componentMap = MergedComponentMap.create(defaults, changes);

        set.add(DataComponentTypes.BLOCK_ENTITY_DATA);
        this.readComponents(new ComponentsAccess()
        {
            @Nullable
            public <T> T get(ComponentType<T> type)
            {
                set.add(type);
                return componentMap.get(type);
            }

            public <T> T getOrDefault(ComponentType<? extends T> type, T fallback)
            {
                set.add(type);
                return componentMap.getOrDefault(type, fallback);
            }
        });

        Objects.requireNonNull(set);
        ComponentChanges newChanges = changes.withRemovedIf(set::contains);
        this.components = newChanges.toAddedRemovedPair().added();
    }

    public final ComponentMap createComponentMap()
    {
        ComponentMap.Builder builder = ComponentMap.builder();
        builder.addAll(this.components);
        this.addComponents(builder);
        return builder.build();
    }

    public void clear()
    {
        this.loaded = false;
        this.entityId = -1;
        this.entityType = null;
        this.blockType = null;
        this.nbt.copyFrom(new NbtCompound());
        this.components = ComponentMap.EMPTY;
    }

    static class Components
    {
        public static final Codec<ComponentMap> CODEC;

        private Components() {}

        static
        {
            CODEC = ComponentMap.CODEC.optionalFieldOf(BLOCK_ENTITY_KEY, ComponentMap.EMPTY).codec();
        }
    }

    public interface ComponentsAccess
    {
        @Nullable
        <T> T get(ComponentType<T> type);

        <T> T getOrDefault(ComponentType<? extends T> type, T fallback);
    }
}
