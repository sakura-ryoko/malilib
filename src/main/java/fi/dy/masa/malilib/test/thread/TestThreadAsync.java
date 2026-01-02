package fi.dy.masa.malilib.test.thread;

import java.util.concurrent.CompletableFuture;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.AsyncThreadTaskBase;

public class TestThreadAsync extends AsyncThreadTaskBase
{
	private final TestThreadData data;

	public TestThreadAsync()
	{
		super();
		this.data = new TestThreadData();
	}

	@Override
	public CompletableFuture<Void> runTask()
	{
		if (this.isFinished())
		{
			return CompletableFuture.completedFuture(null);
		}

		return CompletableFuture.runAsync(
				() ->
				{
					MaLiLib.LOGGER.info("TestThreadAsync.runTask() -- DATA: [{}]", this.data.getData());
					this.finish();
				}
		);
	}
}
