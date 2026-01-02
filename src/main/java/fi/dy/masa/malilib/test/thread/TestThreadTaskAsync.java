package fi.dy.masa.malilib.test.thread;

import java.util.concurrent.CompletableFuture;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.AsyncThreadTaskBase;

public class TestThreadTaskAsync extends AsyncThreadTaskBase
{
	private final TestThreadData data;
	private final Runnable task;

	public TestThreadTaskAsync(Runnable task)
	{
		super();
		this.data = new TestThreadData();
		this.task = task;
	}

	public void setData(String newString)
	{
		this.data.setData(newString);
	}

	@Override
	public CompletableFuture<Void> runAsync()
	{
		if (this.isFinished())
		{
			MaLiLib.LOGGER.info("TestThreadTaskAsync: is finished.");
			return CompletableFuture.completedFuture(null);
		}

		return CompletableFuture.runAsync(
				() ->
				{
					MaLiLib.LOGGER.info("TestThreadTaskAsync: is started.");
					this.task.run();
					this.finish();
					MaLiLib.LOGGER.info("TestThreadTaskAsync.run() -- DATA: [{}]", this.data.getData());
				}
		);
	}
}
