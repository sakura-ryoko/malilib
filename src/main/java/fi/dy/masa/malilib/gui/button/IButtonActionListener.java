package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.util.input.ScanCodes;

public interface IButtonActionListener
{
    static IButtonActionListener handling(HandlingActionListener actionListener) {
        return new IButtonActionListener() {
            @Override
            public boolean handleAction(ButtonBase button, int mouseButton) {
                return actionListener.handleAction(button, mouseButton);
            }
        };
    }
    static IButtonActionListener simple(SimpleActionListener actionListener) {
        return new IButtonActionListener() {
            @Override
            public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
                actionListener.handleAction(button, mouseButton);
            }
        };
    }

    interface HandlingActionListener {
        boolean handleAction(ButtonBase button, int mouseButton);
    }

    interface SimpleActionListener {
        void handleAction(ButtonBase button, int mouseButton);
    }

    default void actionPerformedWithButton(ButtonBase button, int mouseButton)
    {

    }

    default boolean handleAction(ButtonBase button, int mouseButton) {
        if (mouseButton == ScanCodes.OFFSET_MOUSE_LEFT)
        {
            this.actionPerformedWithButton(button, mouseButton);
            return true;
        }
        return false;
    }
}
