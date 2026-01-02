package fi.dy.masa.malilib.test.thread;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IThreadDaemonHandler;
import fi.dy.masa.malilib.util.MathUtils;

// todo -- UNCOMMENT WHEN TESTING!  (Do not mess with threads when not in use)
public class TestThreadDaemonDefaultHandler implements IThreadDaemonHandler<TestThreadTaskDefault>
{
//	private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setNameFormat("MaLiLib Test Default Thread Daemon").setDaemon(true).build();
	public static final TestThreadDaemonDefaultHandler INSTANCE = new TestThreadDaemonDefaultHandler();
//	private final Thread thread;
//	private final TestThreadDaemonExecutorDefault threadExecutor;

//	private final Queue<TestThreadTaskDefault> queue = new LinkedBlockingQueue<>();
	private final float taskInterval = 30.0f;
	private long lastTick;

	private TestThreadDaemonDefaultHandler()
	{
		this.lastTick = System.currentTimeMillis();
//		this.threadExecutor = new TestThreadDaemonExecutorDefault();
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
	public void addTask(TestThreadTaskDefault task)
	{
//		this.queue.offer(task);
	}

	@Override
	public TestThreadTaskDefault getNextTask()
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

					this.addTask(new TestThreadTaskDefault(() ->
                                 MaLiLib.LOGGER.info("Running TestThreadTaskDefault as a Runnable, [{}]", finalIndex))
					);
				}

//				System.out.printf("TestThreadDaemonDefaultHandler: taskQueue: [%02d]\n", this.queue.size());
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
