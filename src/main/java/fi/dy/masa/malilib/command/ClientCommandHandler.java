package fi.dy.masa.malilib.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.ApiStatus;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.interfaces.IClientCommandListener;
import fi.dy.masa.malilib.interfaces.ICommandDispatcher;

public class ClientCommandHandler implements ICommandDispatcher
// extends CommandHandler
{
    public static final ClientCommandHandler INSTANCE = new ClientCommandHandler();
    private final List<IClientCommandListener> commands = new ArrayList<>();

    @Override
    public void registerCommand(IClientCommandListener command)
    {
        if (!this.commands.contains(command))
        {
            if (this.checkIfAvailable(command.getCommand()))
            {
                this.commands.add(command);
            }
            else
            {
                MaLiLib.LOGGER.error("ClientCommandHandler: Tried to register a duplicate command '{}'.",
                                     command.getCommand());
            }
        }
    }

    private boolean checkIfAvailable(String command)
    {
        AtomicBoolean valid = new AtomicBoolean(true);

        this.commands.forEach(
                (handler) ->
                {
                    if (handler.getCommand().equalsIgnoreCase(command))
                    {
                        valid.set(false);
                    }
                });

        return valid.get();
    }

    @ApiStatus.Internal
    public boolean onSendClientMessage(String message, MinecraftClient mc)
    {
        if (!this.commands.isEmpty())
        {
            for (IClientCommandListener command : this.commands)
            {
                if (message.startsWith(command.getCommand()))
                {
                    List<String> args = Arrays.stream(message.split("\\s+")).toList();

                    // Double verify we have a full word match
                    if (args.getFirst().equalsIgnoreCase(command.getCommand()))
                    {
                        return command.execute(args, mc);
                    }
                }
            }
        }

        return false;
    }

//    public String[] latestAutoComplete = null;

    /**
     * Attempt to execute a command. This method should return the number of times that the command was executed. If the
     * command does not exist or if the player does not have permission, 0 will be returned. A number greater than 1 can
     * be returned if a player selector is used.
     * 
     * @return 1 if successfully executed, -1 if no permission or wrong usage, 0 if it doesn't exist
     */
    /*
    @Override
    public int executeCommand(ICommandSender sender, String message)
    {
        message = message.trim();

        boolean usedSlash = message.startsWith("/");

        if (usedSlash)
        {
            message = message.substring(1);
        }

        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        ICommand command = this.getCommands().get(commandName);
        TextFormatting red = TextFormatting.RED;

        try
        {
            if (command == null)
            {
                return 0;
            }

            if (command.checkPermission(this.getServer(), sender))
            {
                this.tryExecute(sender, args, command, message);
                return 1;
            }
            else
            {
                sender.sendMessage(this.format(red, "commands.generic.permission"));
            }
        }
        catch (Throwable t)
        {
            sender.sendMessage(this.format(red, "commands.generic.exception"));
            LiteModMaLiLib.logger.error("Command '{}' threw an exception:", message, t);
        }

        return -1;
    }

    private TextComponentTranslation format(TextFormatting color, String str, Object... args)
    {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }

    public void autoComplete(String leftOfCursor)
    {
        this.latestAutoComplete = null;

        if (leftOfCursor.charAt(0) == '/')
        {
            leftOfCursor = leftOfCursor.substring(1);

            if (GuiUtils.getCurrentScreen() instanceof GuiChat)
            {
                Minecraft mc = Minecraft.getMinecraft();
                List<String> commands = this.getTabCompletions(mc.player, leftOfCursor, mc.player.getPosition());

                if (commands.isEmpty() == false)
                {
                    TextFormatting gray = TextFormatting.GRAY;
                    TextFormatting reset = TextFormatting.RESET;

                    if (leftOfCursor.indexOf(' ') == -1)
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, gray + "/" + commands.get(i) + reset);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < commands.size(); i++)
                        {
                            commands.set(i, gray + commands.get(i) + reset);
                        }
                    }

                    this.latestAutoComplete = commands.toArray(new String[commands.size()]);
                }
            }
        }
    }

    @Override
    protected MinecraftServer getServer()
    {
        return Minecraft.getMinecraft().getIntegratedServer();
    }
    */
}
