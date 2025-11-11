package fi.dy.masa.malilib.interfaces;

import java.util.List;
import net.minecraft.client.MinecraftClient;

public interface IClientCommandListener
{
    /**
     * Return the value of the command; such as the `/<command>' or '#<command>'
     * --> NOTE: That if you are using `/` Vanilla may still throw an invalid command error.
     * @return (string of command)
     */
    String getCommand();

    /**
     * Execute the command with args as a callback;
     * return if it should cancel further processing.
     * @return (True|False)
     */
    boolean execute(List<String> args, MinecraftClient mc);
}
