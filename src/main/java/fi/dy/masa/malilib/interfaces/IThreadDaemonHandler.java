package fi.dy.masa.malilib.interfaces;

import java.time.Duration;
import java.util.ConcurrentModificationException;
import org.apache.commons.lang3.math.Fraction;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.MathUtils;

/**
 * Extend this to create a "Daemon" Instance class that manages a task {@link java.util.Queue} for the Daemon.
 * @param <T> {@link IThreadTaskBase}
 * <br>
 * NOTE: In my experience; using a {@link java.util.concurrent.LinkedBlockingQueue} or using a
 * {@link java.util.concurrent.ConcurrentLinkedQueue} should be considered first.
 */
public interface IThreadDaemonHandler<T extends IThreadTaskBase>
		extends IClientTickHandler, AutoCloseable
{
	/**
	 * Get a "Safe" {@link Thread} count; or 1/8 of your system's Core Count.
	 * Note that the number return might be 0, which means that you should
	 * only be using 1 Virtual {@link Thread} Max if your CPU has less than 8 Cores.
	 * @return -
	 */
	default int getThreadCountSafe()
	{
		final int maxThreads = Runtime.getRuntime().availableProcessors();
		final Fraction calc = Fraction.getFraction(maxThreads, 8);
		return MathUtils.clamp(calc.intValue(), 0, maxThreads);
	}

	/**
	 * Default wrapper around building a new {@link Thread}
	 * @param name The name of the new {@link Thread}
	 * @param useVirtual Whether the {@link Thread} should be run Virtually by the JVM
	 * @param executor The {@link IThreadDaemonExecutor} to utilize
	 * @return The newly built {@link Thread}
	 */
	default Thread threadFactory(String name, boolean useVirtual, IThreadDaemonExecutor<T> executor)
	{
		MaLiLib.debugLog("IThreadDaemonHandler#threadFactory: '{}' [useVirtual: {}]", name, useVirtual);
		if (useVirtual)
		{
			return Thread.ofVirtual().name(name).unstarted(executor);
		}

		return Thread.ofPlatform().name(name).daemon(true).unstarted(executor);
	}

	/**
	 * Safely start the {@link Thread} by checking the current state.
	 * @param t The {@link Thread}
	 * @throws RuntimeException The {@link Thread} is Null, or already Running
	 * @throws ConcurrentModificationException The {@link Thread} is in the Blocking state
	 * @throws IllegalStateException The {@link Thread} was terminated, and needs to be replaced.
	 */
	default void safeStart(Thread t) throws RuntimeException
	{
		if (t == null) { throw new RuntimeException(); }
		MaLiLib.debugLog("IThreadDaemonHandler#safeStart: '{}' [State: {}]", t.getName(), t.getState().name());

		switch (t.getState())
		{
			case NEW -> t.start();
			case TIMED_WAITING, WAITING -> t.interrupt();
			case RUNNABLE -> throw new RuntimeException();
			case BLOCKED -> throw new ConcurrentModificationException();
			case TERMINATED -> throw new IllegalStateException();
		}
	}

	/**
	 * Safely Stop the {@link Thread} by checking the current state.
	 * @param t The {@link Thread}
	 * @throws RuntimeException If the {@link Thread} is Null
	 * @throws IllegalThreadStateException If the {@link Thread} is New and not yet started
	 * @throws ConcurrentModificationException If the {@link Thread} is in a Blocking state
	 * @throws IllegalStateException If the {@link Thread} was Terminated
	 */
	default void safeStop(Thread t) throws RuntimeException
	{
		if (t == null) { throw new RuntimeException(); }
		MaLiLib.debugLog("IThreadDaemonHandler#safeStop: '{}' [State: {}]", t.getName(), t.getState().name());

		switch (t.getState())
		{
			case NEW -> throw new IllegalThreadStateException();
			case BLOCKED -> throw new ConcurrentModificationException();
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
	 * Return the {@link Thread} "Prefix" name; such as "MaLiLib Worker Thread ";
	 * which usually ends with a number.  This is important for checking
	 * the Current Running {@link Thread}'s name under {@link IThreadDaemonExecutor}
	 * @return -
	 */
	String getName();

	/**
	 * Start the {@link Thread}(s) -- Which should be done after Game login
	 */
	void start();

	/**
	 * Stop the {@link Thread}(s) -- Which should only be done at Game exit
	 */
	void stop();

	/**
	 * Clear any tasks remaining in the Queue
	 */
	void reset();

	/**
	 * Offer a new task to process
	 * @param newTask {@link IThreadTaskBase}
	 */
	void addTask(T newTask);

	/**
	 * Poll (or Take) the next free task, or NULL
	 * @return {@link IThreadTaskBase}
	 */
	T getNextTask() throws InterruptedException;

	/**
	 * Return the tick interval for managing the queue
	 * @return -
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
		MaLiLib.debugLog("IThreadDaemonHandler#endAll()");
		this.reset();
		this.stop();
	}
}
