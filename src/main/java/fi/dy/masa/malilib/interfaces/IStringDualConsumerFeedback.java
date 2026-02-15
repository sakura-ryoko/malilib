package fi.dy.masa.malilib.interfaces;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface IStringDualConsumerFeedback
{
    /**
     * 
     * @param string1 ()
     * @param string2 ()
     * @return true if the operation succeeded, false if there was some kind of an error
     */
    boolean setStrings(String string1, String string2);
}
