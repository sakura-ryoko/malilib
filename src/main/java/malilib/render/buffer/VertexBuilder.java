package malilib.render.buffer;

import java.nio.ByteBuffer;

import net.minecraft.client.renderer.BufferBuilder;

import malilib.util.data.Color4f;

public interface VertexBuilder
{
    VertexBuilder posColor(double x, double y, double z, int r, int g, int b, int a);

    default VertexBuilder posColor(double x, double y, double z, Color4f color)
    {
        return this.posColor(x, y, z, color.ri, color.gi, color.bi, color.ai);
    }

    VertexBuilder posUv(double x, double y, double z, float u, float v);

    VertexBuilder posUvColor(double x, double y, double z, float u, float v, int r, int g, int b, int a);

    default VertexBuilder posUvColor(double x, double y, double z, float u, float v, Color4f color)
    {
        return this.posUvColor(x, y, z, u, v, color.ri, color.gi, color.bi, color.ai);
    }

    default VertexBuilder posUvColor(double x, double y, double z, float u, float v, int color)
    {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >>  8) & 0xFF;
        int b = color & 0xFF;

        return this.posUvColor(x, y, z, u, v, r, g, b, a);
    }

    void addVertexData(int[] data);

    void putPosition(double x, double y, double z);

    void putNormal(float x, float y, float z);

    void putColorMultiplier(float r, float g, float b, int vertexIndex);

    void putQuadColor(int argb);

    void putQuadColor(float r, float g, float b);

    void putBrightness(int vertex0, int vertex1, int vertex2, int vertex3);

    void sortVertexData(float cameraX, float cameraY, float cameraZ);

    boolean isStarted();

    VertexBuilder start();

    void draw();

    void finishDrawing();

    void reset();

    ByteBuffer getByteBuffer();

    BufferBuilder.State getVertexData();

    void setVertexData(BufferBuilder.State state);
}
