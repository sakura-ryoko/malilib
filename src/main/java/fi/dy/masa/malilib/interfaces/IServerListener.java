package fi.dy.masa.malilib.interfaces;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

public interface IServerListener
{
    /**
     * Called at the initial occurrence of a MinecraftServer is starting up
     * @param server (The MinecraftServer object)
     */
    default void onServerStarting(MinecraftServer server) {}

    /**
     * Called when the local MinecraftServer is finished starting
     * @param server (The MinecraftServer object)
     */
    default void onServerStarted(MinecraftServer server) {}

    /**
     * Called when the local MinecraftServer is configured
     * @param server (The IntegratedServer object)
     */
    default void onServerIntegratedSetup(IntegratedServer server) {}

    /**
     * Called when the local MinecraftServer is configured for "OpenToLan"
     * @param server (The IntegratedServer object)
     */
    default void onServerOpenToLan(IntegratedServer server) {}

    /**
     * Called when the local MinecraftServer enters its initial "stopping" state
     * @param server (The MinecraftServer object)
     */
    default void onServerStopping(MinecraftServer server) {}

    /**
     * Called when the local MinecraftServer finishes it's "stopped" state and before the "server" object itself is killed.
     * @param server (The MinecraftServer object)
     */
    default void onServerStopped(MinecraftServer server) {}
}
