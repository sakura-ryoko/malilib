package fi.dy.masa.malilib.util;

import java.util.UUID;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import fi.dy.masa.malilib.util.nbt.NbtEntityUtils;

/**
 * Consider Migrating to util/nbt/NbtEntityUtils
 */
public class EntityUtils extends NbtEntityUtils
{
    /**
     * Returns the camera entity, if it's not null, otherwise returns the client player entity.
     *
     * @return ()
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
     * Returns if the Entity has a Turtle Helmet equipped
     *
     * @param player (The Player)
     * @return (True / False)
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

    /**
     * Fake "LeashData" record.  To change the values, just make a new one.
     *
     * @param unresolvedLeashHolderId
     * @param leashHolder
     * @param unresolvedLeashData
     */
    public record FakeLeashData(int unresolvedLeashHolderId, @Nullable Entity leashHolder, @Nullable Either<UUID, BlockPos> unresolvedLeashData) {}
}
