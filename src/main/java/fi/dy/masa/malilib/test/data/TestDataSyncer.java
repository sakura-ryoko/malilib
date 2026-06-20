package fi.dy.masa.malilib.test.data;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.interfaces.IDataSyncer;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.malilib.util.data_syncer.EntityDataCache;
import fi.dy.masa.malilib.util.data_syncer.EntityDataRequestTracker;

@ApiStatus.Experimental
public class TestDataSyncer implements IDataSyncer, IClientTickHandler
{
    public static final TestDataSyncer INSTANCE = new TestDataSyncer();
    private final EntityDataCache cache;
    private final EntityDataRequestTracker requestTracker;
    private final static float TICK_RATE = 0.75f;
    private long lastTick;

    public TestDataSyncer()
    {
        this.cache = new EntityDataCache(MaLiLibReference.MOD_ID, 2500L);
        this.requestTracker = new EntityDataRequestTracker();
        this.lastTick = System.currentTimeMillis();

        if (MaLiLibReference.DEBUG_MODE)
        {
            Registry.ENTITY_DATA_REGISTRY.registerEntityDataCache(this.cache);
        }
    }

    @Override
    public EntityDataCache getCache()
    {
        return this.cache;
    }

    @Override
    public EntityDataRequestTracker getRequestTracker()
    {
        return this.requestTracker;
    }

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public boolean isBackupEnabled()
    {
        return false;
    }

    @Override
    public long getRefreshTime()
    {
        return 150L;
    }

    @Override
    public long getCacheTimeout()
    {
        return 2500L;
    }

    @Override
    public boolean loadContainerBlockEntities()
    {
        return true;
    }

    @Override
    public Level getBestWorld()
    {
        return WorldUtils.getBestWorld(Minecraft.getInstance());
    }

    private long tickRate()
    {
        return (long) (TICK_RATE * 1000L);
    }

    @Override
    public void onClientTick(Minecraft mc)
    {
        final long now = System.currentTimeMillis();

        if ((now - this.lastTick) > this.tickRate())
        {
            this.cache.tickCache(now);
            this.lastTick = now;
        }
    }
}
