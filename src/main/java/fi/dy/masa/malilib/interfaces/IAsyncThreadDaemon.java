package fi.dy.masa.malilib.interfaces;

/**
 * Extend this to create a "Daemon" Instance class that manages a task queue for the Daemon.
 * @param <T> {@link AsyncThreadTaskBase}
 */
public interface IAsyncThreadDaemon<T extends AsyncThreadTaskBase>
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
	 * @param newTask {@link AsyncThreadTaskBase}
	 */
	void addTask(T newTask);

	/**
	 * Pool the next free task, or NULL
	 * @return {@link AsyncThreadTaskBase}
	 */
	T getNextTask();

	/**
	 * Return the tick interval for managing the queue
	 * @return ()
	 */
	long getTaskInterval();
}
