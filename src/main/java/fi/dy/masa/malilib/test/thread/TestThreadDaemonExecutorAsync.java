package fi.dy.masa.malilib.test.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IThreadDaemonExecutor;
import fi.dy.masa.malilib.util.MathUtils;

public class TestThreadDaemonExecutorAsync implements IThreadDaemonExecutor<TestThreadTaskAsync>
{
	private final AtomicBoolean running = new AtomicBoolean(true);
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private final long sleepTime;

	public TestThreadDaemonExecutorAsync()
	{
		this(600000L);  // 10 min
	}

	public TestThreadDaemonExecutorAsync(long sleepTime)
	{
		this.sleepTime = MathUtils.clamp(sleepTime, 60000L, Long.MAX_VALUE); // 1 min
	}

	@Override
	public boolean isRunning()
	{
		return this.running.get();
	}

	@Override
	public boolean isPaused()
	{
		return this.paused.get();
	}

	@Override
	public void start()
	{
		if (!this.isRunning())
		{
			MaLiLib.LOGGER.error("Executor: Starting");
			if (this.isPaused())
			{
				this.paused.set(false);
			}

			this.running.set(true);
		}

		this.run();
	}

	@Override
	public void interrupt(InterruptedException interrupt)
	{
		MaLiLib.LOGGER.error("Executor: Interrupt Signal: {}", interrupt.getLocalizedMessage());
		if (this.isPaused())
		{
			this.resume();
		}
	}

	@Override
	public void pause()
	{
		this.paused.set(true);
	}

	@Override
	public void resume()
	{
		if (this.isPaused())
		{
			MaLiLib.LOGGER.error("Executor: Resuming");
			this.paused.set(false);
		}

		this.start();
	}

	@Override
	public void stop()
	{
		MaLiLib.LOGGER.error("Executor: Stopping");
		if (!this.isPaused())
		{
			this.paused.set(true);
		}
		if (this.isRunning())
		{
			this.running.set(false);
		}
	}

	@Override
	public long sleepTime()
	{
		return this.sleepTime;
	}

	@Override
	public String getName()
	{
		return TestThreadDaemonAsyncHandler.INSTANCE.getName();
	}

	@Override
	public boolean hasTasks()
	{
		return TestThreadDaemonAsyncHandler.INSTANCE.hasTasks();
	}

	@Override
	public void run()
	{
		if (!this.isCorrectThread()) { return; }
		MaLiLib.LOGGER.error("Executor: Running: [{}/{}]", this.isRunning(), this.isPaused());

		while (this.isRunning())
		{
			if (this.isPaused() && this.hasTasks())
			{
				this.resume();
			}
			else if (!this.isPaused() && this.loopSafe())
			{
				this.paused.set(true);
				this.sleep();
				return;
			}
		}
	}

	@Override
	public boolean loopSafe()
	{
		try
		{
			TestThreadTaskAsync task = TestThreadDaemonAsyncHandler.INSTANCE.getNextTask();

			if (task != null)
			{
				this.processTask(task);
				return false;
			}
		}
		catch (InterruptedException e)
		{
			this.interrupt(e);
		}
		catch (Exception err)
		{
			MaLiLib.LOGGER.error("loopSafe: Exception: {}", err.getLocalizedMessage());
		}

		return this.shouldPause();
	}

	@Override
	public void processTask(TestThreadTaskAsync task) throws InterruptedException
	{
		// CompletableFuture uses a ForkJoinPool
		CompletableFuture<Void> result = task.runAsync();

		result.whenComplete((res, err) ->
		                    {
								if (err != null)
								{
									MaLiLib.LOGGER.error("processTask: completed with error: {}", err.getLocalizedMessage());
									return;
								}

								MaLiLib.LOGGER.info("processTask: completed");
		                    });
	}
}
