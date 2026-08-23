package fi.dy.masa.malilib.test.thread;

import java.util.concurrent.atomic.AtomicBoolean;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IThreadDaemonExecutor;
import fi.dy.masa.malilib.util.MathUtils;

public class TestThreadDaemonExecutor implements IThreadDaemonExecutor<TestThreadTask>
{
	private final AtomicBoolean running = new AtomicBoolean(true);
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private final long sleepTime;
	private final float sleepDelay;
	private final long maxTicks;
	private long lastTaskTime;
	private long ticks;

	public TestThreadDaemonExecutor()
	{
		this(1800000L);  // 30 min
	}

	public TestThreadDaemonExecutor(long sleepTime)
	{
		this.sleepTime = MathUtils.clamp(sleepTime, 60000L, Long.MAX_VALUE); // 1 min
		this.sleepDelay = 0.75F;        // <1-second sleep delay (Must not be < 1/2 the tick rate)
		this.maxTicks = 32L;           // Cap how many ticks per an interrupt cycle without tasks to do
		this.ticks = 0L;
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
		if (TestThreadDaemonHandler.INSTANCE.isForceStop())
		{
			this.stop();
			return;
		}

		if (!this.isRunning())
		{
			MaLiLib.LOGGER.error("Executor: Starting");
			if (this.isPaused())
			{
				this.paused.set(false);
			}

			this.run();
		}
	}

	@Override
	public void interrupt(InterruptedException interrupt)
	{
		MaLiLib.LOGGER.error("Executor: Interrupt Signal: {}",
		                    interrupt.getLocalizedMessage() != null
		                    ? interrupt.getLocalizedMessage()  // This is null sometimes?
		                    : "received interrupt signal");
		if (this.isPaused() || !this.isRunning())
		{
			this.resume();
		}
	}

	@Override
	public void pause()
	{
		MaLiLib.LOGGER.error("Executor: Pausing");
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
		return TestThreadDaemonHandler.INSTANCE.getName();
	}

	@Override
	public boolean hasTasks()
	{
		return TestThreadDaemonHandler.INSTANCE.hasTasks();
	}

	@Override
	public void run()
	{
		if (!this.isCorrectThread()) { return; }

		if (TestThreadDaemonHandler.INSTANCE.isForceStop())
		{
			this.stop();
			return;
		}

		this.running.set(true);
		this.lastTaskTime = System.currentTimeMillis();
		this.ticks = 0L;
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
				this.ticks = 0L;
				this.sleep();
				// calls this.resume() when sleep is interrupt() or times out.
			}

			if (TestThreadDaemonHandler.INSTANCE.isForceStop())
			{
				this.stop();
				return;
			}
		}
	}

	@Override
	public boolean loopSafe()
	{
		this.ticks++;

		try
		{
			TestThreadTask task = this.takeNextTask();

			if (task != null)
			{
				this.processTask(task);
				this.lastTaskTime = System.currentTimeMillis();
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
	public boolean shouldPause()
	{
		if (this.hasTasks()) { return false; }
		if (this.ticks > this.maxTicks) { return true; }
		return (System.currentTimeMillis() - this.lastTaskTime) > (this.sleepDelay * 1000L);
	}

	private TestThreadTask takeNextTask() throws InterruptedException
	{
		return TestThreadDaemonHandler.INSTANCE.getNextTask();
	}

	@Override
	public void processTask(TestThreadTask task) throws InterruptedException
	{
		task.run();
		MaLiLib.LOGGER.info("processTask: completed");
	}
}
