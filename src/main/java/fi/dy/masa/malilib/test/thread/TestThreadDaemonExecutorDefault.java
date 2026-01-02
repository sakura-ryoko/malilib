package fi.dy.masa.malilib.test.thread;

import java.util.concurrent.atomic.AtomicBoolean;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IThreadDaemonExecutor;

public class TestThreadDaemonExecutorDefault implements IThreadDaemonExecutor<TestThreadTaskDefault>
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
				TestThreadTaskDefault task = TestThreadDaemonDefaultHandler.INSTANCE.getNextTask();

				if (task != null)
				{
					this.processTask(task);
				}
			}
			catch (InterruptedException interrupt)
			{
				MaLiLib.LOGGER.error("TestThreadDaemonExecutorDefault: Interrupted: {}", interrupt.getLocalizedMessage());
				this.stop();
				return;
			}
			catch (Exception err)
			{
				MaLiLib.LOGGER.error("TestThreadDaemonExecutorDefault: Exception: {}", err.getLocalizedMessage());
				this.stop();
				return;
			}
		}
	}

	@Override
	public void processTask(TestThreadTaskDefault task)
			throws InterruptedException
	{
		try
		{
			task.run();
		}
		catch (Exception err)
		{
			MaLiLib.LOGGER.error("TestThreadTaskDefault: completed with error: {}", err.getLocalizedMessage());
			return;
		}

		MaLiLib.LOGGER.info("TestThreadTaskDefault: completed");
	}
}
