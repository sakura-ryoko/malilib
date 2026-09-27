package fi.dy.masa.malilib.gui.button;

@Deprecated // See IButtonActionHandler instead --
public interface IButtonActionListener extends IButtonActionHandler
{
    void actionPerformedWithButton(ButtonBase button, int mouseButton);

    @Override
    default boolean handleAction(ButtonBase button, int mouseButton) {
        this.actionPerformedWithButton(button, mouseButton);
            return true;
    }
}
