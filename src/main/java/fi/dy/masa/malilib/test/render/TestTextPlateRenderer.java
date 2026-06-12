package fi.dy.masa.malilib.test.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.position.Vec3d;

public class TestTextPlateRenderer
{
	public static final TestTextPlateRenderer INSTANCE = new TestTextPlateRenderer();
	private final CopyOnWriteArrayList<Entity> nearbyEntities;
	private final List<String> text;

	private TestTextPlateRenderer()
	{
		this.nearbyEntities = new CopyOnWriteArrayList<>();
		this.text = new ArrayList<>();
		this.buildText();
	}

	private void buildText()
	{
		this.text.clear();
		this.text.add("A Horse");
		this.text.add("Of course");
		this.text.add("The Famous");
		this.text.add("Mr Ed");
	}

	private void scanForEntities(Minecraft mc)
	{
		ClientLevel level = mc.level;
		Entity camera = mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player;
		if (camera == null) { return; }
		Vec3 pos = camera.position();
		AABB bb = new AABB(pos, pos).inflate(16);

		this.nearbyEntities.clear();

		if (level != null)
		{
			List<Horse> nearbyHorses = level.getEntitiesOfClass(Horse.class, bb);

			if (!nearbyHorses.isEmpty())
			{
				this.nearbyEntities.addAll(nearbyHorses);
			}
		}
	}

	public void update(Minecraft mc)
	{
		if (mc.level != null)
		{
			this.scanForEntities(mc);
		}
	}

	public boolean shouldRender()
	{
		return !this.nearbyEntities.isEmpty();
	}

	public void render(Vec3 camPos, Minecraft mc, ProfilerFiller profiler)
	{
		if (this.shouldRender())
		{
			this.nearbyEntities.forEach(e -> this.renderEach(e, camPos, mc));
		}
	}

	private void renderEach(Entity e, Vec3 camPos, Minecraft mc)
	{
		float delta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		Vec3 targetPos = e.getPosition(delta);
		double hypot = Mth.length(camPos.x() - targetPos.x(), camPos.z() - targetPos.z());
		double distance = 0.8;
		double x = targetPos.x() + (camPos.x() - targetPos.x()) / hypot * distance;
		double z = targetPos.z() + (camPos.z() - targetPos.z()) / hypot * distance;
		double y = targetPos.y() + 1.5 + 0.1 * this.text.size();
		final float scale = 2.0f * 0.01F;       // 2.0f is configurable SCALE -- do not modify the 0.01F

//		RenderUtils.scheduleTextPlate(this.text, new Vec3d(x, y, z), scale, Color4f.WHITE, Color4f.fromColor(0x40000000), 15728880, false, false, TextAlignment.CENTER);
		RenderUtils.scheduleTextPlate(this.text, new Vec3d(x, y, z), scale);
	}
}
