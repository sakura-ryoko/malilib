package fi.dy.masa.malilib.util.thread;

import fi.dy.masa.malilib.interfaces.IThreadDaemonExecutor;
import fi.dy.masa.malilib.interfaces.IThreadTaskBase;

public record ThreadExecutorPair<T extends IThreadTaskBase>(Thread thread, IThreadDaemonExecutor<T> executor)
{
}
