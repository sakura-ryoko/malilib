package fi.dy.masa.malilib.interfaces;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Basic runAsync() task handler structure --
 * This is meant to be extended and managed by {@link IAsyncThreadExecutor}
 */
public abstract class AsyncThreadTaskBase
{
	private final AtomicBoolean finished = new AtomicBoolean(false);

	/**
	 * Check if the task is marked as "finished"
	 * @return (bool)
	 */
	public boolean isFinished()
	{
		return this.finished.get();
	}

	/**
	 * Mark the task as finished.
	 */
	public void finish()
	{
		this.finished.set(true);
	}

	/**
	 * Run the task {@link CompletableFuture}
	 * @return (null)
	 */
	public abstract CompletableFuture<Void> runTask();
}
