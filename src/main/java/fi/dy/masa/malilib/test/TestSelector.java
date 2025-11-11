package fi.dy.masa.malilib.test;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

public class TestSelector implements IClientTickHandler
{
    public static final TestSelector INSTANCE = new TestSelector();
    public Selection AREA_SELECTION = new Selection();
    public BlockPos posLookingAt = null;

    private final Color4f colorPos1 = new Color4f(1f, 0.0625f, 0.0625f);
    private final Color4f colorPos2 = new Color4f(0.0625f, 0.0625f, 1f);
    private final Color4f sideColor = Color4f.fromColor(0x30FFFFFF);
    private final Color4f colorOverlapping = new Color4f(1f, 0.0625f, 1f);
    private final Color4f colorX = new Color4f(1f, 0.25f, 0.25f);
    private final Color4f colorY = new Color4f(0.25f, 1f, 0.25f);
    private final Color4f colorZ = new Color4f(0.25f, 0.25f, 1f);
    private final Color4f colorLooking = new Color4f(1.0f, 1.0f, 1.0f, 0.6f);

    public TestSelector() {}

    @Override
    public void onClientTick(MinecraftClient mc)
    {
        if (MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
        {
            if (ConfigTestEnum.TEST_SELECTOR_HOTKEY.getBooleanValue())
            {
                if (mc.options.attackKey.isPressed())
                {
                    select(false);
                }

                if (mc.options.useKey.isPressed())
                {
                    select(true);
                }

                if (this.posLookingAt == null)
                {
                    this.updateLookingAt(mc);
                }
            }
        }
    }

    public static class Selection
    {
        public BlockPos pos1 = null;
        public BlockPos pos2 = null;
    }

    public void updateLookingAt(MinecraftClient mc)
    {
        if (mc.crosshairTarget instanceof BlockHitResult)
        {
            this.posLookingAt = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            //posLookingAt = posLookingAt.offset(((BlockHitResult) mc.crosshairTarget).getSide());
        }
        else
        {
            this.posLookingAt = null;
        }
    }

    public void select(boolean pos2)
    {
        if (this.posLookingAt == null)
        {
            return;
        }

        if (pos2)
        {
            this.AREA_SELECTION.pos2 = this.posLookingAt;
        }
        else
        {
            this.AREA_SELECTION.pos1 = this.posLookingAt;
        }
    }

    public boolean shouldRender()
    {
        return (this.AREA_SELECTION.pos1 != null || this.AREA_SELECTION.pos2 != null) ||
                this.posLookingAt == null;
    }

    public void render(Matrix4f posMatrix, Matrix4f projMatrix, Profiler profiler, MinecraftClient mc)
    {
        float expand = 0.001f;
        //float lineWidthBlockBox = 2.0f;
        float lineWidthBlockBox = 2.2f;

        if (ConfigTestEnum.TEST_SELECTOR_HOTKEY.getBooleanValue())
        {
            this.updateLookingAt(mc);
        }
        else
        {
            return;
        }

        profiler.push(MaLiLibReference.MOD_ID+"_selector");
        Matrix4fStack globalStack = RenderSystem.getModelViewStack();

        globalStack.pushMatrix();
//        RenderUtils.color(1f, 1f, 1f, 1f);
//        RenderUtils.blend(true);
//        RenderUtils.depthTest(false);
//        RenderUtils.polygonOffset(true);
//        RenderUtils.polygonOffset(-1.2f, -0.2f);

        //renderLists(posMatrix, projMatrix, profiler);

        if (this.posLookingAt != null)
        {
            RenderUtils.renderBlockOutline(this.posLookingAt, expand, lineWidthBlockBox, this.colorLooking, false);
        }

        this.renderSelection(posMatrix, projMatrix, profiler, this.AREA_SELECTION, mc);

//        RenderUtils.polygonOffset(0f, 0f);
//        RenderUtils.polygonOffset(false);
        globalStack.popMatrix();
//        RenderUtils.depthMask(true);
        profiler.pop();
    }

    public void renderSelection(Matrix4f posMatrix, Matrix4f projMatrix, Profiler profiler, Selection selection, MinecraftClient mc)
    {

        BlockPos pos1 = selection.pos1;
        BlockPos pos2 = selection.pos2;

        if (pos1 == null && pos2 == null)
        {
            return;
        }

        float expand = 0.001f;
        float lineWidthBlockBox = 2.2f;
        //float lineWidthArea = 1.5f;
        float lineWidthArea = 2.0f;

        profiler.push("selection");

        if (pos1 != null && pos2 != null)
        {
            if (!pos1.equals(pos2))
            {
                RenderUtils.renderAreaOutlineNoCorners(pos1, pos2, lineWidthArea, this.colorX, this.colorY, this.colorZ);
                RenderUtils.renderAreaSides(pos1, pos2, this.sideColor, posMatrix, false);
                RenderUtils.renderBlockOutline(pos1, expand, lineWidthBlockBox, this.colorPos1, false);
                RenderUtils.renderBlockOutline(pos2, expand, lineWidthBlockBox, this.colorPos2, false);
            }
            else
            {
                RenderUtils.renderBlockOutlineOverlapping(pos1, expand, lineWidthBlockBox, this.colorPos1, this.colorPos2,
                                                          this.colorOverlapping, posMatrix, false);
            }
        }
        else
        {
            if (pos1 != null)
            {
                RenderUtils.renderBlockOutline(pos1, expand, lineWidthBlockBox, this.colorPos1, false);
            }

            if (pos2 != null)
            {
                RenderUtils.renderBlockOutline(pos2, expand, lineWidthBlockBox, this.colorPos2, false);
            }
        }

        profiler.pop();
    }
}
