package fi.dy.masa.malilib.render.texture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.resources.Identifier;

public record MaLiLibComplexBinding(String name, Identifier location, Supplier<GpuSampler> sampler)
{
	public static MaLiLibComplexBinding fromVanilla(String name, RenderSetup.TextureBinding binding)
	{
		return new MaLiLibComplexBinding(name, binding.location(), binding.sampler());
	}

	public static List<MaLiLibComplexBinding> fromRenderSetup(@Nonnull RenderSetup setup)
	{
		List<MaLiLibComplexBinding> textures = new ArrayList<>();
		setup.textures.forEach((name, binding) -> textures.add(fromVanilla(name, binding)));
		return textures;
	}
}
