package fi.dy.masa.malilib.sync.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SyncPatterns extends SyncData
{
    private static final String PATTERNS_KEY = "patterns";
    private @Nullable BannerPatternsComponent patterns;

    public SyncPatterns(BlockEntityType<?> type, BlockPos pos, BlockState state, World world)
    {
        super(type, pos, state, world);
        this.patterns = BannerPatternsComponent.DEFAULT;
    }

    public SyncPatterns(BlockEntity be)
    {
        this(be.getType(), be.getPos(), be.getCachedState(), be.getWorld());
        this.copyNbtFromBlockEntity(be);
        //this.readCustomDataFromNbt(this.getNbt());
    }

    protected void initInventory()
    {
        // NO-OP
    }

    protected void initAttributes(Entity entity)
    {
        // NO-OP
    }

    public @Nullable BannerPatternsComponent getPatterns()
    {
        return this.patterns;
    }

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.readCustomDataFromNbt(nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        if (nbt.contains(PATTERNS_KEY))
        {
            BannerPatternsComponent.CODEC.parse(this.getRegistryManager().getOps(NbtOps.INSTANCE), nbt.get(PATTERNS_KEY)).resultOrPartial((patterns) ->
                                    LOGGER.error("Failed to parse banner patterns: '{}'", patterns)).ifPresent((patterns) -> this.patterns = patterns);
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        if (this.patterns == null)
        {
            this.patterns = BannerPatternsComponent.DEFAULT;
        }
        if (!this.patterns.equals(BannerPatternsComponent.DEFAULT))
        {
            nbt.put(PATTERNS_KEY, BannerPatternsComponent.CODEC.encodeStart(this.getRegistryManager().getOps(NbtOps.INSTANCE), this.patterns).getOrThrow());
        }
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.patterns = components.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.BANNER_PATTERNS, this.patterns);
    }
}
