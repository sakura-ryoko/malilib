package fi.dy.masa.malilib.test.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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

	private void scanForEntities()
	{
		Minecraft mc = Minecraft.getInstance();
		ClientLevel level = mc.level;
		Entity camera = mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player;
		if (camera == null) { return; }
		Vec3 pos = camera.position();
		AABB bb = new AABB(pos, pos).inflate(16);

		if (level != null)
		{
			this.nearbyEntities.clear();
			List<Horse> nearbyHorses = level.getEntitiesOfClass(Horse.class, bb);

			if (!nearbyHorses.isEmpty())
			{
				this.nearbyEntities.addAll(nearbyHorses);
			}
		}
	}

	public void update()
	{
		this.scanForEntities();
	}

	public boolean shouldRender()
	{
		return !this.nearbyEntities.isEmpty();
	}

	public void render()
	{
		if (this.shouldRender())
		{
			this.nearbyEntities.forEach(this::renderEach);
		}
	}

	private void renderEach(Entity e)
	{
		// TODO
	}
}
