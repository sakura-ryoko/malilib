package fi.dy.masa.malilib.test.command;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibFabricData;
import fi.dy.masa.malilib.interfaces.IClientCommandListener;
import fi.dy.masa.malilib.util.time.TimeTestExample;

public class TestCommand implements IClientCommandListener
{
    @Override
    public String getCommand()
    {
        return "#test";
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
        else if (op.equalsIgnoreCase("mods"))
        {
            mc.gui.getChat().addMessage(Component.nullToEmpty(this.getModList()));
            return true;
        }

        return op.equalsIgnoreCase("cancel");
    }

    private String getModList()
    {
        StringBuilder builder = new StringBuilder();
        int count = 0;

        for (String mod : MaLiLibFabricData.ALL_MOD_VERSIONS.keySet())
        {
            String version = MaLiLibFabricData.ALL_MOD_VERSIONS.get(mod);

            builder.append(String.format("§f[§b%03d§f]: §d", count++));
            builder.append(mod).append("§r §f/ §e").append(version).append("§r\n");
        }

        return builder.toString();
    }
}
