package fi.dy.masa.malilib.render;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.RenderPhase;

/**
 * Kind of a lame hack to create a Pseudo RenderPhase
 */
@ApiStatus.Experimental
public class RenderTarget extends RenderPhase
{
    private final String targetName;

    public RenderTarget(String name, Runnable beginAction, Runnable endAction)
    {
        super(name, beginAction, endAction);
        this.targetName = name;
    }

    public void startDrawing(String name)
    {
        if (this.targetName.equals(name))
        {
            this.startDrawing();
        }
    }

    public void endDrawing(String name)
    {
        if (this.targetName.equals(name))
        {
            this.endDrawing();
        }
    }
}
