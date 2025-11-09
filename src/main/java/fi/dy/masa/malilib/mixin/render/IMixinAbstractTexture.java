package fi.dy.masa.malilib.mixin.render;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface IMixinAbstractTexture
{
    @Accessor("texture")
    GpuTexture malilib_getGlTexture();

    @Accessor("textureView")
    GpuTextureView malilib_getGlTextureView();
}
