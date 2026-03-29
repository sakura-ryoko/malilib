package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.resources.Identifier;

import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public interface IOnDemandRenderState
{
	@Nonnull RenderPipeline pipeline();

	@Nullable Identifier texture();

	int textureId();

	int textureWidth();

	int textureHeight();

	@Nonnull Color4f color();

	@Nonnull float[] offset();

	void update(VertexConsumer consumer);
}
