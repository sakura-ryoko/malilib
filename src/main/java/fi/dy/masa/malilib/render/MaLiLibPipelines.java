package fi.dy.masa.malilib.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;

/**
 * This is meant as a central place to manage all custom Render Pipelines
 */
public class MaLiLibPipelines
{
    // POSITION STAGES
    public static RenderPipeline.Snippet POSITION_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_OVERLAY_STAGE;
    public static RenderPipeline.Snippet POSITION_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet POSITION_MASA_STAGE;
    public static RenderPipeline.Snippet POSITION_COLOR_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_COLOR_OVERLAY_STAGE;
    public static RenderPipeline.Snippet POSITION_COLOR_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet POSITION_COLOR_MASA_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_OVERLAY_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_MASA_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_COLOR_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_COLOR_OVERLAY_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_COLOR_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet POSITION_TEX_COLOR_MASA_STAGE;

    // LINES STAGES
    public static RenderPipeline.Snippet LINES_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet LINES_OVERLAY_STAGE;
    public static RenderPipeline.Snippet LINES_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet LINES_MASA_STAGE;
    public static RenderPipeline.Snippet DEBUG_LINES_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet DEBUG_LINES_OVERLAY_STAGE;
    public static RenderPipeline.Snippet DEBUG_LINES_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet DEBUG_LINES_MASA_STAGE;

    // TERRAIN/ENTITY STAGES
    public static RenderPipeline.Snippet TERRAIN_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet TERRAIN_OVERLAY_STAGE;
    public static RenderPipeline.Snippet TERRAIN_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet TERRAIN_MASA_STAGE;
    public static RenderPipeline.Snippet ENTITY_TRANSLUCENT_STAGE;
    public static RenderPipeline.Snippet ENTITY_OVERLAY_STAGE;
    public static RenderPipeline.Snippet ENTITY_MASA_SIMPLE_STAGE;
    public static RenderPipeline.Snippet ENTITY_MASA_STAGE;

    // POSITION_TRANSLUCENT
    public static RenderPipeline POSITION_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_TRANSLUCENT_GREATER_DEPTH;
    public static RenderPipeline POSITION_TRANSLUCENT;

    // POSITION_OVERLAY
    public static RenderPipeline POSITION_OVERLAY_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_OVERLAY_NO_DEPTH;
    public static RenderPipeline POSITION_OVERLAY_LESSER_DEPTH;
    public static RenderPipeline POSITION_OVERLAY_GREATER_DEPTH;
    public static RenderPipeline POSITION_OVERLAY;

    // POSITION_MASA_SIMPLE
    public static RenderPipeline POSITION_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline POSITION_MASA_SIMPLE_LESSER_DEPTH;
    public static RenderPipeline POSITION_MASA_SIMPLE_GREATER_DEPTH;
    public static RenderPipeline POSITION_MASA_SIMPLE;

    // POSITION_MASA
    public static RenderPipeline POSITION_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_MASA_GREATER_DEPTH;
    public static RenderPipeline POSITION_MASA;

    // POSITION_COLOR_TRANSLUCENT
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT_GREATER_DEPTH;
    public static RenderPipeline POSITION_COLOR_TRANSLUCENT;

    // POSITION_COLOR_OVERLAY
    public static RenderPipeline POSITION_COLOR_OVERLAY_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_OVERLAY_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_OVERLAY_LESSER_DEPTH;
    public static RenderPipeline POSITION_COLOR_OVERLAY_GREATER_DEPTH;
    public static RenderPipeline POSITION_COLOR_OVERLAY;

    // POSITION_COLOR_MASA_SIMPLE
    public static RenderPipeline POSITION_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_SIMPLE_LESSER_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_SIMPLE_GREATER_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_SIMPLE;

    // POSITION_COLOR_MASA
    public static RenderPipeline POSITION_COLOR_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_COLOR_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA_GREATER_DEPTH;
    public static RenderPipeline POSITION_COLOR_MASA;

