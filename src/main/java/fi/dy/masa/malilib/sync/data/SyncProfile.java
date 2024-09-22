package fi.dy.masa.malilib.sync.data;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.util.Constants;

public class SyncProfile extends SyncData
{
    private static final String PROFILE_KEY = "profile";
    private static final String NOTE_SOUND_KEY = "note_block_sound";
    private static final String CUSTOM_NAME_KEY = "custom_name";
    @Nullable private ProfileComponent owner;
    @Nullable private Identifier noteBlockSound;

    public SyncProfile(BlockEntityType<?> type, BlockPos pos, BlockState state, World world)
    {
        super(type, pos, state, world);
    }

    public SyncProfile(BlockEntity be)
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

    public @Nullable ProfileComponent getOwner()
    {
        return this.owner;
    }

    public void setOwner(@Nullable ProfileComponent profile)
    {
        this.owner = profile;
    }

    public void readNbt(@Nonnull NbtCompound nbt)
    {
        this.readCustomDataFromNbt(nbt);
        super.readNbt(nbt);
    }

    public void writeNbt(@Nonnull NbtCompound nbt)
    {
        this.writeCustomDataToNbt(nbt);
        super.writeNbt(nbt);
    }

    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
        if (nbt.contains(PROFILE_KEY))
        {
            ProfileComponent.CODEC.parse(NbtOps.INSTANCE, nbt.get(PROFILE_KEY)).resultOrPartial((string) ->
                             LOGGER.error("Failed to load profile from player head: {}", string)).ifPresent(this::setOwner);
        }

        if (nbt.contains(NOTE_SOUND_KEY, Constants.NBT.TAG_STRING))
        {
            this.noteBlockSound = Identifier.tryParse(nbt.getString(NOTE_SOUND_KEY));
        }

        if (nbt.contains(CUSTOM_NAME_KEY, Constants.NBT.TAG_STRING))
        {
            this.setCustomName(toCustomName(nbt.getString(CUSTOM_NAME_KEY), this.getRegistryManager()));
        }
        else
        {
            this.setCustomName(null);
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
        if (this.owner != null)
        {
            nbt.put(PROFILE_KEY, ProfileComponent.CODEC.encodeStart(NbtOps.INSTANCE, this.owner).getOrThrow());
        }

        if (this.noteBlockSound != null)
        {
            nbt.putString(NOTE_SOUND_KEY, this.noteBlockSound.toString());
        }

        if (this.getCustomName() != null)
        {
            nbt.putString(CUSTOM_NAME_KEY, fromCustomName(this.getCustomName(), this.getRegistryManager()));
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
