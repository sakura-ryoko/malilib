package fi.dy.masa.malilib.sync.fbe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import fi.dy.masa.malilib.sync.fc.IFakeBlockProvider;

public class FakeNamed extends FakeBlockEntity implements IFakeBlockProvider
{
    @Nullable
    private Text customName;

    public FakeNamed(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeNamed(BlockEntityType.ENCHANTING_TABLE, pos, state);
    }

    @Nullable
    public Text getCustomName()
    {
        return this.customName;
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
    }
}
