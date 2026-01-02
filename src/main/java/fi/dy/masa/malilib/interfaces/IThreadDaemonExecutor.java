package fi.dy.masa.malilib.interfaces;

/**
 * This interface is for creating a Thread Executor class --
 * - The thread's main loop via {@link Runnable}
 * @param <T> {@link IThreadTaskBase}
 */
public interface IThreadDaemonExecutor<T extends IThreadTaskBase>
		extends Runnable
{
	/**
	 * Return the "Running" status of the Thread (Use an {@link java.util.concurrent.atomic.AtomicBoolean})
	 * @return ()
	 */
	boolean isRunning();

	/**
	 * Starts the Running process.
	 */
	void start();

	/**
	 * Stops the running process
	 */
	void stop();

	/**
	 * Executes a task that is polled by the {@link java.util.Queue}
	 * @param task {@link IThreadTaskBase}
	 * @throws InterruptedException ()
	 */
	void processTask(T task) throws InterruptedException;
}
