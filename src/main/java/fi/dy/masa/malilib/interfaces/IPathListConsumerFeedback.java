package fi.dy.masa.malilib.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface IPathListConsumerFeedback
{
	/**
	 * @param sources ()
	 * @return true if the operation succeeded, false if there was some kind of an error
	 */
	boolean onSetPathsCompleted(List<Path> sources);

	/**
	 * Called when a task wants to inform a listener about the task being aborted before completion
	 */
	default void onSetPathsAborted() {}
}
