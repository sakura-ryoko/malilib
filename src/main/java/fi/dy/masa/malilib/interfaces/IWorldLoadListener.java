package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;

public interface IWorldLoadListener
{
    /**
     * Called before the IntegratedServer is launched just after the inital reading of the World Save Loader
     * @param immutable (Immutable Combined Dynamic Registry Manager)
     */
    default void onWorldLoadImmutable(DynamicRegistryManager.Immutable immutable) {}

    /**
     * Called when the client world is going to be changed,
     * before the reference has been changed
     * @param worldBefore the old world reference, before the new one gets assigned
     * @param worldAfter the new world reference that is going to get assigned
     * @param mc
     */
    default void onWorldLoadPre(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc) {}

    /**
     * Called after the client world reference has been changed
     * @param worldBefore the old world reference, before the new one gets assigned
     * @param worldAfter the new world reference that is going to get assigned
     * @param mc
     */
    default void onWorldLoadPost(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc) {}
}
