package fi.dy.masa.malilib.test.thread;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.thread.ThreadTaskBase;

public class TestThreadTask extends ThreadTaskBase
{
	private final TestThreadData data;
	private final Runnable task;

	public TestThreadTask(Runnable task)
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
	public void run()
	{
		if (this.isFinished())
		{
			MaLiLib.LOGGER.info("TestThreadTaskDefault: is finished.");
			return;
		}

		MaLiLib.LOGGER.info("TestThreadTaskDefault: is started.");
		this.task.run();
		this.finish();
		MaLiLib.LOGGER.info("TestThreadTaskDefault.run() -- DATA: [{}]", this.data.getData());
	}
}
