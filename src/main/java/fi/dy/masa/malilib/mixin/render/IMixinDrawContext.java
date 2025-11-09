package fi.dy.masa.malilib.mixin.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiGraphics.class)
public interface IMixinDrawContext
{
//    @Accessor("vertexConsumers")
//    VertexConsumerProvider.Immediate malilib_getVertexConsumers();

    @Accessor("guiRenderState")
    GuiRenderState malilib_getRenderState();

    @Accessor("scissorStack")
    GuiGraphics.ScissorStack malilib_getScissorStack();
}
