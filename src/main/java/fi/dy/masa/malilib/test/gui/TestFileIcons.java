package fi.dy.masa.malilib.test.gui;

import java.nio.file.Path;

import javax.annotation.Nullable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.interfaces.IFileBrowserIconProvider;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;

public enum TestFileIcons implements IGuiIcon, IFileBrowserIconProvider
{
	DUMMY                   ( 0,   0,  0,  0),
	FILE_ICON_TEST          ( 0,   0, 12, 12),
	FILE_ICON_DIR           (12,   0, 12, 12),
	FILE_ICON_DIR_UP        (12,  12, 12, 12),
	FILE_ICON_DIR_ROOT      (12,  24, 12, 12),
	FILE_ICON_SEARCH        (12,  36, 12, 12),
	FILE_ICON_CREATE_DIR    (12,  48, 12, 12),
	;

	public static final Identifier TEXTURE = Identifier.of(MaLiLibReference.MOD_ID, "textures/gui/test_gui_widgets.png");

	private final int u;
	private final int v;
	private final int w;
	private final int h;

	TestFileIcons(int u, int v, int w, int h)
	{
		this.u = u;
		this.v = v;
		this.w = w;
		this.h = h;
	}

	@Override
	public int getWidth()
	{
		return this.w;
	}

	@Override
	public int getHeight()
	{
		return this.h;
	}

	@Override
	public int getU()
	{
		return this.u;
	}

	@Override
	public int getV()
	{
		return this.v;
	}

	@Override
	public void renderAt(DrawContext drawContext, int x, int y, float zLevel, boolean enabled, boolean selected)
	{
		RenderUtils.drawTexturedRect(drawContext, this.getTexture(), x, y, this.u, this.v, this.w, this.h, zLevel);
	}

	@Override
	public Identifier getTexture()
	{
		return TEXTURE;
	}

	@Override
	public IGuiIcon getIconRoot()
	{
		return FILE_ICON_DIR_ROOT;
	}

	@Override
	public IGuiIcon getIconUp()
	{
		return FILE_ICON_DIR_UP;
	}

	@Override
	public IGuiIcon getIconCreateDirectory()
	{
		return FILE_ICON_CREATE_DIR;
	}

	@Override
	public IGuiIcon getIconSearch()
	{
		return FILE_ICON_SEARCH;
	}

	@Override
	public IGuiIcon getIconDirectory()
	{
		return FILE_ICON_DIR;
	}

	@Override
	@Nullable
	public IGuiIcon getIconForFile(Path file)
	{
		if (this == DUMMY)
		{
			return null;
		}

		return FILE_ICON_TEST;
	}
}
