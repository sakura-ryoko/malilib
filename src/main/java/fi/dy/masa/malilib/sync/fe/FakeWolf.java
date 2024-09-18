package fi.dy.masa.malilib.sync.fe;

import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FakeWolf extends FakeTamable implements Angerable, VariantHolder<RegistryEntry<WolfVariant>>
{
    RegistryEntry<WolfVariant> variant;
    private int collarColor;

    public FakeWolf(EntityType<?> type, World world, int entityId)
    {
        super(type, world, entityId);
        this.setTamed(false, false);
    }

    public FakeWolf(Entity input)
    {
        super(input);
        this.setTamed(false, false);

        if (input instanceof WolfEntity we)
        {
            this.setVariant(we.getVariant());
            this.buildAttributes(createWolfAttributes());
            this.readCustomDataFromNbt(this.getNbt());
        }
    }

    @Override
    public void setVariant(RegistryEntry<WolfVariant> variant)
    {
        this.variant = variant;
    }

    @Override
    public RegistryEntry<WolfVariant> getVariant()
    {
        return this.variant;
    }

    @Override
    public int getAngerTime()
    {
        return 0;
    }

    @Override
    public void setAngerTime(int angerTime)
    {
        // NO-OP
    }

    @Override
    public @Nullable UUID getAngryAt()
    {
        return null;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt)
    {
        // NO-OP
    }

    @Override
    public void chooseRandomAngerTime()
    {
        // NO-OP
    }

    @Override
    public void setAttacking(@Nullable PlayerEntity attacking)
    {
        // NO-OP
    }

    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        // NO-OP
    }

    @Override
    public boolean canTarget(LivingEntity target)
    {
        return false;
    }

    @Override
    public @Nullable LivingEntity getTarget()
    {
        return null;
    }

    public DyeColor getCollarColor()
    {
        return DyeColor.byId(this.collarColor);
    }

    public void setCollarColor(DyeColor color)
    {
        this.collarColor = color.getId();
    }

    public static DefaultAttributeContainer.Builder createWolfAttributes()
    {
        return FakeAnimal.createAnimalAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.MAX_HEALTH, 8.0).add(EntityAttributes.ATTACK_DAMAGE, 4.0);
    }

    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        nbt.putByte("CollarColor", (byte) this.getCollarColor().getId());
        this.getVariant().getKey().ifPresent((registryKey) -> nbt.putString("variant", registryKey.getValue().toString()));
        this.writeAngerToNbt(nbt);
        super.writeCustomDataToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        Optional.ofNullable(Identifier.tryParse(nbt.getString("variant"))).map((variantId) ->
               RegistryKey.of(RegistryKeys.WOLF_VARIANT, variantId)).flatMap((variantKey) ->
                         this.getRegistryManager().getOrThrow(RegistryKeys.WOLF_VARIANT).getOptional(variantKey)).ifPresent(this::setVariant);
        if (nbt.contains("CollarColor", 99))
        {
            this.setCollarColor(DyeColor.byId(nbt.getInt("CollarColor")));
        }

        this.readAngerFromNbt(this.getWorld(), nbt);
    }
}
