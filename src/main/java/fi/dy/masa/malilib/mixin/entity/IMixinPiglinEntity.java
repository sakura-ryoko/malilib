package fi.dy.masa.malilib.mixin.entity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Piglin.class)
public interface IMixinPiglinEntity
{
    @Accessor("inventory")
    SimpleContainer malilib_getInventory();
}
