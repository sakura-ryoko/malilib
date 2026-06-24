package fi.dy.masa.malilib.compat.iris;

import java.util.Objects;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.render.MaLiLibPipelines;

public class IrisCompat
{
    private static final String SODIUM_ID = "sodium";
    private static final String IRIS_ID = "iris";

    private static boolean isSodiumLoaded = false;
    private static boolean isIrisLoaded = false;
    private static String sodiumVersion = "";
    private static String irisVersion = "";

    static
    {
        FabricLoader.getInstance().getAllMods().stream().toList().forEach((mc ->
        {
            ModMetadata meta = mc.getMetadata();

            if (Objects.equals(meta.getId(), SODIUM_ID))
            {
                sodiumVersion = meta.getVersion().getFriendlyString();
                isSodiumLoaded = true;
            }
            else if (Objects.equals(meta.getId(), IRIS_ID))
            {
                irisVersion = meta.getVersion().getFriendlyString();
                isIrisLoaded = true;
            }
        }));

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

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_NO_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LEQUAL_DEPTH, IrisProgram.BASIC);
//	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_LESS_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_GREATER_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_DEPTH_MASK, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT, IrisProgram.BASIC);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_NO_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LEQUAL_DEPTH, IrisProgram.BASIC);
//	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_LESS_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_GREATER_DEPTH, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA_DEPTH_MASK, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_COLOR_MASA, IrisProgram.BASIC);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_1, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_2, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_OFFSET_3, IrisProgram.TEXTURED);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_LEQUAL_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_GREATER_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT_DEPTH_MASK, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_TRANSLUCENT, IrisProgram.TEXTURED);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_NO_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_1, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_2, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_OFFSET_3, IrisProgram.TEXTURED);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH_NO_CULL, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_LEQUAL_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_GREATER_DEPTH, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA_DEPTH_MASK, IrisProgram.TEXTURED);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.POSITION_TEX_COLOR_MASA, IrisProgram.TEXTURED);

//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_TRANSLUCENT_NO_DEPTH, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_TRANSLUCENT_NO_CULL, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_TRANSLUCENT_OFFSET_1, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_TRANSLUCENT_OFFSET_2, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_TRANSLUCENT_OFFSET_3, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_TRANSLUCENT, IrisProgram.LINES);

//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_MASA_SIMPLE_NO_DEPTH, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_MASA_SIMPLE_NO_CULL, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_MASA_SIMPLE_OFFSET_1, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_MASA_SIMPLE_OFFSET_2, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_MASA_SIMPLE_OFFSET_3, IrisProgram.LINES);
//            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.LINES_MASA_SIMPLE, IrisProgram.LINES);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_LEQUAL_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_TRANSLUCENT, IrisProgram.LINES);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_LEQUAL_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE, IrisProgram.LINES);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_TRANSLUCENT, IrisProgram.LINES);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_DEPTH, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_NO_CULL, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_1, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_2, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE_OFFSET_3, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.DEBUG_LINE_STRIP_MASA_SIMPLE, IrisProgram.LINES);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.SOLID_MASA_OFFSET, IrisProgram.TERRAIN_SOLID);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.WIREFRAME_MASA_OFFSET, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.CUTOUT_MIPPED_MASA_OFFSET, IrisProgram.TERRAIN_CUTOUT);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.CUTOUT_MASA_OFFSET, IrisProgram.TERRAIN_CUTOUT);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.TRANSLUCENT_MASA_OFFSET, IrisProgram.TRANSLUCENT);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.TRIPWIRE_MASA_OFFSET, IrisProgram.TRANSLUCENT);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.SOLID_MASA, IrisProgram.TERRAIN_SOLID);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.WIREFRAME_MASA, IrisProgram.LINES);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.CUTOUT_MIPPED_MASA, IrisProgram.TERRAIN_CUTOUT);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.CUTOUT_MASA, IrisProgram.TERRAIN_CUTOUT);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.TRANSLUCENT_MASA, IrisProgram.TRANSLUCENT);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.TRIPWIRE_MASA, IrisProgram.TRANSLUCENT);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH_OFFSET, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_NO_DEPTH, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_OFFSET_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_OFFSET, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_DEPTH_MASK, IrisProgram.BASIC);
	        IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE_NO_CULL, IrisProgram.BASIC);
            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.MINIHUD_SHAPE, IrisProgram.BASIC);

            IrisApi.getInstance().assignPipeline(MaLiLibPipelines.GUI_OVERLAY, IrisProgram.BASIC);
        }
    }
}
