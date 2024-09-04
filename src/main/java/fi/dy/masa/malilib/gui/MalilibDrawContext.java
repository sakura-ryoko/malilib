package fi.dy.masa.malilib.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;

import fi.dy.masa.malilib.mixin.IMixinDrawContext;

/**
 * No longer required past 1.21
 */
public class MalilibDrawContext extends DrawContext
{
    public MalilibDrawContext(MinecraftClient client, Immediate vertexConsumers)
    {
        super(client, vertexConsumers);
    }

    @Override
    public void draw()
    {
        // Omit the disableDepthTest() call >_>
        //this.getVertexConsumers().draw();
        ((IMixinDrawContext) this).malilib_getVertexConsumers().draw();
    }
}
