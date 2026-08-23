package fi.dy.masa.malilib.util.data;

import fi.dy.masa.malilib.util.position.Vec3d;

@FunctionalInterface
public interface Vec3dConsumer
{
	void accept(Vec3d value);
}
