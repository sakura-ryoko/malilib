package fi.dy.masa.malilib.interfaces;

/**
 * Extend this to create a "Daemon" Instance class that manages a task queue for the Daemon.
 * @param <T> {@link IThreadTaskBase}
 */
public interface IThreadDaemonHandler<T extends IThreadTaskBase>
		extends IClientTickHandler, AutoCloseable
{
	/**
	 * Meant to delay the start of the thread
	 */
	void start();

	/**
	 * Stop the thread
	 */
	void stop();

	/**
	 * Stop/Start
	 */
	void reset();

	/**
	 * Add a new task to process
	 * @param newTask {@link IThreadTaskBase}
	 */
	void addTask(T newTask);

	/**
	 * Pool the next free task, or NULL
	 * @return {@link IThreadTaskBase}
	 */
	T getNextTask();

	/**
	 * Return the tick interval for managing the queue
	 * @return ()
	 */
	long getTaskInterval();
}
