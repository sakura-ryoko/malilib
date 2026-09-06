package fi.dy.masa.malilib.interfaces;

import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface IPathListConsumerFeedback
{
	/**
	 *
	 * @param sources ()
	 * @return true if the operation succeeded, false if there was some kind of an error
	 */
	boolean setPaths(List<Path> sources);
}
