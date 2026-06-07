package fi.dy.masa.malilib.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibFabricData;
import fi.dy.masa.malilib.compat.ModIds;
import fi.dy.masa.malilib.render.MaLiLibPipelines;

public class IrisCompat
{
    private static boolean isSodiumLoaded = false;
    private static boolean isIrisLoaded = false;
    private static String sodiumVersion = "";
    private static String irisVersion = "";

    static
    {
	    if (MaLiLibFabricData.ALL_MOD_VERSIONS.containsKey(ModIds.sodium))
	    {
			sodiumVersion = MaLiLibFabricData.ALL_MOD_VERSIONS.get(ModIds.sodium);
			isSodiumLoaded = true;
	    }
		if (MaLiLibFabricData.ALL_MOD_VERSIONS.containsKey(ModIds.iris))
		{
			irisVersion = MaLiLibFabricData.ALL_MOD_VERSIONS.get(ModIds.iris);
			isIrisLoaded = true;
		}

        MaLiLib.LOGGER.info("Sodium: [{}], Iris: [{}]", isSodiumLoaded ? sodiumVersion : "N/F", isIrisLoaded ? irisVersion : "N/F");
    }

	public static boolean hasSodium()
	{
		return isSodiumLoaded;
	}

	public static boolean hasIris()
    {
        return isSodiumLoaded && isIrisLoaded;
    }

	public static boolean isShaderActive()
	{
		if (hasIris())
		{
			return IrisApi.getInstance().isShaderPackInUse();
		}

		return false;
	}

	public static boolean isShadowPassActive()
	{
		if (hasIris())
		{
			return IrisApi.getInstance().isRenderingShadowPass();
		}

		return false;
	}

	public static void registerPipelines()
    {
        if (hasIris())
        {
            MaLiLib.LOGGER.info("Assigning MaLiLib Pipelines to Iris Programs:");

	        // POSITION_COLOR_TRANSLUCENT
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_DEPTH_MASK, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT, IrisProgram.BASIC);

			// POSITION_COLOR_MASA
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_DEPTH_MASK, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA, IrisProgram.BASIC);

			// TEXT_PLATE_MASA
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.TEXT_PLATE_BG_MASA_NO_DEPTH, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.TEXT_PLATE_BG_MASA, IrisProgram.BASIC);

			// MINIHUD_SHAPE
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH_OFFSET, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_OFFSET, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_NO_CULL, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE, IrisProgram.BASIC);

	        // POSITION_COLOR_LINES_TRANSLUCENT
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_NO_DEPTH, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT_LEQUAL_DEPTH, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_TRANSLUCENT, IrisProgram.LINES);

			// POSITION_COLOR_LINES_MASA
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_NO_DEPTH_NO_CULL, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_NO_DEPTH, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_1, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_2, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_OFFSET_3, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH_NO_CULL, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA_LEQUAL_DEPTH, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_LINES_MASA, IrisProgram.LINES);

			// MINIHUD_SHAPE_LINES
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_DEPTH_OFFSET, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_DEPTH, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_LINES_OFFSET_NO_CULL, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_LINES_OFFSET, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_LINES_NO_CULL, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_LINES, IrisProgram.LINES);

	        // POSITION_TEX_COLOR_TRANSLUCENT
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3, IrisProgram.TEXTURED);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_DEPTH_MASK, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT, IrisProgram.TEXTURED);

			// POSITION_TEX_COLOR_MASA
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3, IrisProgram.TEXTURED);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_DEPTH_MASK, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA, IrisProgram.TEXTURED);

	        // DEBUG_LINES_TRANSLUCENT
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_LEQUAL_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT, IrisProgram.LINES);

			// DEBUG_LINES_MASA
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE, IrisProgram.LINES);

			// DEBUG_LINE_STRIP_TRANSLUCENT
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT, IrisProgram.LINES);

	        // DEBUG_LINE_STRIP_MASA
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE, IrisProgram.LINES);

			// GUI
//	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.GUI_OVERLAY, IrisProgram.BASIC);
//	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.GUI_TEXTURED_OVERLAY, IrisProgram.TEXTURED);

	        // LEGACY_TERRAIN
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_SOLID_TERRAIN, IrisProgram.TERRAIN_SOLID);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_WIREFRAME, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_CUTOUT_TERRAIN, IrisProgram.TERRAIN_CUTOUT);

	        // LEGACY_TERRAIN_OFFSET
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_SOLID_TERRAIN_OFFSET, IrisProgram.TERRAIN_SOLID);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_WIREFRAME_OFFSET, IrisProgram.LINES);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_CUTOUT_TERRAIN_OFFSET, IrisProgram.TERRAIN_CUTOUT);

	        // LEGACY_TERRAIN_TRANSLUCENT
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_TRANSLUCENT, IrisProgram.TRANSLUCENT);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LEGACY_TRANSLUCENT_OFFSET, IrisProgram.TRANSLUCENT);
        }
    }
}
