package fi.dy.masa.malilib.test.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IThreadDaemonExecutor;

public class TestThreadDaemonExecutorAsync implements IThreadDaemonExecutor<TestThreadTaskAsync>
{
	private final AtomicBoolean running = new AtomicBoolean(true);

	@Override
	public boolean isRunning()
	{
		return this.running.get();
	}

	@Override
	public void start()
	{
		this.running.set(true);
	}

	@Override
	public void stop()
	{
		this.running.set(false);
	}

	@Override
	public void run()
	{
		while (this.isRunning())
		{
			try
			{
				TestThreadTaskAsync task = TestThreadDaemonAsyncHandler.INSTANCE.getNextTask();

				if (task != null)
				{
					this.processTask(task);
				}
			}
			catch (InterruptedException interrupt)
			{
				MaLiLib.LOGGER.error("TestThreadExecutor: Interrupted: {}", interrupt.getLocalizedMessage());
				this.stop();
				return;
			}
			catch (Exception err)
			{
				MaLiLib.LOGGER.error("TestThreadExecutor: Exception: {}", err.getLocalizedMessage());
				this.stop();
				return;
			}
		}
	}

	@Override
	public void processTask(TestThreadTaskAsync task)
			throws InterruptedException
	{
		CompletableFuture<Void> result = task.runAsync();

		result.whenComplete((res, err) ->
		                    {
								if (err != null)
								{
									MaLiLib.LOGGER.error("TestThreadTaskAsync: completed with error: {}", err.getLocalizedMessage());
									return;
								}

								MaLiLib.LOGGER.info("TestThreadTaskAsync: completed");
		                    });
	}
}
