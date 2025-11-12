package fi.dy.masa.malilib.interfaces;

public interface IStringConsumerFeedback
{
    /**
     * 
     * @param string ()
     * @return true if the operation succeeded, false if there was some kind of an error
     */
    boolean setString(String string);
}
