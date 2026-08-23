package fi.dy.masa.malilib.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.util.data.Color4f;

@ApiStatus.Experimental
public interface IOnDemandRenderState
{
	@Nonnull
	RenderPipeline pipeline();

//	default int formatIndex() {return 0;}

	default @Nullable Identifier texture() {return null;}

	default int textureId() {return -1;}

	default int textureWidth() {return -1;}

	default int textureHeight() {return -1;}

//	default boolean bindOverlay() { return false; }
//
//	default boolean bindLightmap() { return false; }

//	default List<MaLiLibComplexBinding> complexTextures() { return List.of(); }

	default @Nullable Color4f getColor() {return null;}

	default @Nonnull Color4f color() {return Color4f.WHITE;}

	default @Nonnull float[] offset() {return new float[0];}

	default float lineWidth() {return 1.0f;}

	double x();

	double y();

	double z();

	void update(VertexConsumer consumer);
}
