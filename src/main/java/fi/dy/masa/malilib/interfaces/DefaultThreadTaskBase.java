package fi.dy.masa.malilib.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Basic run() task handler structure --
 * This is meant to be extended and managed by {@link IThreadDaemonExecutor}
 * -
 * NOTE: Default tasks are meant to run in a proper sequence; ie; "0, 1, 2, 3, 4"
 */

public abstract class DefaultThreadTaskBase implements IThreadTaskBase, Runnable
{
	private final AtomicBoolean finished = new AtomicBoolean(false);

	/**
	 * Check if the task is marked as "finished"
	 * @return (bool)
	 */
	@Override
	public boolean isFinished()
	{
		return this.finished.get();
	}

	/**
	 * Mark the task as finished.
	 */
	@Override
	public void finish()
	{
		this.finished.set(true);
	}

	/**
	 * Run the task using {@link Runnable}
	 */
	@Override
	public abstract void run();
}