    // POSITION_TEX_TRANSLUCENT
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_TRANSLUCENT_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_TRANSLUCENT;

    // POSITION_TEX_OVERLAY
    public static RenderPipeline POSITION_TEX_OVERLAY_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_OVERLAY_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_OVERLAY_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_OVERLAY_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_OVERLAY;

    // POSITION_TEX_MASA_SIMPLE
    public static RenderPipeline POSITION_TEX_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA_SIMPLE_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA_SIMPLE_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA_SIMPLE;

    // POSITION_TEX_MASA
    public static RenderPipeline POSITION_TEX_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_MASA;

    // POSITION_TEX_COLOR_TRANSLUCENT
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_TRANSLUCENT;

    // POSITION_TEX_COLOR_OVERLAY
    public static RenderPipeline POSITION_TEX_COLOR_OVERLAY_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_OVERLAY_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_OVERLAY_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_OVERLAY_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_OVERLAY;

    // POSITION_TEX_COLOR_MASA_SIMPLE
    public static RenderPipeline POSITION_TEX_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_SIMPLE_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_SIMPLE_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_SIMPLE;

    // POSITION_TEX_COLOR_MASA
    public static RenderPipeline POSITION_TEX_COLOR_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_NO_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_LESSER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA_GREATER_DEPTH;
    public static RenderPipeline POSITION_TEX_COLOR_MASA;

    // LINES_TRANSLUCENT
    public static RenderPipeline LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline LINES_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline LINES_TRANSLUCENT_NO_CULL;
    public static RenderPipeline LINES_TRANSLUCENT;

    // LINES_OVERLAY
    public static RenderPipeline LINES_OVERLAY_NO_DEPTH_NO_CULL;
    public static RenderPipeline LINES_OVERLAY_NO_DEPTH;
    public static RenderPipeline LINES_OVERLAY_NO_CULL;
    public static RenderPipeline LINES_OVERLAY;

    // LINES_MASA_SIMPLE
    public static RenderPipeline LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline LINES_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline LINES_MASA_SIMPLE_NO_CULL;
    public static RenderPipeline LINES_MASA_SIMPLE;

    // LINES_MASA
    public static RenderPipeline LINES_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline LINES_MASA_NO_DEPTH;
    public static RenderPipeline LINES_MASA_NO_CULL;
    public static RenderPipeline LINES_MASA;

    // DEBUG_LINES_TRANSLUCENT
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT_NO_CULL;
    public static RenderPipeline DEBUG_LINES_TRANSLUCENT;

    // DEBUG_LINES_OVERLAY
    public static RenderPipeline DEBUG_LINES_OVERLAY_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_OVERLAY_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_OVERLAY_NO_CULL;
    public static RenderPipeline DEBUG_LINES_OVERLAY;

    // DEBUG_LINES_MASA_SIMPLE
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE_NO_CULL;
    public static RenderPipeline DEBUG_LINES_MASA_SIMPLE;

    // DEBUG_LINES_MASA
    public static RenderPipeline DEBUG_LINES_MASA_NO_DEPTH_NO_CULL;
    public static RenderPipeline DEBUG_LINES_MASA_NO_DEPTH;
    public static RenderPipeline DEBUG_LINES_MASA_NO_CULL;
    public static RenderPipeline DEBUG_LINES_MASA;

    // TERRAIN_TRANSLUCENT
    public static RenderPipeline SOLID_TRANSLUCENT;
    public static RenderPipeline WIREFRAME_TRANSLUCENT;
    public static RenderPipeline CUTOUT_MIPPED_TRANSLUCENT;
    public static RenderPipeline CUTOUT_TRANSLUCENT;
    public static RenderPipeline TRANSLUCENT_TRANSLUCENT;
    public static RenderPipeline TRIPWIRE_TRANSLUCENT;

