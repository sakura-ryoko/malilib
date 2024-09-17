package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.World;

import fi.dy.masa.malilib.interfaces.ISyncManager;
import fi.dy.masa.malilib.interfaces.ISyncProvider;

public class SyncHandler implements ISyncManager
{
    private static final SyncHandler INSTANCE = new SyncHandler();
    public static SyncHandler getInstance() { return INSTANCE; }

    private final List<ISyncProvider> handlers = new ArrayList<>();

    @Override
    public void registerSyncProvider(ISyncProvider handler)
    {
        if (!this.handlers.contains(handler))
        {
            this.handlers.add(handler);
            handler.onInstanceStart();
        }
    }

    @Override
    public void unregisterSyncProvider(ISyncProvider handler)
    {
        if (this.handlers.contains(handler))
        {
            handler.onInstanceStop();
            this.handlers.remove(handler);
        }
    }

    @ApiStatus.Internal
    public void onStartServices(World world)
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncProvider handler : this.handlers)
            {
                handler.onStartServices(world);
            }
        }
    }

    @ApiStatus.Internal
    public void onStopServices()
    {
        if (!this.handlers.isEmpty())
        {
            for (ISyncProvider handler : this.handlers)
            {
                handler.onStopServices();
            }
        }
    }
}
