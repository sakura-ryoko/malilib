package fi.dy.masa.malilib.mixin.entity;

import java.util.*;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.nbt.INbtEntityInvoker;
import fi.dy.masa.malilib.util.nbt.NbtKeys;
import fi.dy.masa.malilib.util.nbt.NbtView;

@Mixin(Entity.class)
public abstract class MixinEntity implements INbtEntityInvoker
{
    @Shadow @Final private EntityType<?> type;
    @Shadow private int id;
    @Shadow @Final private static Codec<List<String>> TAG_LIST_CODEC;
    @Shadow private @Nullable Entity vehicle;
    @Shadow protected UUID uuid;
    @Shadow private World world;
    @Shadow private Vec3d pos;
    @Shadow private Vec3d velocity;
    @Shadow private float yaw;
    @Shadow private float pitch;
    @Shadow private boolean onGround;
    @Shadow public double fallDistance;
    @Shadow private int fireTicks;
    @Shadow @Final protected DataTracker dataTracker;
    @Shadow @Final private static TrackedData<Integer> AIR;
    @Shadow @Final private static TrackedData<Optional<Text>> CUSTOM_NAME;
    @Shadow @Final private static TrackedData<Boolean> NAME_VISIBLE;
    @Shadow @Final private static TrackedData<Boolean> SILENT;
    @Shadow @Final private static TrackedData<Boolean> NO_GRAVITY;
    @Shadow @Final private static TrackedData<Integer> FROZEN_TICKS;
    @Shadow private int portalCooldown;
    @Shadow private boolean invulnerable;
    @Shadow private boolean glowing;
    @Shadow private boolean hasVisualFire;
    @Shadow @Final private Set<String> commandTags;
    @Shadow private NbtComponent customData;
    @Shadow protected abstract void writeCustomData(WriteView view);

    @Unique
    private Optional<NbtCompound> malilib$gatherPassengerlessNbtInternal(final int expectedId)
    {
        if (this.id != expectedId)
        {
            return Optional.empty();
        }

        try
        {
            NbtCompound nbt = new NbtCompound();

            if (this.vehicle != null)
            {
                nbt.put(NbtKeys.POS, Vec3d.CODEC, new Vec3d(this.vehicle.getX(), this.pos.getY(), this.vehicle.getZ()));
            }
            else
            {
                nbt.put(NbtKeys.POS, Vec3d.CODEC, this.pos);
            }

            nbt.put(NbtKeys.MOTION, Vec3d.CODEC, this.velocity);
            nbt.put(NbtKeys.ROTATION, Vec2f.CODEC, new Vec2f(this.yaw, this.pitch));
            nbt.putDouble(NbtKeys.FALL_DISTANCE, this.fallDistance);
            nbt.putShort(NbtKeys.FIRE, (short) this.fireTicks);
            nbt.putShort(NbtKeys.AIR, this.dataTracker.get(AIR).shortValue());
            nbt.putBoolean(NbtKeys.ON_GROUND, this.onGround);
            nbt.putBoolean(NbtKeys.INVULNERABLE, this.invulnerable);
            nbt.putInt(NbtKeys.PORTAL_COOLDOWN, this.portalCooldown);
            nbt.put(NbtKeys.UUID, Uuids.INT_STREAM_CODEC, this.uuid);

            this.dataTracker.get(CUSTOM_NAME).ifPresent(name -> nbt.put(NbtKeys.CUSTOM_NAME, TextCodecs.CODEC, name));

            if (this.dataTracker.get(NAME_VISIBLE))
            {
                nbt.putBoolean(NbtKeys.CUSTOM_NAME_VISIBLE, true);
            }

            if (this.dataTracker.get(SILENT))
            {
                nbt.putBoolean(NbtKeys.SILENT, true);
            }

            if (this.dataTracker.get(NO_GRAVITY))
            {
                nbt.putBoolean(NbtKeys.NO_GRAVITY, true);
            }

            if (this.glowing)
            {
                nbt.putBoolean(NbtKeys.GLOWING, true);
            }

            int i = this.dataTracker.get(FROZEN_TICKS);

            if (i > 0)
            {
                nbt.putInt(NbtKeys.TICKS_FROZEN, i);
            }

            if (this.hasVisualFire)
            {
                nbt.putBoolean(NbtKeys.HAS_VISUAL_FIRE, true);
            }

            if (!this.commandTags.isEmpty())
            {
                nbt.put(NbtKeys.COMMAND_TAGS, TAG_LIST_CODEC, List.copyOf(this.commandTags));
            }

            if (!this.customData.isEmpty())
            {
                nbt.put(NbtKeys.CUSTOM_DATA, NbtComponent.CODEC, this.customData);
            }

            // Ignore Passengers
            NbtView view = NbtView.getWriter(this.world.getRegistryManager());

            this.writeCustomData(view.getWriter());
            nbt.copyFrom(Objects.requireNonNullElse(view.readNbt(), new NbtCompound()));
            nbt.putString(NbtKeys.ID, EntityType.getId(this.type).toString());

            return Optional.of(nbt);
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("malilib$getNbtDataWithId: Exception writing NBT tags for entityId [{}]; Exception: {}", expectedId, err.getLocalizedMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<NbtCompound> malilib$getNbtDataWithId(int expectedId)
    {
        return this.malilib$gatherPassengerlessNbtInternal(expectedId);
    }

	@Override
	public Optional<CompoundData> malilib$getDataTagWithId(int expectedId)
	{
		return this.malilib$gatherPassengerlessNbtInternal(expectedId).map(DataConverterNbt::fromVanillaCompound);
	}
}
