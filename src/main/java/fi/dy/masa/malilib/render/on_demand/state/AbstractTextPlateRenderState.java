package fi.dy.masa.malilib.render.on_demand.state;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import fi.dy.masa.malilib.interfaces.IOnDemandRenderState;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.position.Vec3d;

@ApiStatus.Experimental
public abstract class AbstractTextPlateRenderState implements IOnDemandRenderState
{
	protected List<String> text;
	protected Vec3d position;
	protected float yaw;
	protected float pitch;
	protected float scale;
	protected Color4f textColor;
	protected Color4f backgroundColor;
	protected boolean disableDepth;

	public AbstractTextPlateRenderState(final List<String> text,
	                                    final Vec3d position,
	                                    final float yaw, final float pitch, final float scale,
	                                    Color4f textColor, Color4f backgroundColor,
	                                    boolean disableDepth)
	{
		this.text = text;
		this.position = position;
		this.yaw = yaw;
		this.pitch = pitch;
		this.scale = scale;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.disableDepth = disableDepth;
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

	public float yaw()
	{
		return this.yaw;
	}

	public float pitch()
	{
		return this.pitch;
	}

	public float scale()
	{
		return this.scale;
	}

	public boolean disableDepth()
	{
		return this.disableDepth;
	}
}
