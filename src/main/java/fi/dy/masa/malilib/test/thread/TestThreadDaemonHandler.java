package fi.dy.masa.malilib.test.thread;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IThreadDaemonHandler;
import fi.dy.masa.malilib.util.MathUtils;

// todo -- UNCOMMENT WHEN TESTING!  (Do not mess with threads when not in use)
public class TestThreadDaemonHandler implements IThreadDaemonHandler<TestThreadTask>
{
	public static final TestThreadDaemonHandler INSTANCE = new TestThreadDaemonHandler();
	private static final int MAX_PLATFORM_THREADS = 1;
	private final int threadCount = this.calculateMaxThreads();
	private boolean useVirtual = false;
	private final String namePrefix = MaLiLibReference.MOD_NAME+" Test Default Thread";
	private static final float TASK_INTERVAL = 20.0f;
//	private final ConcurrentHashMap<String, ThreadExecutorPair<TestThreadTask>> threadMap = this.builder();
//	private final LinkedBlockingQueue<TestThreadTask> queue = new LinkedBlockingQueue<>();
	private long lastTick;
	private boolean forceStop = false;

	private int calculateMaxThreads()
	{
		final int result = this.getThreadCountSafe();
		if (result < 1) { this.useVirtual = true; }

		return MathUtils.clamp(result, 1, MAX_PLATFORM_THREADS);
	}

//	private ConcurrentHashMap<String, ThreadExecutorPair<TestThreadTask>> builder()
//	{
//		ConcurrentHashMap<String, ThreadExecutorPair<TestThreadTask>> threads = new ConcurrentHashMap<>(this.threadCount, 0.9f, 1);
//
//		for (int i = 0; i < this.threadCount; i++)
//		{
//			final String name = this.threadCount > 1 ? this.namePrefix+" "+ (i+1) : this.namePrefix;
//			threads.put(name, this.threadFactory(name, this.useVirtual, new TestThreadDaemonExecutor()));
//		}
//
//		return threads;
//	}

	private TestThreadDaemonHandler()
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
//			ThreadExecutorPair<TestThreadTask> pair = this.threadMap.get(key);
//
//			try
//			{
//				this.safeStart(pair);
//			}
//			catch (ConcurrentModificationException cme)
//			{
//				// Busy
//			}
//			catch (IllegalStateException is)
//			{
//				// Terminated
//				pair = this.threadFactory(key, this.useVirtual, new TestThreadDaemonExecutor());
//				pair.thread().start();
//
//				synchronized (this.threadMap)
//				{
//					this.threadMap.replace(key, pair);
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
	public void addTask(TestThreadTask task)
	{
//		boolean empty = this.queue.isEmpty();
//		this.queue.offer(task);
//
//		if (empty)
//		{
//			this.ensureThreadsAreAlive();
//		}
	}

	@Override
	public TestThreadTask getNextTask() throws InterruptedException
	{
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
//		if (MaLiLibReference.DEBUG_MODE && MaLiLibReference.EXPERIMENTAL_MODE)
//		{
//			long now = System.currentTimeMillis();
//
//			if ((now - this.lastTick) > this.getTaskInterval())
//			{
//				if (mc.level != null)
//				{
//					for (int i = 0; i < 64; i++)
//					{
//						final int finalIndex = i;
//
//						this.addTask(
//								new TestThreadTask(() -> MaLiLib.LOGGER.info("Running TestThreadTaskDefault as a Runnable, [{}]", finalIndex))
//						);
//					}
//
//					System.out.printf("TestThreadDaemonDefaultHandler: taskQueue: [%02d]\n", this.queue.size());
//					this.ensureThreadsAreAlive();
//				}
//
//				this.lastTick = now;
//			}
//		}
	}

	private void ensureThreadsAreAlive()
	{
//		if (this.hasTasks())
//		{
//			Set<String> keySet = this.threadMap.keySet();
//
//			for (String key : keySet)
//			{
//				ThreadExecutorPair<TestThreadTask> pair = this.threadMap.get(key);
//
//				try
//				{
//					this.safeStart(pair);
//				}
//				catch (IllegalStateException is)
//				{
//					// Terminated (Replace)
//					pair = this.threadFactory(key, this.useVirtual, new TestThreadDaemonExecutor());
//					pair.thread().start();
//
//					synchronized (this.threadMap)
//					{
//						this.threadMap.replace(key, pair);
//					}
//				}
//				catch (RuntimeException ignored) {}
//			}
//		}
	}

	@Override
	public void resetForceStop()
	{
		this.forceStop = false;
	}

	@Override
	public boolean isForceStop()
	{
		return this.forceStop;
	}

	@Override
	public void endAll()
	{
		this.forceStop = true;
		this.reset();
		this.stop();
	}

	@Override
	public void close() throws Exception
	{
		this.endAll();
	}
}
