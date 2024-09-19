package fi.dy.masa.malilib.sync.fbe;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakePatterns extends FakeNamed
{
    @Nullable
    private BannerPatternsComponent patterns;

    public FakePatterns(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FakePatterns(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        this.setWorld(world);
        this.copyFromBlockEntity(be, world.getRegistryManager());
    }

    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakePatterns(BlockEntityType.BANNER, pos, state);
    }

    public @Nullable BannerPatternsComponent getPatterns()
    {
        return this.patterns;
    }

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.writeNbt(nbt, registries);
        if (this.patterns == null)
        {
            this.patterns = BannerPatternsComponent.DEFAULT;
        }
        if (!this.patterns.equals(BannerPatternsComponent.DEFAULT))
        {
            nbt.put("patterns", BannerPatternsComponent.CODEC.encodeStart(registries.getOps(NbtOps.INSTANCE), this.patterns).getOrThrow());
        }

        if (this.getCustomName() != null)
        {
            nbt.putString("CustomName", Text.Serialization.toJsonString(this.getCustomName(), registries));
        }
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        super.readNbt(nbt, registries);
        if (nbt.contains("CustomName", 8))
        {
            this.setCustomName(tryParseCustomName(nbt.getString("CustomName"), registries));
        }

        if (nbt.contains("patterns"))
        {
            BannerPatternsComponent.CODEC.parse(registries.getOps(NbtOps.INSTANCE), nbt.get("patterns")).resultOrPartial((patterns) ->
                                 LOGGER.error("Failed to parse banner patterns: '{}'", patterns)).ifPresent((patterns) ->
                                this.patterns = patterns);
        }
    }

    protected void readComponents(FakeBlockEntity.ComponentsAccess components)
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
