package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.ShaderPipelines;

/**
 * This is meant as a central place to manage all custom Render Pipelines
 */
public class MaLiLibPipelines
{
    // POSITION STAGES
    public static RenderPipeline.Snippet POSITION_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_MASA_STAGE;
    public static RenderPipeline.Snippet POSITION_COLOR_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_COLOR_MASA_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_MASA_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_COLOR_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_COLOR_MASA_STAGE;

    // LINES STAGES
    public static RenderPipeline.Snippet LINES_STAGE;
    public static RenderPipeline.Snippet LINES_MASA_STAGE;
    public static RenderPipeline.Snippet DEBUG_LINES_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet DEBUG_LINES_MASA_STAGE;

    // TERRAIN/ENTITY STAGES
    public static RenderPipeline.Snippet TERRAIN_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet TERRAIN_MASA_STAGE;
    public static RenderPipeline.Snippet ENTITY_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet ENTITY_MASA_STAGE;

    // POSITION_TRANSLUCENT
    public static RenderPipeline POSITION_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_TRANSLUCENT_GREATER_DEPTH;

    // POSITION_MASA
    public static RenderPipeline POSITION_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_MASA_GREATER_DEPTH;

    // POSITION_COLOR_TRANSLUCENT
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_GREATER_DEPTH;

    // POSITION_COLOR_MASA
    public static RenderPipeline POSITION_COLOR_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_GREATER_DEPTH;

    // POSITION_TEX_TRANSLUCENT
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_GREATER_DEPTH;

    // POSITION_TEX_MASA
    public static RenderPipeline POSITION_TEX_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA_GREATER_DEPTH;

    // POSITION_TEX_COLOR_TRANSLUCENT
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_GREATER_DEPTH;

    // POSITION_TEX_COLOR_MASA
    public static RenderPipeline POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_GREATER_DEPTH;

    // LINES
    public static RenderPipeline LINES_NO_DEPTH_NO_CULL;
    public static RenderPipeline LINES_NO_DEPTH;
    public static RenderPipeline LINES_NO_CULL;

    // LINES_MASA
    public static RenderPipeline LINES_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline LINES_MASA_NO_DEPTH;
    public static RenderPipeline LINES_MASA_NO_CULL;

    // DEBUG_LINES_TRANSLUCENT
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_CULL;

    // DEBUG_LINES_MASA
    public static RenderPipeline DEBUG_LINES_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_MASA_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_MASA_NO_CULL;

    // TERRAIN_TRANSLUCENT
    public static RenderPipeline SOLID_TRANSLUCENT;
    public static RenderPipeline WIREFRAME_TRANSLUCENT;
    public static RenderPipeline CUTOUT_MIPPED_TRANSLUCENT;
    public static RenderPipeline CUTOUT_TRANSLUCENT;

    // TERRAIN_MASA
    public static RenderPipeline SOLID_MASA;
    public static RenderPipeline WIREFRAME_MASA;
    public static RenderPipeline CUTOUT_MIPPED_MASA;
    public static RenderPipeline CUTOUT_MASA;

