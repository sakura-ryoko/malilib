package fi.dy.masa.malilib.sync.fbe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.math.BlockPos;

import fi.dy.masa.malilib.sync.fc.IFakeBlockProvider;

public class FakePatterns extends FakeNamed implements IFakeBlockProvider
{
    @Nullable
    private BannerPatternsComponent patterns;

    public FakePatterns(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakePatterns(BlockEntityType.BANNER, pos, state);
    }

    public @Nullable BannerPatternsComponent getPatterns()
    {
        return this.patterns;
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
