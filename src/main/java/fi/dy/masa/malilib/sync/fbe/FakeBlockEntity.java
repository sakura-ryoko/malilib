package fi.dy.masa.malilib.sync.fbe;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLib;

public class FakeBlockEntity
{
    protected final static Logger LOGGER = MaLiLib.logger;
    private BlockEntityType<?> type;
    private BlockPos pos;
    private World world;
    private BlockState state;
    private ComponentMap components;
    private NbtCompound nbt;
    private boolean loaded;

    public FakeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        this.type = type;
        this.pos = pos.toImmutable();
        this.components = ComponentMap.EMPTY;
        this.state = state;
        this.checkSupport(state);
        this.loaded = false;
    }

    public FakeBlockEntity(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        System.out.print("be -> FakeBlockEntity\n");
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public BlockEntityType<?> getType()
    {
        return this.type;
    }

    public void setType(BlockEntityType<?> type)
    {
        this.type = type;
    }

    private void checkSupport(BlockState state)
    {
        if (!this.supports(state))
        {
            String typeString = String.valueOf(Registries.BLOCK_ENTITY_TYPE.getId(this.getType()));
            LOGGER.warn("Invalid block state given for [{}]", typeString);
        }
    }

    public boolean isLoaded()
    {
        return this.loaded;
    }

    public void setLoaded(boolean loaded)
    {
        this.loaded = loaded && !this.nbt.isEmpty();
    }

    public boolean supports(BlockState state)
    {
        return this.type.supports(state);
    }

    @Nullable
    public World getWorld()
    {
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

    public BlockPos getPos()
    {
        return this.pos;
    }

    public void setPos(BlockPos pos)
    {
        this.pos = pos;
    }

    public BlockState getState()
    {
        return this.state;
    }

    public void setState(BlockState state)
    {
        this.checkSupport(state);
        this.state = state;
    }

    public ComponentMap getComponents()
    {
        return this.components;
    }

    public void setComponents(ComponentMap components)
    {
        this.components = components;
    }

    public final void read(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        this.readPos(nbt);
        this.readNbt(nbt, registry);
        FakeBlockEntity.Components.CODEC.parse(registry.getOps(NbtOps.INSTANCE), nbt).resultOrPartial((error) ->
                                  LOGGER.warn("Failed to load components: {}", error)).ifPresent((components) ->
                                        this.components = components);
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
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

    public NbtCompound getNbt()
    {
        if (this.nbt == null)
        {
            this.nbt = new NbtCompound();
        }

        return this.nbt;
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        if (this.nbt.isEmpty())
        {
            nbt.copyFrom(new NbtCompound());
        }
        else
        {
            nbt = new NbtCompound();
            nbt.copyFrom(this.nbt);
        }
    }

    public final NbtCompound createNbtWithId(RegistryWrapper.WrapperLookup registry)
    {
        NbtCompound nbt = this.createNbt(registry);
        this.writeIdToNbt(nbt);
        this.nbt.copyFrom(nbt);
        return nbt;
    }

    public final NbtCompound createBasicNbt(RegistryWrapper.WrapperLookup registry)
    {
        NbtCompound newNbt = new NbtCompound();
        this.writeNbt(newNbt, registry);
        return newNbt;
    }

    public NbtCompound createNbt(RegistryWrapper.WrapperLookup registry)
    {
        NbtCompound newNbt = new NbtCompound();
        this.writeNbt(newNbt, registry);
        FakeBlockEntity.Components.CODEC.encodeStart(registry.getOps(NbtOps.INSTANCE), this.components).resultOrPartial((snbt) ->
                    LOGGER.warn("Failed to save components: {}", snbt)).ifPresent((nbt) ->
                    newNbt.copyFrom((NbtCompound) nbt));

        return newNbt;
    }

    public void writeIdToNbt(@Nonnull NbtCompound nbt)
    {
        Identifier identifier = BlockEntityType.getId(this.type);

        if (identifier == null)
        {
            LOGGER.error("FakeBlockEntity#writeIdToNbt():  Error, invalid block entity type!");
            return;
        }

        this.putIdString(nbt, identifier.toString());
        this.putPos(nbt);
    }

    public void writeIdToNbt(@Nonnull NbtCompound nbt, @Nonnull BlockEntityType<?> type)
    {
        this.putIdString(nbt, Objects.requireNonNull(BlockEntityType.getId(type)).toString());
        this.putPos(nbt);
    }

    public void putIdString(@Nonnull NbtCompound nbt, String id)
    {
        nbt.putString("id", id);
    }

    public void putPos(@Nonnull NbtCompound nbt)
    {
        if (!nbt.contains("id"))
        {
            this.writeIdToNbt(nbt);
        }
        nbt.putInt("x", this.pos.getX());
        nbt.putInt("y", this.pos.getY());
        nbt.putInt("z", this.pos.getZ());
    }

    public void readPos(@Nonnull NbtCompound nbt)
    {
        if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z"))
        {
            this.setPos(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")).toImmutable());
        }
    }

    @Nullable
    public FakeBlockEntity createFromNbt(BlockPos pos, BlockState state, @Nonnull NbtCompound nbtIn, RegistryWrapper.WrapperLookup registry)
    {
        String idString = nbtIn.getString("id");
        Identifier id = Identifier.tryParse(idString);
        if (id == null)
        {
            LOGGER.warn("FakeBlockEntity#createFromNbt(): Invalid id given: [{}]", idString.isEmpty() ? "<EMPTY>" : idString);
            return null;
        }

        Optional<BlockEntityType<?>> opt = Registries.BLOCK_ENTITY_TYPE.getOptionalValue(id);

        if (opt.isPresent())
        {
            FakeBlockEntity be = new FakeBlockEntity(opt.get(), pos, state);

            try
            {
                be.read(nbtIn, registry);
            }
            catch (Throwable err)
            {
                LOGGER.error("FakeBlockEntity#createFromNbt(): block entity failed to inject NBT for [{}], Reason: [{}]", idString, err.getMessage());
            }

            return be;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends FakeBlockEntity> T copyFromBlockEntity(BlockEntity be, RegistryWrapper.WrapperLookup registry)
    {
        BlockEntityType<?> type = be.getType();
        BlockPos pos = be.getPos();
        BlockState state = be.getCachedState();
        World world = be.getWorld();
        ComponentMap map = be.getComponents();
        NbtCompound nbt = be.createNbtWithIdentifyingData(registry);

        FakeBlockEntity fbe = new FakeBlockEntity(type, pos, state);
        fbe.setWorld(world);
        fbe.setComponents(map);
        fbe.readNbt(nbt, registry);

        return (T) fbe;
    }

    protected void copyFromBlockEntityInternal(BlockEntity be, RegistryWrapper.WrapperLookup registry)
    {
        NbtCompound nbt = be.createNbtWithIdentifyingData(registry);
        this.setWorld(be.getWorld());
        System.out.printf("copyFromBlockEntityInternal() nbt in [%s]\n", nbt);
        this.read(nbt, registry);
        this.printComponents();
        this.setLoaded(true);
    }

    private void printComponents()
    {
        Iterator<Component<?>> iter = this.components.stream().iterator();
        int i = 0;

        System.out.print("Comps:\n");
        while (iter.hasNext())
        {
            Component<?> entry = iter.next();
            System.out.printf("comp[%d]: type [%s], value [%s]\n", i, entry.type().toString(), entry.value());
            i++;
        }
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T extends BlockEntity> T copyToBlockEntity(RegistryWrapper.WrapperLookup registry)
    {
        NbtCompound nbt = this.createNbtWithId(registry);
        var be1 = this.getType().get(this.getWorld(), this.getPos());

        // Copy to existing
        if (be1 != null)
        {
            be1.read(nbt, registry);
            return (T) be1;
        }

        // Create New
        var be2 = this.getType().instantiate(this.getPos(), this.getState());

        if (be2 != null)
        {
            be2.read(nbt, registry);
            return (T) be2;
        }

        return null;
    }


    @Nullable
    public static Text tryParseCustomName(String json, RegistryWrapper.WrapperLookup registries)
    {
        try
        {
            return Text.Serialization.fromJson(json, registries);
        }
        catch (Exception e)
        {
            LOGGER.warn("Failed to parse custom name from [{}], discarding [{}]", json, e.getMessage());
            return null;
        }
    }

    protected void readComponents(FakeBlockEntity.ComponentsAccess components)
    {
        // NO-OP
    }

    public final void readComponentsFromStack(ItemStack stack)
    {
        this.readComponentsInternal(stack.getDefaultComponents(), stack.getComponentChanges());
    }

    public void setStackNbt(ItemStack stack, RegistryWrapper.WrapperLookup registry)
    {
        NbtCompound newNbt = this.createBasicNbt(registry);
        stack.clearComponentChanges();
        BlockItem.setBlockEntityData(stack, this.getType(), newNbt);
        stack.applyComponentsFrom(this.createComponentMap());
    }

    public final void readComponentsInternal(ComponentMap defaults, ComponentChanges changes)
    {
        final Set<ComponentType<?>> set = new HashSet<>();
        final ComponentMap componentMap = MergedComponentMap.create(defaults, changes);

        set.add(DataComponentTypes.BLOCK_ENTITY_DATA);
        this.readComponents(new FakeBlockEntity.ComponentsAccess()
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

    protected void addComponents(ComponentMap.Builder builder)
    {
        // NO-OP
    }

    public final ComponentMap createComponentMap()
    {
        ComponentMap.Builder builder = ComponentMap.builder();
        builder.addAll(this.components);
        this.addComponents(builder);
        return builder.build();
    }

    public @Nullable FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return null;
    }

    public void clear()
    {
        this.loaded = false;
        this.nbt.copyFrom(new NbtCompound());
        this.components = ComponentMap.EMPTY;
    }

    static class Components
    {
        public static final Codec<ComponentMap> CODEC;

        private Components() {}

        static
        {
            CODEC = ComponentMap.CODEC.optionalFieldOf("components", ComponentMap.EMPTY).codec();
        }
    }

    public interface ComponentsAccess
    {
        @Nullable
        <T> T get(ComponentType<T> type);

        <T> T getOrDefault(ComponentType<? extends T> type, T fallback);
    }
}
