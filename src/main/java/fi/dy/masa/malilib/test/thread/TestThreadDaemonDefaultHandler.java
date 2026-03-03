package fi.dy.masa.malilib.test.thread;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IThreadDaemonHandler;
import fi.dy.masa.malilib.util.MathUtils;

// todo -- UNCOMMENT WHEN TESTING!  (Do not mess with threads when not in use)
public class TestThreadDaemonDefaultHandler implements IThreadDaemonHandler<TestThreadTaskDefault>
{
	public static final TestThreadDaemonDefaultHandler INSTANCE = new TestThreadDaemonDefaultHandler();
	private static final int MAX_PLATFORM_THREADS = 1;
//	private final int threadCount = this.calculateMaxThreads();
	private boolean useVirtual = false;
	private final String namePrefix = MaLiLibReference.MOD_NAME+" Test Default Thread";
	private static final float TASK_INTERVAL = 20.0f;
//	private final ConcurrentHashMap<String, Thread> threadMap = this.builder();
//	private final LinkedBlockingQueue<TestThreadTaskDefault> queue = new LinkedBlockingQueue<>();
	private long lastTick;

	private int calculateMaxThreads()
	{
		final int result = this.getThreadCountSafe();
		if (result < 1) { this.useVirtual = true; }

		return MathUtils.clamp(result, 1, MAX_PLATFORM_THREADS);
	}

//	private ConcurrentHashMap<String, Thread> builder()
//	{
//		ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<>(this.threadCount, 0.9f, 1);
//
//		for (int i = 0; i < this.threadCount; i++)
//		{
//			final String name = this.threadCount > 1 ? this.namePrefix+" "+ (i+1) : this.namePrefix;
//			threads.put(name, this.threadFactory(name, this.useVirtual, new TestThreadDaemonExecutorDefault()));
//		}
//
//		return threads;
//	}

	private TestThreadDaemonDefaultHandler()
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
//		MaLiLib.LOGGER.info("Starting [{}] Test Default threads", this.threadMap.size());
//		Set<String> keys = this.threadMap.keySet();
//
//		for (String key : keys)
//		{
//			try
//			{
//				this.safeStart(this.threadMap.get(key));
//			}
//			catch (ConcurrentModificationException cme)
//			{
//				// Busy
//			}
//			catch (IllegalStateException is)
//			{
//				// Terminated
//				Thread entry = this.threadFactory(key, this.useVirtual, new TestThreadDaemonExecutorDefault());
//				entry.start();
//
//				synchronized (this.threadMap)
//				{
//					this.threadMap.replace(key, entry);
//				}
//			}
//			catch (RuntimeException re)
//			{
//				// Already Running
//			}
//			catch (Exception ignored) {}
//		}
	}

	@Override
	public void stop()
	{
//		MaLiLib.LOGGER.info("Stopping [{}] Test Default threads", this.threadMap.size());
//		Set<String> keys = this.threadMap.keySet();
//
//		for (String key : keys)
//		{
//			try
//			{
//				this.safeStop(this.threadMap.get(key));
//			}
//			catch (ConcurrentModificationException cme)
//			{
//				// Busy
//				MaLiLib.LOGGER.warn("Thread [{}] is currently busy, and shouldn't be stopped", key);
//			}
//			catch (IllegalStateException is)
//			{
//				// Terminated already
//			}
//			catch (IllegalThreadStateException is)
//			{
//				// Never started
//			}
//			catch (Exception ignored) {}
//		}
	}

	@Override
	public void reset()
	{
//		this.queue.clear();
	}

	@Override
	public void addTask(TestThreadTaskDefault task)
	{
//		boolean wasEmpty = this.queue.isEmpty();
//		this.queue.offer(task);
//
//		if (wasEmpty)
//		{
//			this.ensureThreadsAreAlive();
//		}
	}

	@Override
	public TestThreadTaskDefault getNextTask() throws InterruptedException
	{
//		return this.queue.take();
//		return this.queue.poll();
		return null;
	}

	@Override
	public boolean hasTasks()
	{
//		return !this.queue.isEmpty();
		return false;
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
					for (int i = 0; i < 5; i++)
					{
						final int finalIndex = i;

						this.addTask(new TestThreadTaskDefault(() ->
								                                       MaLiLib.LOGGER.info("Running TestThreadTaskDefault as a Runnable, [{}]", finalIndex))
						);
					}

//					System.out.printf("TestThreadDaemonDefaultHandler: taskQueue: [%02d]\n", this.queue.size());
					this.ensureThreadsAreAlive();
				}

				this.lastTick = now;
			}
		}
	}

	private void ensureThreadsAreAlive()
	{
//		if (this.hasTasks())
//		{
//			Set<String> keySet = this.threadMap.keySet();
//
//			for (String key : keySet)
//			{
//				try
//				{
//					this.safeStart(this.threadMap.get(key));
//				}
//				catch (IllegalStateException is)
//				{
//					// Terminated (Replace)
//					Thread entry = this.threadFactory(key, this.useVirtual, new TestThreadDaemonExecutorDefault());
//					entry.start();
//
//					synchronized (this.threadMap)
//					{
//						this.threadMap.replace(key, entry);
//					}
//				}
//				catch (RuntimeException ignored) {}
//			}
//		}
	}

	@Override
	public void close() throws Exception
	{
		this.endAll();
	}
}
