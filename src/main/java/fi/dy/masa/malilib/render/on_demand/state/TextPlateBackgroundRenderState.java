package fi.dy.masa.malilib.render.on_demand.state;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

@ApiStatus.Experimental
public class TextPlateBackgroundRenderState extends AbstractTextPlateRenderState
{
	private static int strLenHalf;

	public TextPlateBackgroundRenderState(List<String> text, Vec3d position,
	                                      float yaw, float pitch, float scale,
	                                      Color4f textColor, Color4f backgroundColor,
	                                      boolean disableDepth)
	{
		super(text, position, yaw, pitch, scale, textColor, backgroundColor, disableDepth);
	}

	@Override
	public @NonNull RenderPipeline pipeline()
	{
		return this.disableDepth ? MaLiLibPipelines.TEXT_PLATE_MASA_NO_DEPTH : MaLiLibPipelines.TEXT_PLATE_MASA;
	}

	public int strLenHalf()
	{
		return strLenHalf;
	}

	@Override
	public void update(VertexConsumer consumer)
	{
		final int bgColor = this.backgroundColor().getIntValue();
		Font font = Minecraft.getInstance().font;
		int maxLineLen = 0;

		for (String line : this.text)
		{
			maxLineLen = MathUtils.max(maxLineLen, font.width(line));
		}

		strLenHalf = maxLineLen / 2;
		int textHeight = font.lineHeight * this.text.size() - 1;
		int bga = ((bgColor >>> 24) & 0xFF);
		int bgr = ((bgColor >>> 16) & 0xFF);
		int bgg = ((bgColor >>> 8) & 0xFF);
		int bgb = (bgColor & 0xFF);

		consumer.addVertex((float) (-strLenHalf - 1), (float) -1, 0.0F).setColor(bgr, bgg, bgb, bga);
		consumer.addVertex((float) (-strLenHalf - 1), (float) textHeight, 0.0F).setColor(bgr, bgg, bgb, bga);
		consumer.addVertex((float) strLenHalf, (float) textHeight, 0.0F).setColor(bgr, bgg, bgb, bga);
		consumer.addVertex((float) strLenHalf, (float) -1, 0.0F).setColor(bgr, bgg, bgb, bga);
	}
}
