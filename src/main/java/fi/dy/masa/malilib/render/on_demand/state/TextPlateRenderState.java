package fi.dy.masa.malilib.render.on_demand.state;

import java.util.List;
import org.jspecify.annotations.NonNull;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.util.MathUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;
import fi.dy.masa.malilib.util.text.TextAlignment;

public class TextPlateRenderState extends AbstractTextPlateRenderState
{
	private int strLenHalf;

	public TextPlateRenderState(List<String> text, Vec3d position, float scale)
	{
		this(text, position, scale, TextAlignment.CENTER);
	}

	public TextPlateRenderState(List<String> text, Vec3d position, float scale,
	                            TextAlignment alignment)
	{
		this(text, position, scale, Color4f.WHITE, alignment);
	}

	public TextPlateRenderState(List<String> text, Vec3d position, float scale,
	                            Color4f textColor,
	                            TextAlignment alignment)
	{
		this(text, position, scale, textColor, false, alignment);
	}

	public TextPlateRenderState(List<String> text, Vec3d position, float scale,
	                            boolean disableDepth,
	                            TextAlignment alignment)
	{
		this(text, position, scale, Color4f.WHITE, Color4f.fromColor(0x40000000), disableDepth, alignment);
	}

	public TextPlateRenderState(List<String> text, Vec3d position, float scale,
	                            Color4f textColor,
	                            boolean disableDepth,
	                            TextAlignment alignment)
	{
		this(text, position, scale, textColor, Color4f.fromColor(0x40000000), disableDepth, alignment);
	}

	public TextPlateRenderState(List<String> text, Vec3d position, float scale,
	                            Color4f textColor, Color4f backgroundColor,
	                            boolean disableDepth,
	                            TextAlignment alignment)
	{
		this(text, position, scale, textColor, backgroundColor, disableDepth, false, alignment);
	}

	public TextPlateRenderState(List<String> text, Vec3d position, float scale,
	                            Color4f textColor, Color4f backgroundColor,
	                            boolean disableDepth, boolean useShadow,
	                            TextAlignment alignment)
	{
		this(text, position, scale, textColor, backgroundColor, 15728880, disableDepth, useShadow, alignment);
	}

	public TextPlateRenderState(List<String> text, Vec3d position, float scale,
	                            Color4f textColor, Color4f backgroundColor,
	                            int light, boolean disableDepth, boolean useShadow,
	                            TextAlignment alignment)
	{
		super(text, position, scale, textColor, backgroundColor, light, disableDepth, useShadow, alignment);
	}

	@Override
	public @NonNull RenderPipeline pipeline()
	{
		return this.seeThrough ? MaLiLibPipelines.TEXT_PLATE_BG_MASA_NO_DEPTH : MaLiLibPipelines.TEXT_PLATE_BG_MASA;
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

		this.strLenHalf = maxLineLen / 2;
		int textHeight = font.lineHeight * this.text.size() - 1;
		int bga = ((bgColor >>> 24) & 0xFF);
		int bgr = ((bgColor >>> 16) & 0xFF);
		int bgg = ((bgColor >>> 8) & 0xFF);
		int bgb = (bgColor & 0xFF);

		consumer.addVertex((float) (-this.strLenHalf - 1), (float) -1, 0.0F).setColor(bgr, bgg, bgb, bga);
		consumer.addVertex((float) (-this.strLenHalf - 1), (float) textHeight, 0.0F).setColor(bgr, bgg, bgb, bga);
		consumer.addVertex((float) this.strLenHalf, (float) textHeight, 0.0F).setColor(bgr, bgg, bgb, bga);
		consumer.addVertex((float) this.strLenHalf, (float) -1, 0.0F).setColor(bgr, bgg, bgb, bga);
	}
}
