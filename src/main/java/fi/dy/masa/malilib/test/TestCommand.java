package fi.dy.masa.malilib.test;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
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
    public boolean execute(List<String> args, MinecraftClient mc)
    {
        MaLiLib.LOGGER.warn("TestCommand - execute with args: {}", args.toString());
        String op = args.get(1);

        if (op.equalsIgnoreCase("date") || op.equalsIgnoreCase("time"))
        {
            mc.inGameHud.getChatHud().addMessage(Text.of(TimeTestExample.runTimeDateTest()));
            return true;
        }
        else if (op.equalsIgnoreCase("duration"))
        {
            mc.inGameHud.getChatHud().addMessage(Text.of(TimeTestExample.runDurationTest()));
            return true;
        }

        return op.equalsIgnoreCase("cancel");
    }
}
