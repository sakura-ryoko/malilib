package fi.dy.masa.malilib.gui.interfaces;

public interface ISliderCallback
{
    /**
     * Maximum number of values/steps the underlying data can have.
     * Return Integer.MAX_VALUE for unlimited/non-specified, like double data type ranges.
     * @return
     */
    int getMaxSteps();

    /**
     * Returns the relative value (within the min - max range)
     * @return
     */
    double getValueRelative();

    /**
     * Sets the value
     * @param relativeValue)
     */
    void setValueRelative(double relativeValue);

    /**
     * Returns the formatted display string for the current value
     * @return
     */
    String getFormattedDisplayValue();
}
