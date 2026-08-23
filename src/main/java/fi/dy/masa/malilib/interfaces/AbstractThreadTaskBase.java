package fi.dy.masa.malilib.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractThreadTaskBase implements IThreadTaskBase
{
	private final AtomicBoolean finished = new AtomicBoolean(false);

	/**
	 * Check if the task is marked as "finished"
	 *
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
}
