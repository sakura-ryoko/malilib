package fi.dy.masa.malilib.mixin.render;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface IMixinAbstractTexture
{
    @Accessor("glTexture")
    GpuTexture malilib_getGlTexture();
}
