package fi.dy.masa.malilib.interfaces;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Basic runAsync() task handler structure --
 * This is meant to be extended and managed by {@link IThreadDaemonExecutor}
 * -
 * NOTE: Async tasks can often run "out of sequence", such as "3, 0, 2, 1, 4"; and the
 * use of {@link CompletableFuture} utilizes the default {@link java.util.concurrent.ForkJoinPool}.
 */
public abstract class AsyncThreadTaskBase extends AbstractThreadTaskBase
{
	/**
	 * Run the task {@link CompletableFuture}
	 * @return (null)
	 */
	@Override
	public abstract CompletableFuture<Void> runAsync();
}
