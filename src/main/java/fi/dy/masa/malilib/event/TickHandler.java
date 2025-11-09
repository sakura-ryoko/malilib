package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;

public class TickHandler
{
    private static final TickHandler INSTANCE = new TickHandler();

    private final List<IClientTickHandler> clientTickHandlers = new ArrayList<>();

    public static TickHandler getInstance()
    {
        return INSTANCE;
    }

    public void registerClientTickHandler(IClientTickHandler handler)
    {
        if (this.clientTickHandlers.contains(handler) == false)
        {
            this.clientTickHandlers.add(handler);
        }
    }

    @ApiStatus.Internal
    public void onClientTick(Minecraft mc)
    {
        if (this.clientTickHandlers.isEmpty() == false)
        {
            for (IClientTickHandler handler : this.clientTickHandlers)
            {
                handler.onClientTick(mc);
            }
        }
    }
}
