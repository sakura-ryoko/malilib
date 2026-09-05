package fi.dy.masa.malilib.render.texture;

import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;

public record MaLiLibComplexTexture(String name, GpuTextureView texture, GpuSampler sampler)
{
}
