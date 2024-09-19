package fi.dy.masa.malilib.sync.fbe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeNamed extends FakeBlockEntity
{
    @Nullable
    private Text customName;

    public FakeNamed(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FakeNamed(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        this.setWorld(world);
        this.copyFromBlockEntity(be, world.getRegistryManager());
    }

    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeNamed(BlockEntityType.ENCHANTING_TABLE, pos, state);
    }

    @Nullable
    public Text getCustomName()
    {
        return this.customName;
    }

    public void setCustomName(@Nullable Text name)
    {
        this.customName = name;
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
