package fi.dy.masa.malilib.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Basic run() task handler structure --
 * This is meant to be extended and managed by {@link IThreadDaemonExecutor}
 * -
 * NOTE: Default tasks are meant to run in a proper sequence; ie; "0, 1, 2, 3, 4"
 */

public abstract class DefaultThreadTaskBase extends AbstractThreadTaskBase implements Runnable
{
	/**
	 * Run the task using {@link Runnable}
	 */
	@Override
	public abstract void run();
}
