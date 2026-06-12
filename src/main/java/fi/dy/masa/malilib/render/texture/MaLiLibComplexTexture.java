package fi.dy.masa.malilib.render.texture;

import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;

public record MaLiLibComplexTexture(String name, GpuTextureView texture, GpuSampler sampler)
{
}
