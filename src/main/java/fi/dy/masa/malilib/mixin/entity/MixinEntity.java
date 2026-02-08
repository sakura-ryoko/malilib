package fi.dy.masa.malilib.mixin.entity;

import java.util.*;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
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

@Mixin(value = Entity.class, priority = 999)
public abstract class MixinEntity implements INbtEntityInvoker
{
    @Shadow @Final private EntityType<?> type;
    @Shadow private int id;
    @Shadow @Final private static Codec<List<String>> TAG_LIST_CODEC;
    @Shadow private @Nullable Entity vehicle;
    @Shadow protected UUID uuid;
    @Shadow private Level level;
    @Shadow private Vec3 position;
    @Shadow private Vec3 deltaMovement;
    @Shadow private float yRot;
    @Shadow private float xRot;
    @Shadow private boolean onGround;
    @Shadow public double fallDistance;
    @Shadow private int remainingFireTicks;
    @Shadow @Final protected SynchedEntityData entityData;
    @Shadow @Final private static EntityDataAccessor<@NotNull Integer> DATA_AIR_SUPPLY_ID;
    @Shadow @Final private static EntityDataAccessor<@NotNull Optional<Component>> DATA_CUSTOM_NAME;
    @Shadow @Final private static EntityDataAccessor<@NotNull Boolean> DATA_CUSTOM_NAME_VISIBLE;
    @Shadow @Final private static EntityDataAccessor<@NotNull Boolean> DATA_SILENT;
    @Shadow @Final private static EntityDataAccessor<@NotNull Boolean> DATA_NO_GRAVITY;
    @Shadow @Final private static EntityDataAccessor<@NotNull Integer> DATA_TICKS_FROZEN;
    @Shadow private int portalCooldown;
    @Shadow private boolean invulnerable;
    @Shadow private boolean hasGlowingTag;
    @Shadow private boolean hasVisualFire;
    @Shadow @Final private Set<String> tags;
    @Shadow private CustomData customData;
    @Shadow protected abstract void addAdditionalSaveData(ValueOutput view);

    @Unique
    private Optional<CompoundTag> malilib$gatherPassengerlessNbtInternal(final int expectedId)
    {
        if (this.id != expectedId)
        {
            return Optional.empty();
        }

        try
        {
            CompoundTag nbt = new CompoundTag();

            if (this.vehicle != null)
            {
                nbt.store(NbtKeys.POS, Vec3.CODEC, new Vec3(this.vehicle.getX(), this.position.y(), this.vehicle.getZ()));
            }
            else
            {
                nbt.store(NbtKeys.POS, Vec3.CODEC, this.position);
            }

            nbt.store(NbtKeys.MOTION, Vec3.CODEC, this.deltaMovement);
            nbt.store(NbtKeys.ROTATION, Vec2.CODEC, new Vec2(this.yRot, this.xRot));
            nbt.putDouble(NbtKeys.FALL_DISTANCE, this.fallDistance);
            nbt.putShort(NbtKeys.FIRE, (short) this.remainingFireTicks);
            nbt.putShort(NbtKeys.AIR, this.entityData.get(DATA_AIR_SUPPLY_ID).shortValue());
            nbt.putBoolean(NbtKeys.ON_GROUND, this.onGround);
            nbt.putBoolean(NbtKeys.INVULNERABLE, this.invulnerable);
            nbt.putInt(NbtKeys.PORTAL_COOLDOWN, this.portalCooldown);
            nbt.store(NbtKeys.UUID, UUIDUtil.CODEC, this.uuid);

            this.entityData.get(DATA_CUSTOM_NAME).ifPresent(name -> nbt.store(NbtKeys.CUSTOM_NAME, ComponentSerialization.CODEC, name));

            if (this.entityData.get(DATA_CUSTOM_NAME_VISIBLE))
            {
                nbt.putBoolean(NbtKeys.CUSTOM_NAME_VISIBLE, true);
            }

            if (this.entityData.get(DATA_SILENT))
            {
                nbt.putBoolean(NbtKeys.SILENT, true);
            }

            if (this.entityData.get(DATA_NO_GRAVITY))
            {
                nbt.putBoolean(NbtKeys.NO_GRAVITY, true);
            }

            if (this.hasGlowingTag)
            {
                nbt.putBoolean(NbtKeys.GLOWING, true);
            }

            int i = this.entityData.get(DATA_TICKS_FROZEN);

            if (i > 0)
            {
                nbt.putInt(NbtKeys.TICKS_FROZEN, i);
            }

            if (this.hasVisualFire)
            {
                nbt.putBoolean(NbtKeys.HAS_VISUAL_FIRE, true);
            }

            if (!this.tags.isEmpty())
            {
                nbt.store(NbtKeys.COMMAND_TAGS, TAG_LIST_CODEC, List.copyOf(this.tags));
            }

            if (!this.customData.isEmpty())
            {
                nbt.store(NbtKeys.CUSTOM_DATA, CustomData.CODEC, this.customData);
            }

            // Ignore Passengers
            NbtView view = NbtView.getWriter(this.level.registryAccess());

            this.addAdditionalSaveData(view.getWriter());
            nbt.merge(Objects.requireNonNullElse(view.readNbt(), new CompoundTag()));
            nbt.putString(NbtKeys.ID, EntityType.getKey(this.type).toString());

            return Optional.of(nbt);
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("malilib$getNbtDataWithId: Exception writing NBT tags for entityId [{}]; Exception: {}", expectedId, err.getLocalizedMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<CompoundTag> malilib$getNbtDataWithId(int expectedId)
    {
        return this.malilib$gatherPassengerlessNbtInternal(expectedId);
    }

	@Override
	public Optional<CompoundData> malilib$getDataTagWithId(int expectedId)
	{
		return this.malilib$gatherPassengerlessNbtInternal(expectedId).map(DataConverterNbt::fromVanillaCompound);
	}
}
