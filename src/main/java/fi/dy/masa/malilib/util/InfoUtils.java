package fi.dy.masa.malilib.util;

import org.jetbrains.annotations.ApiStatus;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.MessageRenderer;
import fi.dy.masa.malilib.util.game.IGameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class InfoUtils
{
    private static final MessageRenderer IN_GAME_MESSAGES = new MessageRenderer(0xA0000000, 0).setBackgroundStyle(true, false).setCentered(true, false).setExpandUp(true);

    public static final IStringConsumer INFO_MESSAGE_CONSUMER = new InfoMessageConsumer();

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * @param type ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * @param type ()
     * @param lifeTime ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise prints the message to the action bar.
     * @param type ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiOrActionBarMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiOrActionBarMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise, prints the message to the action bar.
     * @param type ()
     * @param lifeTime ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiOrActionBarMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            String msg = type.getFormatting() + StringUtils.translate(translationKey, args) + GuiBase.TXT_RST;
            printActionbarMessage(msg);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise, adds the message to the in-game message handler.
     * @param type ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiOrInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiOrInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise, adds the message to the in-game message handler.
     * @param type ()
     * @param lifeTime ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiOrInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            showInGameMessage(type, lifeTime, translationKey, args);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * Also shows the message in the in-game message box.
     * @param type ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiAndInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiAndInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * Also shows the message in the in-game message box.
     * @param type ()
     * @param lifeTime ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showGuiAndInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        showGuiMessage(type, lifeTime, translationKey, args);
        showInGameMessage(type, lifeTime, translationKey, args);
    }

    public static void printActionbarMessage(String key, Object... args)
    {
        if (MaLiLibConfigs.Generic.ENABLE_ACTIONBAR_MESSAGES.getBooleanValue())
        {
            sendVanillaMessage(Text.translatable(key, args));
        }
    }

    /**
     * Adds the message to the in-game message handler
     * @param type ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showInGameMessage(type, calcMessageTimeout(), translationKey, args);
    }

    private static long calcMessageTimeout()
    {
        return (long) (MaLiLibConfigs.Generic.IN_GAME_MESSAGE_TIMEOUT.getFloatValue() * 1000L);
    }

    /**
     * Adds the message to the in-game message handler
     * @param type ()
     * @param lifeTime ()
     * @param translationKey ()
     * @param args ()
     */
    public static void showInGameMessage(MessageType type, long lifeTime, String translationKey, Object... args)
    {
        IN_GAME_MESSAGES.addMessage(type, lifeTime, translationKey, args);
    }

    public static void printBooleanConfigToggleMessage(String prettyName, boolean newValue)
    {
        String pre = newValue ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
        String status = StringUtils.translate("malilib.message.value." + (newValue ? "on" : "off"));
        String message = StringUtils.translate("malilib.message.toggled", prettyName, pre + status + GuiBase.TXT_RST);

        printActionbarMessage(message);
    }

    @ApiStatus.Internal
    public static void renderInGameMessages(DrawContext drawContext)
    {
        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() - 76;

        IN_GAME_MESSAGES.drawMessages(drawContext, x, y);
    }

    public static void sendVanillaMessage(MutableText message)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        World world = mc.world;

        if (world != null)
        {
            mc.inGameHud.setOverlayMessage(message, false);

            if (MaLiLibConfigs.Generic.ACTIONBAR_HUD_TICKS.isModified())
            {
                ((IGameHud) mc.inGameHud).malilib$setOverlayRemaining(MaLiLibConfigs.Generic.ACTIONBAR_HUD_TICKS.getIntegerValue());
            }
        }
    }

    public static class InfoMessageConsumer implements IStringConsumer
    {
        @Override
        public void setString(String string)
        {
            printActionbarMessage(string);
        }
    }
}
