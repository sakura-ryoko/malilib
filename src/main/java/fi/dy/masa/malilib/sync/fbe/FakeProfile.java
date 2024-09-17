package fi.dy.masa.malilib.sync.fbe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class FakeProfile extends FakeNamed
{
    @Nullable
    private ProfileComponent owner;
    @Nullable
    private Identifier noteBlockSound;

    public FakeProfile(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FakeBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new FakeProfile(BlockEntityType.SKULL, pos, state);
    }

    @Nullable
    public ProfileComponent getOwner()
    {
        return this.owner;
    }

    public void setOwner(@Nullable ProfileComponent profile)
    {
        this.owner = profile;
    }

    protected void readComponents(ComponentsAccess components)
    {
        super.readComponents(components);
        this.setOwner(components.get(DataComponentTypes.PROFILE));
        this.noteBlockSound = components.get(DataComponentTypes.NOTE_BLOCK_SOUND);
    }

    protected void addComponents(ComponentMap.Builder builder)
    {
        super.addComponents(builder);
        builder.add(DataComponentTypes.PROFILE, this.owner);
        builder.add(DataComponentTypes.NOTE_BLOCK_SOUND, this.noteBlockSound);
    }
}
