package fi.dy.masa.malilib.test.thread;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.DefaultThreadTaskBase;

public class TestThreadTaskDefault extends DefaultThreadTaskBase
{
	private final TestThreadData data;
	private final Runnable task;

	public TestThreadTaskDefault(Runnable task)
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
