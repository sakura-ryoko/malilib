package fi.dy.masa.malilib.event;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;

public class WorldLoadHandler implements IWorldLoadManager
{
    private static final WorldLoadHandler INSTANCE = new WorldLoadHandler();

    private final List<IWorldLoadListener> worldLoadPreHandlers = new ArrayList<>();
    private final List<IWorldLoadListener> worldLoadPostHandlers = new ArrayList<>();

    public static IWorldLoadManager getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void registerWorldLoadPreHandler(IWorldLoadListener listener)
    {
        if (this.worldLoadPreHandlers.contains(listener) == false)
        {
            this.worldLoadPreHandlers.add(listener);
        }
    }

    @Override
    public void unregisterWorldLoadPreHandler(IWorldLoadListener listener)
    {
        this.worldLoadPreHandlers.remove(listener);
    }

    @Override
    public void registerWorldLoadPostHandler(IWorldLoadListener listener)
    {
        if (this.worldLoadPostHandlers.contains(listener) == false)
        {
            this.worldLoadPostHandlers.add(listener);
        }
    }

    @Override
    public void unregisterWorldLoadPostHandler(IWorldLoadListener listener)
    {
        this.worldLoadPostHandlers.remove(listener);
    }

    @ApiStatus.Internal
    public void onWorldLoadImmutable(DynamicRegistryManager.Immutable immutable)
    {
        if (this.worldLoadPreHandlers.isEmpty() == false)
        {
            for (IWorldLoadListener listener : this.worldLoadPreHandlers)
            {
                listener.onWorldLoadImmutable(immutable);
            }
        }
    }

    @ApiStatus.Internal
    public void onWorldLoadPre(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        if (this.worldLoadPreHandlers.isEmpty() == false)
        {
            for (IWorldLoadListener listener : this.worldLoadPreHandlers)
            {
                listener.onWorldLoadPre(worldBefore, worldAfter, mc);
            }
        }
    }

    @ApiStatus.Internal
    public void onWorldLoadPost(@Nullable ClientWorld worldBefore, @Nullable ClientWorld worldAfter, MinecraftClient mc)
    {
        // Save all the configs when exiting a world
        if (worldBefore != null && worldAfter == null)
        {
            ((ConfigManager) ConfigManager.getInstance()).saveAllConfigs();
            SyncHandler.getInstance().onStopServices();
        }
        // (Re-)Load all the configs from file when entering a world
        else if (worldBefore == null && worldAfter != null)
        {
            ((ConfigManager) ConfigManager.getInstance()).loadAllConfigs();
            InputEventHandler.getKeybindManager().updateUsedKeys();
            SyncHandler.getInstance().onStartServices(worldAfter);
        }

        if (this.worldLoadPostHandlers.isEmpty() == false &&
            (worldBefore != null || worldAfter != null))
        {
            for (IWorldLoadListener listener : this.worldLoadPostHandlers)
            {
                listener.onWorldLoadPost(worldBefore, worldAfter, mc);
            }
        }
    }
}
