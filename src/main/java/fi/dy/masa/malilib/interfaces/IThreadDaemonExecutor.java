package fi.dy.masa.malilib.interfaces;

import fi.dy.masa.malilib.MaLiLib;

/**
 * This interface is for creating a Thread Executor class --
 * - The thread's main loop via {@link Runnable}
 * @param <T> {@link IThreadTaskBase}
 */
public interface IThreadDaemonExecutor<T extends IThreadTaskBase> extends Runnable
{
	/**
	 * Set the Max Sleep time.
	 * @return -
	 */
	long sleepTime();

	/**
	 * Get this Threads' Prefix name, so we can match it using 'isCorrectThread'
	 * @return -
	 */
	String getName();

	/**
	 * Return the "Running" status of the Thread (Use an {@link java.util.concurrent.atomic.AtomicBoolean})
	 * @return ()
	 */
	boolean isRunning();

	/**
	 * Return the "Paused" status of the Thread (Use an {@link java.util.concurrent.atomic.AtomicBoolean})
	 * @return -
	 */
	boolean isPaused();

	/**
	 * Starts the Running process.
	 */
	void start();

	/**
	 * Stops the running process
	 */
	void stop();

	/**
	 * Temporarily Pause execution.
	 */
	void pause();

	/**
	 * Resume from Pause.
	 */
	void resume();

	/**
	 * Return if the Handler has tasks to process.
	 * @return ()
	 */
	boolean hasTasks();

	/**
	 * Run a "Safe" Loop, and return if it should sleep if there are no Tasks to run.
	 * @return ()
	 */
	boolean loopSafe();

	/**
	 * Send the Executor the "interrupt" signal.
	 * @param interrupt ()
	 */
	void interrupt(InterruptedException interrupt);

	/**
	 * Executes a task that is polled by the {@link java.util.Queue}
	 * @param task {@link IThreadTaskBase}
	 * @throws InterruptedException ()
	 */
	void processTask(T task) throws InterruptedException;

	/**
	 * Return if the current thread is correct, and not randomly being called from Minecraft's Rendering Thread.
	 * @return ()
	 */
	default boolean isCorrectThread()
	{
		return Thread.currentThread().getName().toLowerCase().contains(this.getName().toLowerCase());
	}

	/**
	 * Sleeps the current running thread, if we are on the Correct Thread, and if it is Running.
	 */
	default void sleep()
	{
		if (this.isCorrectThread() && this.isRunning())
		{
			try
			{
				if (!this.isPaused())
				{
					this.pause();
				}

				MaLiLib.debugLog("Executor: sleeping: '{}' for [{}]", this.getName(), this.sleepTime());
				Thread.sleep(this.sleepTime());
			}
			catch (InterruptedException e)
			{
				this.interrupt(e);
			}
			finally
			{
				this.resume();
			}
		}
	}
}
