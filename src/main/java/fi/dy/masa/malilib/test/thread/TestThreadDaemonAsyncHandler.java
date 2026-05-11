package fi.dy.masa.malilib.test.thread;

import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IThreadDaemonHandler;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.ThreadExecutorPair;

// todo -- UNCOMMENT WHEN TESTING!  (Do not mess with threads when not in use)
public class TestThreadDaemonAsyncHandler implements IThreadDaemonHandler<TestThreadTaskAsync>
{
	public static final TestThreadDaemonAsyncHandler INSTANCE = new TestThreadDaemonAsyncHandler();
	private static final int MAX_PLATFORM_THREADS = 1;
	private final int threadCount = this.calculateMaxThreads();
	private boolean useVirtual = false;
	private final String namePrefix = MaLiLibReference.MOD_NAME+" Test Async Thread";
	private static final float TASK_INTERVAL = 10.0f;
	private final ConcurrentHashMap<String, ThreadExecutorPair<TestThreadTaskAsync>> threadMap = this.builder();
	private final ConcurrentLinkedQueue<TestThreadTaskAsync> queue = new ConcurrentLinkedQueue<>();
	private long lastTick;

	private int calculateMaxThreads()
	{
		final int result = this.getThreadCountSafe();
		if (result < 1) { this.useVirtual = true; }

		return MathUtils.clamp(result, 1, MAX_PLATFORM_THREADS);
	}

	private ConcurrentHashMap<String, ThreadExecutorPair<TestThreadTaskAsync>> builder()
	{
		ConcurrentHashMap<String, ThreadExecutorPair<TestThreadTaskAsync>> threads = new ConcurrentHashMap<>(this.threadCount, 0.9f, 1);

		for (int i = 0; i < this.threadCount; i++)
		{
			final String name = this.threadCount > 1 ? this.namePrefix+" "+ (i+1) : this.namePrefix;
			threads.put(name, this.threadFactory(name, this.useVirtual, new TestThreadDaemonExecutorAsync()));
		}

		return threads;
	}

	private TestThreadDaemonAsyncHandler()
	{
		this.lastTick = System.currentTimeMillis();
	}

	@Override
	public String getName()
	{
		return this.namePrefix;
	}

	@Override
	public void start()
	{
		MaLiLib.LOGGER.info("Starting [{}] Test Async threads", this.threadMap.size());
		Set<String> keys = this.threadMap.keySet();

		for (String key : keys)
		{
			ThreadExecutorPair<TestThreadTaskAsync> pair = this.threadMap.get(key);

			try
			{
				this.safeStart(pair);
			}
			catch (ConcurrentModificationException cme)
			{
				// Busy
			}
			catch (IllegalStateException is)
			{
				// Terminated
				pair = this.threadFactory(key, this.useVirtual, new TestThreadDaemonExecutorAsync());
				pair.thread().start();

				synchronized (this.threadMap)
				{
					this.threadMap.replace(key, pair);
				}
			}
			catch (RuntimeException re)
			{
				// Already Running
			}
			catch (Exception ignored) {}
		}
	}

	@Override
	public void stop()
	{
		MaLiLib.LOGGER.info("Stopping [{}] Test Async threads", this.threadMap.size());
		Set<String> keys = this.threadMap.keySet();

		for (String key : keys)
		{
			ThreadExecutorPair<TestThreadTaskAsync> pair = this.threadMap.get(key);

			try
			{
				this.safeStop(pair);
			}
			catch (ConcurrentModificationException cme)
			{
				// Busy
				MaLiLib.LOGGER.warn("Thread [{}] is currently busy, and shouldn't be stopped", key);
			}
			catch (IllegalStateException is)
			{
				// Terminated already
			}
			catch (IllegalThreadStateException is)
			{
				// Never started
			}
			catch (Exception ignored) {}
		}
	}

	@Override
	public void reset()
	{
		this.queue.clear();
	}

	@Override
	public void addTask(TestThreadTaskAsync task)
	{
		boolean empty = this.queue.isEmpty();
		this.queue.offer(task);

		if (empty)
		{
			this.ensureThreadsAreAlive();
		}
	}

	@Override
	public TestThreadTaskAsync getNextTask() throws InterruptedException
	{
		return this.queue.poll();
//		return null;
	}

	@Override
	public boolean hasTasks()
	{
		return !this.queue.isEmpty();
//		return false;
	}

	@Override
	public long getTaskInterval()
	{
		return MathUtils.floor(TASK_INTERVAL * 1000L);
	}

	@Override
	public void onClientTick(Minecraft mc)
	{
		if (MaLiLibReference.DEBUG_MODE && MaLiLibReference.EXPERIMENTAL_MODE)
		{
			long now = System.currentTimeMillis();

			if ((now - this.lastTick) > this.getTaskInterval())
			{
				if (mc.level != null)
				{
					for (int i = 0; i < 64; i++)
					{
						final int finalIndex = i;

						this.addTask(new TestThreadTaskAsync(() ->
								                                     MaLiLib.LOGGER.info("Running TestThreadTaskAsync as a Runnable, [{}]", finalIndex))
						);
					}

					System.out.printf("TestThreadDaemonAsyncHandler: taskQueue: [%02d]\n", this.queue.size());
					this.ensureThreadsAreAlive();
				}

				this.lastTick = now;
			}
		}
	}

	// TODO -- is this even necessary?
	private void ensureThreadsAreAlive()
	{
		if (this.hasTasks())
		{
			Set<String> keySet = this.threadMap.keySet();

			for (String key : keySet)
			{
				ThreadExecutorPair<TestThreadTaskAsync> pair = this.threadMap.get(key);

				try
				{
					this.safeStart(pair);
				}
				catch (IllegalStateException is)
				{
					// Terminated (Replace)
					pair = this.threadFactory(key, this.useVirtual, new TestThreadDaemonExecutorAsync());
					pair.thread().start();

					synchronized (this.threadMap)
					{
						this.threadMap.replace(key, pair);
					}
				}
				catch (RuntimeException ignored) {}
			}
		}
	}

	@Override
	public void close() throws Exception
	{
		this.endAll();
	}
}
