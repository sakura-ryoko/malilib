package fi.dy.masa.malilib.render.on_demand.state;

import java.util.List;
import org.jspecify.annotations.NonNull;

import fi.dy.masa.malilib.interfaces.IOnDemandRenderState;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;
import fi.dy.masa.malilib.util.text.TextAlignment;

public abstract class AbstractTextPlateRenderState implements IOnDemandRenderState
{
	protected List<String> text;
	protected Vec3d position;
	protected float scale;
	protected Color4f textColor;
	protected Color4f backgroundColor;
	protected int light;
	protected boolean seeThrough;
	protected boolean useShadow;
	protected TextAlignment alignment;

	protected AbstractTextPlateRenderState(final List<String> text,
	                                       final Vec3d position,
	                                       final float scale,
	                                       Color4f textColor, Color4f backgroundColor,
	                                       final int light,
	                                       boolean seeThrough, boolean useShadow,
	                                       final TextAlignment alignment)
	{
		this.text = text;
		this.position = position;
		this.scale = scale;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.light = light;
		this.seeThrough = seeThrough;
		this.useShadow = useShadow;
		this.alignment = alignment;
	}

	@Override
	public double x()
	{
		return this.position.getX();
	}

	@Override
	public double y()
	{
		return this.position.getY();
	}

	@Override
	public double z()
	{
		return this.position.getZ();
	}

	@Override
	public @NonNull Color4f color()
	{
		return this.textColor;
	}

	public List<String> text()
	{
		return this.text;
	}

	public Vec3d position()
	{
		return this.position;
	}

	public Color4f textColor()
	{
		return this.textColor;
	}

	public Color4f backgroundColor()
	{
		return this.backgroundColor;
	}

	public float scale()
	{
		return this.scale;
	}

	public int light()
	{
		return this.light;
	}

	public boolean seeThrough()
	{
		return this.seeThrough;
	}

	public boolean useShadow()
	{
		return this.useShadow;
	}

	public TextAlignment alignment()
	{
		return this.alignment;
	}
}
