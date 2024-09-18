package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import fi.dy.masa.malilib.sync.fe.*;

public class EntityUtils
{
    /**
     * Returns the camera entity, if it's not null, otherwise returns the client player entity.
     * @return
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity entity = mc.getCameraEntity();

        if (entity == null)
        {
            entity = mc.player;
        }

        return entity;
    }

    /**
     * Returns weather or not the Entity has a Turtle Helmet equipped
     * @param player (The Player)
     * @return (True/False)
     */
    public static boolean hasTurtleHelmetEquipped(PlayerEntity player)
    {
        if (player == null)
        {
            return false;
        }

        ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);

        return !stack.isEmpty() && stack.isOf(Items.TURTLE_HELMET);
    }

    @SuppressWarnings("unchecked")
    public static <T extends FakeEntity> T toFakeEntity(Entity e)
    {
        if (e instanceof ZombieVillagerEntity)
        {
            return (T) new FakeZombieVillager(e);
        }
        if (e instanceof ZombieEntity)
        {
            return (T) new FakeZombie(e);
        }
        if (e instanceof AbstractPiglinEntity)
        {
            return (T) new FakePiglin(e);
        }
        if (e instanceof HostileEntity)
        {
            return (T) new FakeHostile(e);
        }
        if (e instanceof VillagerEntity)
        {
            return (T) new FakeVillager(e);
        }
        if (e instanceof LlamaEntity)
        {
            return (T) new FakeLlama(e);
        }
        if (e instanceof AbstractHorseEntity)
        {
            return (T) new FakeHorse(e);
        }
        if (e instanceof MerchantEntity)
        {
            return (T) new FakeMerchant(e);
        }
        if (e instanceof TameableEntity)
        {
            return (T) new FakeTamable(e);
        }
        if (e instanceof AnimalEntity)
        {
            return (T) new FakeAnimal(e);
        }
        if (e instanceof PassiveEntity)
        {
            return (T) new FakePassive(e);
        }
        if (e instanceof PaintingEntity)
        {
            return (T) new FakePainting(e);
        }
        if (e instanceof AbstractDecorationEntity)
        {
            return (T) new FakeDecoration(e);
        }
        if (e instanceof BlockAttachedEntity)
        {
            return (T) new FakeAttached(e);
        }
        if (e instanceof MobEntity)
        {
            return (T) new FakeMob(e);
        }
        if (e instanceof LivingEntity)
        {
            return (T) new FakeLiving(e);
        }
        if (e instanceof ItemEntity)
        {
            return (T) new FakeItemEntity(e);
        }

        return (T) new FakeEntity(e);
    }
}
