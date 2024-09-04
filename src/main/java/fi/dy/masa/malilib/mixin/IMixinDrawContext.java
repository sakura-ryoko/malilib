package fi.dy.masa.malilib.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DrawContext.class)
public interface IMixinDrawContext
{
    @Accessor("vertexConsumers")
    VertexConsumerProvider.Immediate malilib_getVertexConsumers();
}
