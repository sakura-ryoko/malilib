package fi.dy.masa.malilib.test;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IClientCommandListener;
import fi.dy.masa.malilib.util.time.TimeTestExample;

public class TestCommand implements IClientCommandListener
{
    @Override
    public String getCommand()
    {
        return "#test-cmd";
    }

    @Override
    public boolean execute(List<String> args, Minecraft mc)
    {
        MaLiLib.LOGGER.warn("TestCommand - execute with args: {}", args.toString());
        String op = args.get(1);

        if (op.equalsIgnoreCase("date") || op.equalsIgnoreCase("time"))
        {
            mc.gui.getChat().addMessage(Component.nullToEmpty(TimeTestExample.runTimeDateTest()));
            return true;
        }
        else if (op.equalsIgnoreCase("duration"))
        {
            mc.gui.getChat().addMessage(Component.nullToEmpty(TimeTestExample.runDurationTest()));
            return true;
        }

        return op.equalsIgnoreCase("cancel");
    }
}
