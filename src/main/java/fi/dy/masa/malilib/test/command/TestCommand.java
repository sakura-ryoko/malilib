package fi.dy.masa.malilib.test.command;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibFabricData;
import fi.dy.masa.malilib.compat.carpet.CarpetCompat;
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
            this.msg(mc, Component.nullToEmpty(TimeTestExample.runTimeDateTest()));
            return true;
        }
        else if (op.equalsIgnoreCase("duration"))
        {
            this.msg(mc, Component.nullToEmpty(TimeTestExample.runDurationTest()));
            return true;
        }
        else if (op.equalsIgnoreCase("mods"))
        {
            this.msg(mc, Component.nullToEmpty(this.getModList()));
            return true;
        }
        else if (op.equalsIgnoreCase("carpet") && args.size() > 2)
        {
            String carpetRule = args.get(2);
            return this.displayCarpetRule(mc, carpetRule);
        }
        else if (op.equalsIgnoreCase("carpet-tis") && args.size() > 2)
        {
            String carpetRule = args.get(2);
            return this.displayCarpetTisRule(mc, carpetRule);
        }

        return op.equalsIgnoreCase("cancel");
    }

    private void msg(Minecraft mc, Component text)
    {
        mc.gui.hud.getChat().addClientSystemMessage(text);
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

    private boolean displayCarpetRule(Minecraft mc, String rule)
    {
        if (CarpetCompat.isCarpetLoaded)
        {
            Object obj = CarpetCompat.getCarpetRuleValue(rule);

            if (obj == null)
            {
                String result = "§6Carpet Rule not found§r";
                this.msg(mc, Component.nullToEmpty(result));
            }
            else
            {
                String result = String.format("Carpet Rule: §b%s§r, Value: §d%s§r", rule, obj);
                this.msg(mc, Component.nullToEmpty(result));
            }
        }
        else
        {
            String result = "§cCarpet not found§r";
            this.msg(mc, Component.nullToEmpty(result));
        }

        return true;
    }

    private boolean displayCarpetTisRule(Minecraft mc, String rule)
    {
        if (CarpetCompat.isCarpetTisLoaded)
        {
            Object obj = CarpetCompat.getCarpetTisRuleValue(rule);

            if (obj == null)
            {
                String result = "§6CarpetTIS Rule not found§r";
                this.msg(mc, Component.nullToEmpty(result));
            }
            else
            {
                String result = String.format("CarpetTIS Rule: §b%s§r, Value: §d%s§r", rule, obj);
                this.msg(mc, Component.nullToEmpty(result));
            }
        }
        else
        {
            String result = "§cCarpetTIS not found§r";
            this.msg(mc, Component.nullToEmpty(result));
        }
        return true;
    }
}
