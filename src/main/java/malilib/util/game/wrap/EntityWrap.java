package malilib.util.game.wrap;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import malilib.MaLiLib;
import malilib.util.MathUtils;
import malilib.util.data.Constants;
import malilib.util.data.tag.CompoundData;
import malilib.util.data.tag.ListData;
import malilib.util.data.tag.converter.DataConverterNbt;
import malilib.util.inventory.InventoryUtils;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import malilib.util.position.Vec3d;

public class EntityWrap
{
    public static BlockPos getCameraEntityBlockPos()
    {
        Entity entity = GameWrap.getCameraEntity();
        return entity != null ? getEntityBlockPos(entity) : BlockPos.ORIGIN;
    }

    public static Vec3d getCameraEntityPosition()
    {
        Entity entity = GameWrap.getCameraEntity();
        return entity != null ? getEntityPos(entity) : Vec3d.ZERO;
    }

    public static BlockPos getPlayerBlockPos()
    {
        Entity entity = GameWrap.getClientPlayer();
        return entity != null ? getEntityBlockPos(entity) : BlockPos.ORIGIN;
    }

    public static Vec3d getEntityPos(Entity entity)
    {
        return new Vec3d(getX(entity), getY(entity), getZ(entity));
    }

    public static float getEyeHeight(Entity entity)
    {
        return entity.getEyeHeight();
    }

    public static Vec3d getEntityEyePos(Entity entity)
    {
        return new Vec3d(getX(entity), getY(entity) + getEyeHeight(entity), getZ(entity));
    }

    public static BlockPos getEntityBlockPos(Entity entity)
    {
        return new BlockPos(MathUtils.floor(getX(entity)),
                            MathUtils.floor(getY(entity)),
                            MathUtils.floor(getZ(entity)));
    }

    public static Vec3d getScaledLookVector(Entity entity, double range)
    {
        return MathUtils.getRotationVector(getYaw(entity), getPitch(entity)).scale(range);
    }

    public static double getX(Entity entity)
    {
        return entity.posX;
    }

    public static double getY(Entity entity)
    {
        return entity.posY;
    }

    public static double getZ(Entity entity)
    {
        return entity.posZ;
    }

    public static UUID getUuid(Entity entity)
    {
        return entity.getUniqueID();
    }

    public static float getYaw(Entity entity)
    {
        return entity.rotationYaw;
    }

    public static float getPitch(Entity entity)
    {
        return entity.rotationPitch;
    }

