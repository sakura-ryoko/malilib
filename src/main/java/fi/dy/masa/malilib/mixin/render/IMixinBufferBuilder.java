package fi.dy.masa.malilib.mixin.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BufferBuilder.class)
public interface IMixinBufferBuilder
{
    @Accessor("building")
    boolean malilib_isBuilding();

    @Accessor("vertices")
    int malilib_getVertexCount();

    @Accessor("vertexPointer")
    long malilib_getVertexPointer();
}
