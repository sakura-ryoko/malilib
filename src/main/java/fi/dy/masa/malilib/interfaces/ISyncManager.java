package fi.dy.masa.malilib.interfaces;

public interface ISyncManager
{
    void registerSyncProvider(ISyncProvider handler);

    void unregisterSyncProvider(ISyncProvider handler);
}
