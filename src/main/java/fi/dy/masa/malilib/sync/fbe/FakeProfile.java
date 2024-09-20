package fi.dy.masa.malilib.sync.fbe;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    public FakeProfile(BlockEntity be, World world)
    {
        this(be.getType(), be.getPos(), be.getCachedState());
        System.out.print("be -> FakeProfile\n");
        this.copyFromBlockEntityInternal(be, world.getRegistryManager());
    }

    public FakeProfile createBlockEntity(BlockPos pos, BlockState state)
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

    public void writeNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.writeNbt(nbt, registry);
        if (this.owner != null)
        {
            nbt.put("profile", ProfileComponent.CODEC.encodeStart(NbtOps.INSTANCE, this.owner).getOrThrow());
        }

        if (this.noteBlockSound != null)
        {
            nbt.putString("note_block_sound", this.noteBlockSound.toString());
        }

        if (this.getCustomName() != null)
        {
            nbt.putString("custom_name", Text.Serialization.toJsonString(this.getCustomName(), registry));
        }
    }

    public void readNbt(@Nonnull NbtCompound nbt, RegistryWrapper.WrapperLookup registry)
    {
        super.readNbt(nbt, registry);
        if (nbt.contains("profile"))
        {
            ProfileComponent.CODEC.parse(NbtOps.INSTANCE, nbt.get("profile")).resultOrPartial((string) ->
                          LOGGER.error("Failed to load profile from player head: {}", string)).ifPresent(this::setOwner);
        }

        if (nbt.contains("note_block_sound", 8))
        {
            this.noteBlockSound = Identifier.tryParse(nbt.getString("note_block_sound"));
        }

        if (nbt.contains("custom_name", 8))
        {
            this.setCustomName(tryParseCustomName(nbt.getString("custom_name"), registry));
        }
        else
        {
            this.setCustomName(null);
        }
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