    // TERRAIN_OVERLAY
    public static RenderPipeline SOLID_OVERLAY;
    public static RenderPipeline WIREFRAME_OVERLAY;
    public static RenderPipeline CUTOUT_MIPPED_OVERLAY;
    public static RenderPipeline CUTOUT_OVERLAY;
    public static RenderPipeline TRANSLUCENT_OVERLAY;
    public static RenderPipeline TRIPWIRE_OVERLAY;

    // TERRAIN_MASA_SIMPLE
    public static RenderPipeline SOLID_MASA_SIMPLE;
    public static RenderPipeline WIREFRAME_MASA_SIMPLE;
    public static RenderPipeline CUTOUT_MIPPED_MASA_SIMPLE;
    public static RenderPipeline CUTOUT_MASA_SIMPLE;
    public static RenderPipeline TRANSLUCENT_MASA_SIMPLE;
    public static RenderPipeline TRIPWIRE_MASA_SIMPLE;

    // TERRAIN_MASA
    public static RenderPipeline SOLID_MASA;
    public static RenderPipeline WIREFRAME_MASA;
    public static RenderPipeline CUTOUT_MIPPED_MASA;
    public static RenderPipeline CUTOUT_MASA;
    public static RenderPipeline TRANSLUCENT_MASA;
    public static RenderPipeline TRIPWIRE_MASA;

