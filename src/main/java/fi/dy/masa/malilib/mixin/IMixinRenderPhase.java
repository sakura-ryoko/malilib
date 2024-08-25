package fi.dy.masa.malilib.mixin;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RenderPhase.class)
public interface IMixinRenderPhase
{
    @Accessor("beginAction")
    Runnable malilib_getBeginAction();

    @Accessor("endAction")
    Runnable malilib_getEndAction();
}