    public static double lerpX(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastTickPosX;
        return lastTickPos + (getX(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpY(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastTickPosY;
        return lastTickPos + (getY(entity) - lastTickPos) * tickDelta;
    }

    public static double lerpZ(Entity entity, float tickDelta)
    {
        double lastTickPos = entity.lastTickPosZ;
        return lastTickPos + (getZ(entity) - lastTickPos) * tickDelta;
    }

    public static float lerpPitch(Entity entity, float tickDelta)
    {
        float lastTickPitch = entity.prevRotationPitch;
        return lastTickPitch + (getPitch(entity) - lastTickPitch) * tickDelta;
    }

    public static float lerpYaw(Entity entity, float tickDelta)
    {
        float lastTickYaw = entity.prevRotationYaw;
        return lastTickYaw + (getYaw(entity) - lastTickYaw) * tickDelta;
    }

    public static int getChunkX(Entity entity)
    {
        return MathUtils.floor(getX(entity) / 16.0);
    }

    public static int getChunkY(Entity entity)
    {
        return MathUtils.floor(getY(entity) / 16.0);
    }

    public static int getChunkZ(Entity entity)
    {
        return MathUtils.floor(getZ(entity) / 16.0);
    }

    public static void setYaw(Entity entity, float yaw)
    {
        entity.rotationYaw = yaw;
    }

    public static void setPitch(Entity entity, float pitch)
    {
        entity.rotationPitch = pitch;
    }

    public static Direction getClosestHorizontalLookingDirection(Entity entity)
    {
        //return Direction.fromAngle(EntityWrap.getYaw(entity));
        return Direction.byHorizontalIndex(MathUtils.floor((EntityWrap.getYaw(entity) * 4.0F / 360.0F) + 0.5) & 3);
    }

    public static Direction getClosestLookingDirection(Entity entity)
    {
        return getClosestLookingDirection(entity, 60F);
    }

    /**
     * @param verticalThreshold The pitch rotation angle over which the up or down direction is preferred over the horizontal directions
     * @return the closest direction the entity is currently looking at.
     */
    public static Direction getClosestLookingDirection(Entity entity, float verticalThreshold)
    {
        float pitch = EntityWrap.getPitch(entity);

        if (pitch > verticalThreshold)
        {
            return Direction.DOWN;
        }
        else if (-pitch > verticalThreshold)
        {
            return Direction.UP;
        }

        return getClosestHorizontalLookingDirection(entity);
    }

    public static ItemStack getMainHandItem(EntityLivingBase entity)
    {
        return getHeldItem(entity, EnumHand.MAIN_HAND);
    }

    public static ItemStack getOffHandItem(EntityLivingBase entity)
    {
        return getHeldItem(entity, EnumHand.OFF_HAND);
    }

    public static ItemStack getHeldItem(EntityLivingBase entity, EnumHand hand)
    {
        return entity.getHeldItem(hand);
    }

    /**
     * Checks if the requested item is currently in the entity's hand such that it would be used for using/placing.
     * This means, that it must either be in the main hand, or the main hand must be empty and the item is in the offhand.
     * @param lenient if true, then NBT tags and also damage of damageable items are ignored
     */
    @Nullable
    public static EnumHand getUsedHandForItem(EntityLivingBase entity, ItemStack stack, boolean lenient)
    {
        EnumHand hand = null;
        EnumHand tmpHand = ItemWrap.isEmpty(getMainHandItem(entity)) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        ItemStack handStack = getHeldItem(entity, tmpHand);

        if ((lenient          && InventoryUtils.areItemsEqualIgnoreDurability(handStack, stack)) ||
            (lenient == false && InventoryUtils.areStacksEqual(handStack, stack)))
        {
            hand = tmpHand;
        }

        return hand;
    }

    /**
     * Creates the entity from the provided data.
     * Note: This does not spawn any of the entities in the world!
     */
    @Nullable
    private static Entity createSingleEntityFromNbt(CompoundData data, World world)
    {
        try
        {
            NBTTagCompound nbt = DataConverterNbt.toVanillaCompound(data);
            Entity entity = EntityList.createEntityFromNBT(nbt, world);

            if (entity != null)
            {
                entity.setUniqueId(UUID.randomUUID());
            }

            return entity;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Creates the entity and any possible passengers from the provided data.
     * Note: This does not spawn any of the entities in the world!
     */
    @Nullable
    public static Entity createEntityAndPassengersFromNbt(CompoundData data, World world)
    {
        Entity entity = createSingleEntityFromNbt(data, world);

        if (entity == null)
        {
            return null;
        }

        if (data.containsList("Passengers", Constants.NBT.TAG_COMPOUND))
        {
            ListData list = data.getList("Passengers", Constants.NBT.TAG_COMPOUND);
            final int size = list.size();

            for (int i = 0; i < size; ++i)
            {
                Entity passenger = createEntityAndPassengersFromNbt(list.getCompoundAt(i), world);

                if (passenger != null)
                {
                    passenger.startRiding(entity, true);
                }
            }
        }

        return entity;
    }

    public static void spawnEntityAndPassengersInWorld(Entity entity, World world)
    {
        if (world.spawnEntity(entity) && entity.isBeingRidden())
        {
            for (Entity passenger : entity.getPassengers())
            {
                passenger.setPosition(EntityWrap.getX(entity),
                                      EntityWrap.getY(entity) + entity.getMountedYOffset() + passenger.getYOffset(),
                                      EntityWrap.getZ(entity));
                spawnEntityAndPassengersInWorld(passenger, world);
            }
        }
    }

    @Nullable
    public static CompoundData writeEntityToTag(Entity entity)
    {
        try
        {
            NBTTagCompound tag = new NBTTagCompound();

            if (entity.writeToNBTOptional(tag))
            {
                return DataConverterNbt.fromVanillaCompound(tag);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Failed to write Entity {} to NBT", entity, e);
        }

        return null;
    }
}