    public static RenderPipeline getPositionSimple()
    {
        return POSITION_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPosition(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_MASA_SIMPLE_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_MASA_SIMPLE_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_MASA_SIMPLE_GREATER_DEPTH;
            }
            default ->
            {
                return POSITION_MASA_SIMPLE;
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
                        return POSITION_TRANSLUCENT;
                    }
                }
            }
            case OVERLAY ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_OVERLAY_NO_DEPTH : POSITION_OVERLAY_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_OVERLAY_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_OVERLAY_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_OVERLAY;
                    }
                }
            }
            case MASA_SIMPLE ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_MASA_SIMPLE_NO_DEPTH : POSITION_MASA_SIMPLE_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_MASA_SIMPLE_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_MASA_SIMPLE_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_MASA_SIMPLE;
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
                        return POSITION_MASA;
                    }
                }
            }
            default ->
            {
                return RenderPipelines.POSITION_SKY;
            }
        }
    }

    public static RenderPipeline getPositionTexSimple()
    {
        return POSITION_TEX_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPositionTex(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_TEX_MASA_SIMPLE_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_TEX_MASA_SIMPLE_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_TEX_MASA_SIMPLE_GREATER_DEPTH;
            }
            default ->
            {
                return POSITION_TEX_MASA_SIMPLE;
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
                        return POSITION_TEX_TRANSLUCENT;
                    }
                }
            }
            case OVERLAY ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_OVERLAY_NO_DEPTH : POSITION_TEX_OVERLAY_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_OVERLAY_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_OVERLAY_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_TEX_OVERLAY;
                    }
                }
            }
            case MASA_SIMPLE ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_MASA_SIMPLE_NO_DEPTH : POSITION_TEX_MASA_SIMPLE_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_MASA_SIMPLE_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_MASA_SIMPLE_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_TEX_MASA_SIMPLE;
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
                        return POSITION_TEX_MASA;
                    }
                }
            }
            default ->
            {
                return RenderPipelines.POSITION_TEX_PANORAMA;
            }
        }
    }

    public static RenderPipeline getPositionColorSimple()
    {
        return POSITION_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPositionColor(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_COLOR_MASA_SIMPLE_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_COLOR_MASA_SIMPLE_GREATER_DEPTH;
            }
            default ->
            {
                return POSITION_COLOR_MASA_SIMPLE;
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
                        return POSITION_COLOR_TRANSLUCENT;
                    }
                }
            }
            case OVERLAY ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_COLOR_OVERLAY_NO_DEPTH : POSITION_COLOR_OVERLAY_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_COLOR_OVERLAY_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_COLOR_OVERLAY_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_COLOR_OVERLAY;
                    }
                }
            }
            case MASA_SIMPLE ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_COLOR_MASA_SIMPLE_NO_DEPTH : POSITION_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_COLOR_MASA_SIMPLE_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_COLOR_MASA_SIMPLE_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_COLOR_MASA_SIMPLE;
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
                        return POSITION_COLOR_MASA;
                    }
                }
            }
            default ->
            {
                return RenderPipelines.POSITION_COLOR_SUNRISE_SUNSET;
            }
        }
    }

    public static RenderPipeline getPositionTexColorSimple()
    {
        return POSITION_TEX_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getPositionTexColor(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return POSITION_TEX_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
            }
            case LESSER ->
            {
                return POSITION_TEX_COLOR_MASA_SIMPLE_LESSER_DEPTH;
            }
            case GREATER ->
            {
                return POSITION_TEX_COLOR_MASA_SIMPLE_GREATER_DEPTH;
            }
            default ->
            {
                return POSITION_TEX_COLOR_MASA_SIMPLE;
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
                        return POSITION_TEX_COLOR_TRANSLUCENT;
                    }
                }
            }
            case OVERLAY ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_COLOR_OVERLAY_NO_DEPTH : POSITION_TEX_COLOR_OVERLAY_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_COLOR_OVERLAY_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_COLOR_OVERLAY_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_TEX_COLOR_OVERLAY;
                    }
                }
            }
            case MASA_SIMPLE ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? POSITION_TEX_COLOR_MASA_SIMPLE_NO_DEPTH : POSITION_TEX_COLOR_MASA_SIMPLE_NO_DEPTH_NO_CULL;
                    }
                    case LESSER ->
                    {
                        return POSITION_TEX_COLOR_MASA_SIMPLE_LESSER_DEPTH;
                    }
                    case GREATER ->
                    {
                        return POSITION_TEX_COLOR_MASA_SIMPLE_GREATER_DEPTH;
                    }
                    default ->
                    {
                        return POSITION_TEX_COLOR_MASA_SIMPLE;
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
                        return POSITION_TEX_COLOR_MASA;
                    }
                }
            }
            default ->
            {
                return RenderPipelines.POSITION_TEX_COLOR_CELESTIAL;
            }
        }
    }

    public static RenderPipeline getLinesSimple()
    {
        return LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getLines(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
            }
            case LESSER, GREATER ->
            {
                return LINES_MASA_SIMPLE_NO_CULL;
            }
            default ->
            {
                return LINES_MASA_SIMPLE;
            }
        }
    }

    public static RenderPipeline getLines(Type type, Depth depth, boolean culling)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? LINES_TRANSLUCENT_NO_DEPTH : LINES_TRANSLUCENT_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return LINES_TRANSLUCENT_NO_CULL;
                    }
                    default ->
                    {
                        return LINES_TRANSLUCENT;
                    }
                }
            }
            case OVERLAY ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? LINES_OVERLAY_NO_DEPTH : LINES_OVERLAY_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return LINES_OVERLAY_NO_CULL;
                    }
                    default ->
                    {
                        return LINES_OVERLAY;
                    }
                }
            }
            case MASA_SIMPLE ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? LINES_MASA_SIMPLE_NO_DEPTH : LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return LINES_MASA_SIMPLE_NO_CULL;
                    }
                    default ->
                    {
                        return LINES_MASA_SIMPLE;
                    }
                }
            }
            case MASA ->
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
                        return LINES_MASA;
                    }
                }
            }
            default ->
            {
                return RenderPipelines.LINES;
            }
        }
    }

    public static RenderPipeline getDebugLinesSimple()
    {
        return DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
    }

    public static RenderPipeline getDebugLines(Depth depth)
    {
        switch (depth)
        {
            case NO_DEPTH ->
            {
                return DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
            }
            case LESSER, GREATER ->
            {
                return DEBUG_LINES_MASA_SIMPLE_NO_CULL;
            }
            default ->
            {
                return DEBUG_LINES_MASA_SIMPLE;
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
                        return DEBUG_LINES_TRANSLUCENT;
                    }
                }
            }
            case OVERLAY ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? DEBUG_LINES_OVERLAY_NO_DEPTH : DEBUG_LINES_OVERLAY_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return DEBUG_LINES_OVERLAY_NO_CULL;
                    }
                    default ->
                    {
                        return DEBUG_LINES_OVERLAY;
                    }
                }
            }
            case MASA_SIMPLE ->
            {
                switch (depth)
                {
                    case NO_DEPTH ->
                    {
                        return culling ? DEBUG_LINES_MASA_SIMPLE_NO_DEPTH : DEBUG_LINES_MASA_SIMPLE_NO_DEPTH_NO_CULL;
                    }
                    case LESSER, GREATER ->
                    {
                        return DEBUG_LINES_MASA_SIMPLE_NO_CULL;
                    }
                    default ->
                    {
                        return DEBUG_LINES_MASA_SIMPLE;
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
                        return DEBUG_LINES_MASA;
                    }
                }
            }
            default ->
            {
                return RenderPipelines.DEBUG_LINE_STRIP;
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
            case OVERLAY ->
            {
                return SOLID_OVERLAY;
            }
            case MASA_SIMPLE ->
            {
                return SOLID_MASA_SIMPLE;
            }
            case MASA ->
            {
                return SOLID_MASA;
            }
            default ->
            {
                return RenderPipelines.SOLID;
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
            case OVERLAY ->
            {
                return WIREFRAME_OVERLAY;
            }
            case MASA_SIMPLE ->
            {
                return WIREFRAME_MASA_SIMPLE;
            }
            case MASA ->
            {
                return WIREFRAME_MASA;
            }
            default ->
            {
                return RenderPipelines.WIREFRAME;
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
            case OVERLAY ->
            {
                return CUTOUT_OVERLAY;
            }
            case MASA_SIMPLE ->
            {
                return CUTOUT_MASA_SIMPLE;
            }
            case MASA ->
            {
                return CUTOUT_MASA;
            }
            default ->
            {
                return RenderPipelines.CUTOUT;
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
            case OVERLAY ->
            {
                return CUTOUT_MIPPED_OVERLAY;
            }
            case MASA_SIMPLE ->
            {
                return CUTOUT_MIPPED_MASA_SIMPLE;
            }
            case MASA ->
            {
                return CUTOUT_MIPPED_MASA;
            }
            default ->
            {
                return RenderPipelines.CUTOUT_MIPPED;
            }
        }
    }

    public static RenderPipeline getTranslucent(Type type)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                return TRANSLUCENT_TRANSLUCENT;
            }
            case OVERLAY ->
            {
                return TRANSLUCENT_OVERLAY;
            }
            case MASA_SIMPLE ->
            {
                return TRANSLUCENT_MASA_SIMPLE;
            }
            case MASA ->
            {
                return TRANSLUCENT_MASA;
            }
            default ->
            {
                return RenderPipelines.TRANSLUCENT;
            }
        }
    }

    public static RenderPipeline getTripwire(Type type)
    {
        switch (type)
        {
            case TRANSLUCENT ->
            {
                return TRIPWIRE_TRANSLUCENT;
            }
            case OVERLAY ->
            {
                return TRIPWIRE_OVERLAY;
            }
            case MASA_SIMPLE ->
            {
                return TRIPWIRE_MASA_SIMPLE;
            }
            case MASA ->
            {
                return TRIPWIRE_MASA;
            }
            default ->
            {
                return RenderPipelines.TRIPWIRE;
            }
        }
    }

    public enum Type
    {
        DEFAULT,
        TRANSLUCENT,
        OVERLAY,
        MASA_SIMPLE,
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
