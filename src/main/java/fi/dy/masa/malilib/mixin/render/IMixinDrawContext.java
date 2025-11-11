package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DrawContext.class)
public interface IMixinDrawContext
{
//    @Accessor("vertexConsumers")
//    VertexConsumerProvider.Immediate malilib_getVertexConsumers();

    @Accessor("state")
    GuiRenderState malilib_getRenderState();

    @Accessor("scissorStack")
    DrawContext.ScissorStack malilib_getScissorStack();
}
