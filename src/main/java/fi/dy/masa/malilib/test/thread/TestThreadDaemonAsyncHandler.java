package fi.dy.masa.malilib.test.thread;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IThreadDaemonHandler;
import fi.dy.masa.malilib.util.MathUtils;

// todo -- UNCOMMENT WHEN TESTING!  (Do not mess with threads when not in use)
public class TestThreadDaemonAsyncHandler implements IThreadDaemonHandler<TestThreadTaskAsync>
{
//	private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setNameFormat("MaLiLib Test Async Thread Daemon").setDaemon(true).build();
	public static final TestThreadDaemonAsyncHandler INSTANCE = new TestThreadDaemonAsyncHandler();
//	private final Thread thread;
//	private final TestThreadDaemonExecutorAsync threadExecutor;

//	private final Queue<TestThreadTaskAsync> queue = new ConcurrentLinkedQueue<>();
	private final float taskInterval = 30.0f;
	private long lastTick;

	private TestThreadDaemonAsyncHandler()
	{
		this.lastTick = System.currentTimeMillis();
//		this.threadExecutor = new TestThreadDaemonExecutorAsync();
//		this.thread = THREAD_FACTORY.newThread(this.threadExecutor);
	}

	@Override
	public void start()
	{
//		this.thread.start();
	}

	@Override
	public void stop()
	{
//		this.threadExecutor.stop();
//		this.thread.interrupt();
	}

	@Override
	public void reset()
	{
//		this.queue.clear();
		this.stop();
		this.start();
	}

	@Override
	public void addTask(TestThreadTaskAsync task)
	{
//		this.queue.offer(task);
	}

	@Override
	public TestThreadTaskAsync getNextTask()
	{
//		return this.queue.poll();
		return null;
	}

	@Override
	public long getTaskInterval()
	{
		return MathUtils.floor(this.taskInterval * 1000L);
	}

	@Override
	public void onClientTick(Minecraft mc)
	{
		if (MaLiLibReference.DEBUG_MODE && MaLiLibReference.EXPERIMENTAL_MODE)
		{
			long now = System.currentTimeMillis();

			if ((now - this.lastTick) > this.getTaskInterval())
			{
				for (int i = 0; i < 3; i++)
				{
					final int finalIndex = i;

					this.addTask(new TestThreadTaskAsync(() ->
                                 MaLiLib.LOGGER.info("Running TestThreadTaskAsync as a Runnable, [{}]", finalIndex))
					);
				}

//				System.out.printf("TestThreadDaemonAsyncHandler: taskQueue: [%02d]\n", this.queue.size());
				this.lastTick = now;
			}
		}
		else
		{
//			if (this.threadExecutor.isRunning())
//			{
//				this.stop();
//			}
		}
	}

	@Override
	public void close() throws Exception
	{
//		this.queue.clear();
		this.stop();
	}
}
