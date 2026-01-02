package fi.dy.masa.malilib.interfaces;

import java.util.concurrent.CompletableFuture;

public interface IThreadTaskBase
{
	/**
	 * Check if the task is marked as "finished"
	 * @return (bool)
	 */
	boolean isFinished();

	/**
	 * Mark the task as finished.
	 */
	void finish();

	/**
	 * Run the task using {@link CompletableFuture}
	 * @return (null)
	 */
	default CompletableFuture<Void> runAsync()
	{
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * Run the task using {@link Runnable}
	 */
	default void run()
	{
	}
}
