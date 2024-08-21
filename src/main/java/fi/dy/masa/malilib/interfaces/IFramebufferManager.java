package fi.dy.masa.malilib.interfaces;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;

public interface IFramebufferManager
{
    void registerFramebufferHandler(IFramebufferFactory handler);
    void setFramebufferHandle(IFramebufferFactory handler, @Nonnull Handle<Framebuffer> framebufferHandle);
    @Nullable Handle<Framebuffer> getFramebufferHandle(IFramebufferFactory handler);
    Set<Identifier> getStages();
}
