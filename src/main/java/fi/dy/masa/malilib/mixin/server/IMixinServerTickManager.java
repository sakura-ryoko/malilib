package fi.dy.masa.malilib.mixin.server;

import net.minecraft.server.ServerTickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerTickManager.class)
public interface IMixinServerTickManager
{
    @Accessor("sprintTicks")
    long malilib_getSprintTicks();
}
