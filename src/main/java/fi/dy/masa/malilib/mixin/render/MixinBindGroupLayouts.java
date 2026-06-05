package fi.dy.masa.malilib.mixin.render;

import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.UniformType;
import net.minecraft.client.renderer.BindGroupLayouts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.malilib.render.MaLiLibPipelines;

@Mixin(BindGroupLayouts.class)
public class MixinBindGroupLayouts
{
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void registerBindGroups(CallbackInfo ci)
	{
		MaLiLibPipelines.LEGACY_TERRAIN_GROUP = BindGroupLayout.builder().withUniform("ChunkFix", UniformType.UNIFORM_BUFFER).build();
	}
}
