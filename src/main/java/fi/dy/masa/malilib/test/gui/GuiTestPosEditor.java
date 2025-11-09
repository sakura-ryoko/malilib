package fi.dy.masa.malilib.test.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.ICoordinateValueModifier;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.position.PositionUtils;

public class GuiTestPosEditor extends GuiBase
{
	private final TestVec3dSupplier vec3dSupplier;
	private final TestBlockPosSupplier blockPosSupplier;

	public GuiTestPosEditor()
	{
		this.title = StringUtils.translate("malilib.gui.title.test_pos_editor");
		this.vec3dSupplier = new TestVec3dSupplier();
		this.blockPosSupplier = new TestBlockPosSupplier();
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int x = 10;
		int y = 20;

		this.createPosEditorElements(x, y);
	}

	private void createPosEditorElements(int x, int y)
	{
		y += 12;
		GuiUtils.createVec3dInputsVertical(x, y, 120, this.vec3dSupplier.get(), new TestVec3dEditor(this.vec3dSupplier::get, this.vec3dSupplier::set, this), true, this);
		x += 180;
		GuiUtils.createBlockPosInputsVertical(x, y, 120, this.blockPosSupplier.get(), new TestBlockPosEditor(this.blockPosSupplier::get, this.blockPosSupplier::set, this), true, this);
	}

	public static class TestVec3dSupplier
	{
		private Vec3d pos;

		public TestVec3dSupplier()
		{
			this.pos = Vec3d.ZERO;
		}

		public Vec3d get()
		{
			return this.pos;
		}

		public void set(Vec3d pos)
		{
			this.pos = pos;
			this.debug();
		}

		public void debug()
		{
			MaLiLib.LOGGER.warn("TestVec3dSupplier: [{}]", this.get().toString());
		}
	}

	public static class TestBlockPosSupplier
	{
		private BlockPos pos;

		public TestBlockPosSupplier()
		{
			this.pos = BlockPos.ORIGIN;
		}

		public BlockPos get()
		{
			return this.pos;
		}

		public void set(BlockPos pos)
		{
			this.pos = pos;
			this.debug();
		}

		public void debug()
		{
			MaLiLib.LOGGER.warn("TestBlockPosSupplier: [{}]", this.get().toString());
		}
	}

	public record TestVec3dEditor(Supplier<Vec3d> supplier, Consumer<Vec3d> consumer, GuiTestPosEditor gui) implements ICoordinateValueModifier
	{
		@Override
		public boolean modifyValue(PositionUtils.CoordinateType type, int amount)
		{
			this.consumer.accept(PositionUtils.modifyValue(type, this.supplier.get(), amount));
			this.gui.initGui();
			return true;
		}

		@Override
		public boolean setValueFromString(PositionUtils.CoordinateType type, String newValue)
		{
			try
			{
				this.consumer.accept(PositionUtils.setValue(type, this.supplier.get(), Double.parseDouble(newValue)));
				return true;
			}
			catch (Exception ignore) {}

			return false;
		}
	}

	public record TestBlockPosEditor(Supplier<BlockPos> supplier, Consumer<BlockPos> consumer, GuiTestPosEditor gui) implements ICoordinateValueModifier
	{
		@Override
		public boolean modifyValue(PositionUtils.CoordinateType type, int amount)
		{
			this.consumer.accept(PositionUtils.modifyValue(type, this.supplier.get(), amount));
			this.gui.initGui();
			return true;
		}

		@Override
		public boolean setValueFromString(PositionUtils.CoordinateType type, String newValue)
		{
			try
			{
				this.consumer.accept(PositionUtils.setValue(type, this.supplier.get(), Integer.parseInt(newValue)));
				return true;
			}
			catch (Exception ignore) {}

			return false;
		}
	}
}
