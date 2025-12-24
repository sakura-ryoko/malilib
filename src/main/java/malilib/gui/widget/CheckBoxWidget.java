package malilib.gui.widget;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.render.text.StyledTextLine;
import malilib.util.data.BooleanConsumer;
import malilib.util.data.BooleanStorage;

public class CheckBoxWidget extends InteractableWidget
{
    protected final Icon iconUnchecked;
    protected final Icon iconChecked;
    @Nullable protected BooleanConsumer listener;
    protected BooleanSupplier booleanSupplier;
    protected BooleanConsumer booleanConsumer;
    protected int textColorChecked = 0xFFFFFFFF;
    protected int textColorUnchecked = 0xB0B0B0B0;
    protected boolean currentValue;

    public CheckBoxWidget(@Nullable String translationKey,
                          @Nullable String hoverInfoKey,
                          Icon iconUnchecked,
                          Icon iconChecked)
    {
        super(0, 0);

        this.canReceiveMouseClicks = true;
        this.iconUnchecked = iconUnchecked;
        this.iconChecked = iconChecked;
        this.textOffset.setYOffset(2);

        this.booleanConsumer = this::setInternalBooleanValue;
        this.booleanSupplier = this::getInternalBooleanValue;

        if (StringUtils.isBlank(translationKey) == false)
        {
            this.setText(StyledTextLine.translateFirstLine(translationKey));
        }

        int textWidth = this.text != null ? this.text.renderWidth : 0;
        int ih = iconChecked.getHeight();
        this.setWidth(iconUnchecked.getWidth() + (textWidth > 0 ? textWidth + 3 : 0));
        this.setHeight(textWidth > 0 ? Math.max(this.getFontHeight(), ih) : ih);

        this.updateCheckBoxState();

        if (hoverInfoKey != null)
        {
            this.translateAndAddHoverString(hoverInfoKey);
        }
    }

    public CheckBoxWidget(@Nullable String translationKey,
                          @Nullable String hoverInfoKey)
    {
        this(translationKey, hoverInfoKey, DefaultIcons.CHECKMARK_DARK_OFF, DefaultIcons.CHECKMARK_DARK_ON_VARIANT_1);
    }

    public CheckBoxWidget(@Nullable String translationKey,
                          BooleanStorage booleanStorage)
    {
        this(translationKey, null, booleanStorage::getBooleanValue, booleanStorage::setBooleanValue);
    }

    public CheckBoxWidget(@Nullable String translationKey,
                          @Nullable String hoverInfoKey,
                          BooleanStorage booleanStorage)
    {
        this(translationKey, hoverInfoKey, booleanStorage::getBooleanValue, booleanStorage::setBooleanValue);
    }

    public CheckBoxWidget(@Nullable String translationKey,
                          BooleanSupplier booleanSupplier,
                          BooleanConsumer booleanConsumer)
    {
        this(translationKey, null, booleanSupplier, booleanConsumer);
    }

    public CheckBoxWidget(@Nullable String translationKey,
                          @Nullable String hoverInfoKey,
                          BooleanSupplier booleanSupplier,
                          BooleanConsumer booleanConsumer)
    {
        this(translationKey, hoverInfoKey, DefaultIcons.CHECKMARK_DARK_OFF, DefaultIcons.CHECKMARK_DARK_ON_VARIANT_1);

        this.setBooleanStorage(booleanSupplier, booleanConsumer);
    }

    public CheckBoxWidget setTextColorChecked(int color)
    {
        this.textColorChecked = color;
        this.updateCheckBoxState();
        return this;
    }

    public CheckBoxWidget setTextColorUnchecked(int color)
    {
        this.textColorUnchecked = color;
        this.updateCheckBoxState();
        return this;
    }

    public CheckBoxWidget setTranslationKey(String translationKey)
    {
        this.setText(StyledTextLine.translateFirstLine(translationKey));
        return this;
    }

    public CheckBoxWidget setBooleanStorage(BooleanStorage storage)
    {
        this.setBooleanStorage(storage::getBooleanValue, storage::setBooleanValue);
        return this;
    }

    public CheckBoxWidget setBooleanStorage(BooleanSupplier booleanSupplier, BooleanConsumer booleanConsumer)
    {
        this.booleanSupplier = booleanSupplier;
        this.booleanConsumer = booleanConsumer;
        this.updateCheckBoxState();
        return this;
    }

    public CheckBoxWidget setListener(@Nullable BooleanConsumer listener)
    {
        this.listener = listener;
        return this;
    }

    public boolean isSelected()
    {
        return this.booleanSupplier.getAsBoolean();
    }

    public void setSelected(boolean selected)
    {
        this.setSelected(selected, true);
    }

    public int getIconWidth()
    {
        return this.iconUnchecked.getWidth();
    }

    @Override
    public void updateWidgetState()
    {
        this.updateCheckBoxState();
    }

    /**
     * Set the current selected value/state
     * @param notifyListener If true, then the change listener (if set) will be notified.
     * If false, then the listener will not be notified
     */
    public void setSelected(boolean selected, boolean notifyListener)
    {
        if (this.isEnabled() == false)
        {
            return;
        }

        this.booleanConsumer.accept(selected);
        this.updateCheckBoxState();

        if (notifyListener && this.listener != null)
        {
            this.listener.accept(selected);
        }
    }

    protected void updateCheckBoxState()
    {
        boolean selected = this.isSelected();
        Icon icon = selected ? this.iconChecked : this.iconUnchecked;
        int textXOffset = icon.getWidth() + 3;

        this.getTextSettings().setTextColor(selected ? this.textColorChecked : this.textColorUnchecked);
        this.getTextOffset().setXOffset(textXOffset);
        this.setIcon(icon);
    }

    protected void setInternalBooleanValue(boolean value)
    {
        this.currentValue = value;
    }

    protected boolean getInternalBooleanValue()
    {
        return this.currentValue;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.setSelected(! this.isSelected());
        return true;
    }
}
