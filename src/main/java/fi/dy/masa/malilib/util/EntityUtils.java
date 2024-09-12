package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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
}
