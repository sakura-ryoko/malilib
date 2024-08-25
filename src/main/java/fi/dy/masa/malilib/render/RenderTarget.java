package fi.dy.masa.malilib.render;

import net.minecraft.client.render.RenderPhase;

import fi.dy.masa.malilib.mixin.IMixinRenderPhase;

public class RenderTarget extends RenderPhase
{
    public RenderTarget(String name, Runnable beginAction, Runnable endAction)
    {
        super(name, beginAction, endAction);
    }

    protected Target getTarget(String id)
    {
        return switch (id)
        {
            case "outline" -> RenderPhase.OUTLINE_TARGET;
            case "translucent" -> RenderPhase.TRANSLUCENT_TARGET;
            case "particles" -> RenderPhase.PARTICLES_TARGET;
            case "weather" -> RenderPhase.WEATHER_TARGET;
            case "clouds" -> RenderPhase.CLOUDS_TARGET;
            case "item_entity" -> RenderPhase.ITEM_ENTITY_TARGET;
            default -> RenderPhase.MAIN_TARGET;
        };
    }

    public Runnable getBeginAction()
    {
        return ((IMixinRenderPhase) this).malilib_getBeginAction();
    }

    public Runnable getEndAction()
    {
        return ((IMixinRenderPhase) this).malilib_getEndAction();
    }
}
