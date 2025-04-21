package malilib.render.overlay;

import java.nio.file.Path;
import javax.annotation.Nullable;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;

import malilib.listener.EventListener;
import malilib.util.data.ModInfo;
import malilib.util.data.json.JsonUtils;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.position.BlockPos;
import malilib.util.position.Vec3d;

public abstract class BaseOverlayRenderer
{
    protected BlockPos lastUpdatePos = BlockPos.ORIGIN;
    private Vec3d updateCameraPos = Vec3d.ZERO;
    protected boolean needsUpdate;
    protected boolean disableDepthTest;
    protected float lineWidth = 1f;

    public void setNeedsUpdate()
    {
        this.needsUpdate = true;
    }

    public void setLastUpdatePos(BlockPos lastUpdatePos)
    {
        this.lastUpdatePos = lastUpdatePos;
    }

    /**
     * @return the camera position where the renderer was last updated
     */
    public Vec3d getUpdatePosition()
    {
        return this.updateCameraPos;
    }

    /**
     * Sets the camera position where the renderer was last updated
     */
    public void setUpdatePosition(Vec3d cameraPosition)
    {
        this.updateCameraPos = cameraPosition;
    }

    public void setDisableDepthTest(boolean renderThrough)
    {
        this.disableDepthTest = renderThrough;
    }

    /**
     * Optional code to run when the overlay is enabled.
     * Note that both implementation and calling this method are left for the
     * mod to handle, this is just a "convenience place to run the code related to the overlay"
     */
    public void onEnabled()
    {
    }

    public boolean isEnabled()
    {
        return true;
    }

    public abstract ModInfo getModInfo();

    @Nullable
    public abstract Path getSaveFile(boolean isDimensionChangeOnly);

    /**
     * Should this renderer draw anything at the moment, i.e. is it enabled for example
     */
    public abstract boolean shouldRender();

    /**
     * @return true, if this renderer should get re-drawn/updated
     */
    public abstract boolean needsUpdate(Entity entity);

    /**
     * Update the renderer (draw again to the buffers). This method is called
     * when {@link BaseOverlayRenderer#needsUpdate(Entity)} returns true.
     * 
     * @param cameraPos The position of the camera when the method is called.
     *                  The camera position should be subtracted from any world coordinates for the vertex positions.
     *                  During the draw() call the MatrixStack will be translated by the camera position,
     *                  minus the difference between the camera position during the update() call,
     *                  and the camera position during the draw() call.
     * @param entity The current camera entity
     */
    public abstract void update(Vec3d cameraPos, Entity entity);

    protected abstract void startBuffers();

    protected abstract void uploadBuffers();

    protected void preRender()
    {
        RenderWrap.lineWidth(this.lineWidth);

        if (this.disableDepthTest)
        {
            RenderWrap.disableDepthTest();
            //RenderWrap.depthMask(false);
        }
    }

    protected void postRender()
    {
        if (this.disableDepthTest)
        {
            RenderWrap.enableDepthTest();
            //RenderWrap.depthMask(true);
        }
    }

    /**
     * Draws all the buffers to screen
     */
    public abstract void draw();

    /**
     * Allocates the OpenGL resources according to the current Video settings
     */
    public abstract void allocateGlResources();

    /**
     * Frees the allocated OpenGL buffers etc.
     */
    public abstract void deleteGlResources();

    /**
     * Allocates a new VBO or display list and returns it.
     * This is a convenience method to allocate using the POSITION_COLOR vertex format.
     */
    protected BaseRenderObject allocateBuffer(int glMode)
    {
        return this.allocateBuffer(glMode, DefaultVertexFormats.POSITION_COLOR, VboRenderObject::setupArrayPointersPosColor);
    }

    /**
     * Allocates a new VBO or display list and returns it
     * @param func the function to set up the array pointers according to the used vertex format
     */
    protected BaseRenderObject allocateBuffer(int glMode, VertexFormat vertexFormat, EventListener func)
    {
        return new VboRenderObject(glMode, vertexFormat, func);
    }

    public String getSaveId()
    {
        return this.getClass().getName();
    }

    @Nullable
    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("line_width", this.lineWidth);
        obj.addProperty("render_through", this.disableDepthTest);
        return obj;
    }

    public void fromJson(JsonObject obj)
    {
        this.lineWidth = JsonUtils.getFloatOrDefault(obj, "line_width", 1f);
        this.disableDepthTest = JsonUtils.getBooleanOrDefault(obj, "render_through", false);
    }
}
