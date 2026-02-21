package fi.dy.masa.malilib.interfaces;

import java.time.Duration;
import java.util.ConcurrentModificationException;
import org.apache.commons.lang3.math.Fraction;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.MathUtils;

/**
 * Extend this to create a "Daemon" Instance class that manages a task queue for the Daemon.
 * @param <T> {@link IThreadTaskBase}
 */
public interface IThreadDaemonHandler<T extends IThreadTaskBase>
		extends IClientTickHandler, AutoCloseable
{
	/**
	 * Get a "Safe" Thread count; or 1/8 of your system's Core Count.
	 * Note that the number return might be 0, which means that you should
	 * only be using 1 Virtual Thread Max if your CPU has less than 8 Cores.
	 * @return ()
	 */
	default int getThreadCountSafe()
	{
		final int maxThreads = Runtime.getRuntime().availableProcessors();
		final Fraction calc = Fraction.getFraction(maxThreads, 8);
		return MathUtils.clamp(calc.intValue(), 0, maxThreads);
	}

	/**
	 * Default wrapper around building a new {@link java.util.concurrent.ThreadFactory}
	 * @param name -
	 * @param useVirtual -
	 * @param executor -
	 * @return -
	 */
	default Thread threadFactory(String name, boolean useVirtual, IThreadDaemonExecutor<T> executor)
	{
		MaLiLib.debugLog("Executor#threadFactory: '{}' [useVirtual: {}]", name, useVirtual);
		if (useVirtual)
		{
			return Thread.ofVirtual().name(name).unstarted(executor);
		}

		return Thread.ofPlatform().name(name).daemon(true).unstarted(executor);
	}

	/**
	 * Safely start the thread by checking the current state.
	 * @param t ()
	 * @throws RuntimeException ()
	 */
	default void safeStart(Thread t) throws RuntimeException
	{
		if (t == null) { throw new RuntimeException(); }
		MaLiLib.debugLog("Executor#safeStart: '{}' [State: {}]", t.getName(), t.getState().name());

		switch (t.getState())
		{
			case NEW -> t.start();
			case TIMED_WAITING -> t.interrupt();
			case RUNNABLE -> throw new RuntimeException();
			case BLOCKED, WAITING -> throw new ConcurrentModificationException();
			case TERMINATED -> throw new IllegalStateException();
		}
	}

	/**
	 * Safely Stop the thread by checking the current state.
	 * @param t ()
	 * @throws RuntimeException ()
	 */
	default void safeStop(Thread t) throws RuntimeException
	{
		if (t == null) { throw new RuntimeException(); }
		MaLiLib.debugLog("Executor#safeStop: '{}' [State: {}]", t.getName(), t.getState().name());

		switch (t.getState())
		{
			case NEW -> throw new IllegalThreadStateException();
			case BLOCKED, WAITING -> throw new ConcurrentModificationException();
			case TERMINATED -> throw new IllegalStateException();
			default ->
			{
				try
				{
					if (t.join(Duration.ofMillis(500L)))
					{
						this.safeStop(t);
					}
				}
				catch (Exception ignored) {}
			}
		}
	}

	/**
	 * Return the Thread "Prefix" name.
	 * @return ()
	 */
	String getName();

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
	T getNextTask() throws InterruptedException;

	/**
	 * Return the tick interval for managing the queue
	 * @return ()
	 */
	long getTaskInterval();

	/**
	 * Return if this has tasks.
	 * @return -
	 */
	boolean hasTasks();

	/**
	 * End Task Execution
	 */
	default void endAll()
	{
		this.reset();
		this.stop();
	}
}