    public static RenderPipeline getPositionSimple()
    {
        return POSITION_TRANSLUCENT_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPosition(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_MASA_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_MASA_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_MASA_GREATER_DEPTH;
            }
            default ->
            {
                return ShaderPipelines.POSITION_SKY;
            }
        }
    }

    public static RenderPipeline getPosition(Type type, Depth depth, boolean culling)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TRANSLUCENT_NO_DEPTH : POSITION_TRANSLUCENT_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TRANSLUCENT_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TRANSLUCENT_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_SKY;
                    }
                }
            }
            case MASA ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_MASA_NO_DEPTH : POSITION_MASA_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_MASA_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_MASA_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_SKY;
                    }
                }
            }
            default ->
            {
                return ShaderPipelines.POSITION_SKY;
            }
        }
    }

    public static RenderPipeline getPositionTexSimple()
    {
        return POSITION_TEX_TRANSLUCENT_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPositionTex(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_TEX_MASA_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_TEX_MASA_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_TEX_MASA_GREATER_DEPTH;
            }
            default ->
            {
                return ShaderPipelines.POSITION_TEX_PANORAMA;
            }
        }
    }

    public static RenderPipeline getPositionTex(Type type, Depth depth, boolean culling)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_TRANSLUCENT_NO_DEPTH : POSITION_TEX_TRANSLUCENT_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_TRANSLUCENT_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_TRANSLUCENT_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_TEX_PANORAMA;
                    }
                }
            }
            case MASA ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_MASA_NO_DEPTH : POSITION_TEX_MASA_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_MASA_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_MASA_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_TEX_PANORAMA;
                    }
                }
            }
            default ->
            {
                return ShaderPipelines.POSITION_TEX_PANORAMA;
            }
        }
    }

    public static RenderPipeline getPositionColorSimple()
    {
        return POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPositionColor(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_COLOR_MASA_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_COLOR_MASA_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_COLOR_MASA_GREATER_DEPTH;
            }
            default ->
            {
                return ShaderPipelines.POSITION_COLOR_SUNRISE_SUNSET;
            }
        }
    }

    public static RenderPipeline getPositionColor(Type type, Depth depth, boolean culling)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_COLOR_TRANSLUCENT_NO_DEPTH : POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_COLOR_TRANSLUCENT_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_COLOR_TRANSLUCENT_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_COLOR_SUNRISE_SUNSET;
                    }
                }
            }
            case MASA ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_COLOR_MASA_NO_DEPTH : POSITION_COLOR_MASA_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_COLOR_MASA_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_COLOR_MASA_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_COLOR_SUNRISE_SUNSET;
                    }
                }
            }
            default ->
            {
                return ShaderPipelines.POSITION_COLOR_SUNRISE_SUNSET;
            }
        }
    }

    public static RenderPipeline getPositionTexColorSimple()
    {
        return POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPositionTexColor(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_TEX_COLOR_MASA_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_TEX_COLOR_MASA_GREATER_DEPTH;
            }
            default ->
            {
                return ShaderPipelines.POSITION_TEX_COLOR_END_SKY;
            }
        }
    }

    public static RenderPipeline getPositionTexColor(Type type, Depth depth, boolean culling)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH : POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_COLOR_TRANSLUCENT_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_COLOR_TRANSLUCENT_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_TEX_COLOR_END_SKY;
                    }
                }
            }
            case MASA ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_COLOR_MASA_NO_DEPTH : POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_COLOR_MASA_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_COLOR_MASA_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return ShaderPipelines.POSITION_TEX_COLOR_END_SKY;
                    }
                }
            }
            default ->
            {
                return ShaderPipelines.POSITION_TEX_COLOR_CELESTIAL;
            }
        }
    }

    public static RenderPipeline getLinesSimple()
    {
        return LINES_MASA_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getLines(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return LINES_MASA_NO_DEPTH_NO_CULL;
            }
            case LESSER, GREATER ->
            {
                return LINES_MASA_NO_CULL;
            }
            default ->
            {
                return ShaderPipelines.LINES;
            }
        }
    }

    public static RenderPipeline getLines(Type type, Depth depth, boolean culling)
    {
        switch (type)
        {
            case MASA, TRANSLUCENT ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? LINES_MASA_NO_DEPTH : LINES_MASA_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return LINES_MASA_NO_CULL;
                    }
                    default ->
                    {
                        return ShaderPipelines.LINES;
                    }
                }
            }
            default ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? LINES_NO_DEPTH : LINES_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return LINES_NO_CULL;
                    }
                    default ->
                    {
                        return ShaderPipelines.LINES;
                    }
                }
            }
        }
    }

    public static RenderPipeline getDebugLinesSimple()
    {
        return DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getDebugLines(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return DEBUG_LINES_MASA_NO_DEPTH_NO_CULL;
            }
            case LESSER, GREATER ->
            {
                return DEBUG_LINES_MASA_NO_CULL;
            }
            default ->
            {
                return ShaderPipelines.DEBUG_LINE_STRIP;
            }
        }
    }

    public static RenderPipeline getDebugLines(Type type, Depth depth, boolean culling)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? DEBUG_LINES_TRANSLUCENT_NO_DEPTH : DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return DEBUG_LINES_TRANSLUCENT_NO_CULL;
                    }
                    default ->
                    {
                        return ShaderPipelines.DEBUG_LINE_STRIP;
                    }
                }
            }
            case MASA ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? DEBUG_LINES_MASA_NO_DEPTH : DEBUG_LINES_MASA_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return DEBUG_LINES_MASA_NO_CULL;
                    }
                    default ->
                    {
                        return ShaderPipelines.DEBUG_LINE_STRIP;
                    }
                }
            }
            default ->
            {
                return ShaderPipelines.DEBUG_LINE_STRIP;
            }
        }
    }

    public static RenderPipeline getSolid(Type type)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                return SOLID_TRANSLUCENT;
            }
            case MASA ->
            {
                return SOLID_MASA;
            }
            default ->
            {
                return ShaderPipelines.SOLID;
            }
        }
    }

    public static RenderPipeline getWireframe(Type type)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                return WIREFRAME_TRANSLUCENT;
            }
            case MASA ->
            {
                return WIREFRAME_MASA;
            }
            default ->
            {
                return ShaderPipelines.WIREFRAME;
            }
        }
    }

    public static RenderPipeline getCutout(Type type)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                return CUTOUT_TRANSLUCENT;
            }
            case MASA ->
            {
                return CUTOUT_MASA;
            }
            default ->
            {
                return ShaderPipelines.CUTOUT;
            }
        }
    }

    public static RenderPipeline getCutoutMipped(Type type)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                return CUTOUT_MIPPED_TRANSLUCENT;
            }
            case MASA ->
            {
                return CUTOUT_MIPPED_MASA;
            }
            default ->
            {
                return ShaderPipelines.CUTOUT_MIPPED;
            }
        }
    }

    public enum Type
    {
        DEFAULT,
        TRANSLUCENT,
        MASA
    }

    public enum Depth
    {
        DEFAULT,
        NO_DEPTH,
        GREATER,
        LESSER
    }
}
