package malilib.util.data;

public interface ChangeListener<T>
{
    /**
     * This method should be called before the change happens
     * @param val The object or value which is about to change
     */
    void beforeChange(T val);

    /**
     * This method should be called after the change has happened
     * @param val The object or value that changed
     */
    void afterChange(T val);
}
