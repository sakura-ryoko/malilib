package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface IMixinPlayerEntity
{
    @Accessor("enderChestInventory")
    PlayerEnderChestContainer malilib_getEnderItems();
}
