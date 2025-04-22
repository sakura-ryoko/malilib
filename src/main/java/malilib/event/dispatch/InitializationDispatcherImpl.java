package malilib.event.dispatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import malilib.MaLiLibInitHandler;
import malilib.config.util.ConfigUtils;
import malilib.event.InitializationHandler;

public class InitializationDispatcherImpl implements InitializationDispatcher
{
    protected final List<InitializationHandler> handlers = new ArrayList<>();

    public InitializationDispatcherImpl()
    {
    }

    @Override
    public void registerInitializationHandler(InitializationHandler handler)
    {
        if (this.handlers.contains(handler) == false)
        {
            this.handlers.add(handler);
            this.handlers.sort(Comparator.comparing(InitializationHandler::getPriority));
        }
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onGameInitDone()
    {
        // Register malilib handlers before any other mod handlers
        MaLiLibInitHandler.registerMalilibHandlers();

        if (this.handlers.isEmpty() == false)
        {
            for (InitializationHandler handler : this.handlers)
            {
                handler.registerModHandlers();
            }
        }

        ConfigUtils.loadAllConfigsFromFile();
    }
}
