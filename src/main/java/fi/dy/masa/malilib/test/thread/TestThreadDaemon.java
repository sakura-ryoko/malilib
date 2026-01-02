package fi.dy.masa.malilib.test.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.client.Minecraft;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IAsyncThreadDaemon;
import fi.dy.masa.malilib.util.MathUtils;

// todo -- UNCOMMENT WHEN TESTING!  (Do not mess with threads when not in use)
public class TestThreadDaemon implements IAsyncThreadDaemon<TestThreadAsync>
{
//	private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setNameFormat("MaLiLib Test Thread Daemon").setDaemon(true).build();
	public static final TestThreadDaemon INSTANCE = new TestThreadDaemon();
//	private final Thread thread;
//	private final TestThreadExecutor threadExecutor;

	private final Queue<TestThreadAsync> queue = new ConcurrentLinkedQueue<>();
	private final float taskInterval = 50.0f;
	private long lastTick;

	private TestThreadDaemon()
	{
		this.lastTick = System.currentTimeMillis();
//		this.threadExecutor = new TestThreadExecutor();
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
		this.queue.clear();
		this.stop();
		this.start();
	}

	@Override
	public void addTask(TestThreadAsync task)
	{
		this.queue.offer(task);
	}

	@Override
	public TestThreadAsync getNextTask()
	{
		return this.queue.poll();
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
				for (int i = 0; i < 4; i++)
				{
					this.addTask(new TestThreadAsync());
				}

				System.out.printf("TestThreadDaemon: taskQueue: [%02d]\n", this.queue.size());
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
		this.queue.clear();
		this.stop();
	}
}
