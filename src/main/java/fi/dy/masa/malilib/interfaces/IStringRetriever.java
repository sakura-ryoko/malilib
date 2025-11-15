package fi.dy.masa.malilib.interfaces;

public interface IStringRetriever<T>
{
    /**
     * Returns a string representation of the given value.
     * This may be different than just calling toString().
     * @param entry ()
     * @return ()
     */
    String getStringValue(T entry);
}
