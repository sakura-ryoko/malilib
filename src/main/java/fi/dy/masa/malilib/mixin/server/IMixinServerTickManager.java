package fi.dy.masa.malilib.mixin.server;

import net.minecraft.server.ServerTickRateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerTickRateManager.class)
public interface IMixinServerTickManager
{
    @Accessor("remainingSprintTicks")
    long malilib_getSprintTicks();
}
