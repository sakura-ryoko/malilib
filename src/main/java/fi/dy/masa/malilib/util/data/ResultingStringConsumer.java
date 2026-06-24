package fi.dy.masa.malilib.util.data;

@FunctionalInterface
public interface ResultingStringConsumer
{
    /**
     * Consumes the provided string, and returns true on success, or false on failure
     * @param string the input string argument
     * @return true if the operation succeeded, false if it failed
     */
    boolean consumeString(String string);
}
