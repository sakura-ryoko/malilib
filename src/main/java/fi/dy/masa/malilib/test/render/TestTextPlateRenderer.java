package fi.dy.masa.malilib.test.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import fi.dy.masa.malilib.render.RenderUtils;

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

	private void scanForEntities(MinecraftClient mc)
	{
		ClientWorld level = mc.world;
		Entity camera = mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player;
		if (camera == null) { return; }
		Vec3d pos = camera.getEntityPos();
		Box bb = new Box(pos, pos).expand(16);

		this.nearbyEntities.clear();

		if (level != null)
		{
			List<HorseEntity> nearbyHorses = level.getNonSpectatingEntities(HorseEntity.class, bb);

			if (!nearbyHorses.isEmpty())
			{
				this.nearbyEntities.addAll(nearbyHorses);
			}
		}
	}

	public void update(MinecraftClient mc)
	{
		if (mc.world != null)
		{
			this.scanForEntities(mc);
		}
	}

	public boolean shouldRender()
	{
		return !this.nearbyEntities.isEmpty();
	}

	public void render(Vec3d camPos, MinecraftClient mc, Profiler profiler)
	{
		if (this.shouldRender())
		{
			this.nearbyEntities.forEach(e -> this.renderEach(e, camPos, mc));
		}
	}

	private void renderEach(Entity e, Vec3d camPos, MinecraftClient mc)
	{
		float delta = mc.getRenderTickCounter().getTickProgress(true);
		Vec3d targetPos = e.getLerpedPos(delta);
		double hypot = MathHelper.hypot(camPos.x - targetPos.x, camPos.z - targetPos.z);
		double distance = 0.8;
		double x = targetPos.x + (camPos.x - targetPos.x) / hypot * distance;
		double z = targetPos.z + (camPos.z - targetPos.z) / hypot * distance;
		double y = targetPos.y + 1.5 + 0.1 * this.text.size();
		final float scale = 2.0f * 0.01F;       // 2.0f is configurable SCALE -- do not modify the 0.01F

		RenderUtils.drawTextPlate(this.text, x, y, z, scale, delta);
//		RenderUtils.scheduleTextPlate(this.text, new Vec3d(x, y, z), scale, Color4f.WHITE, Color4f.fromColor(0x40000000), 15728880, false, false, TextAlignment.CENTER);
//		RenderUtils.scheduleTextPlate(this.text, new Vec3d(x, y, z), scale);
	}
}
