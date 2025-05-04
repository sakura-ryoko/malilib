package fi.dy.masa.malilib.util.nbt;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;

import fi.dy.masa.malilib.mixin.nbt.IMixinNbtReadView;
import fi.dy.masa.malilib.mixin.nbt.IMixinNbtWriteView;

public class NbtView
{
    private static final ErrorReporter log = ErrorReporter.EMPTY;
    private ReadView reader;
    private WriteView writer;

    private NbtView() {}

    public static NbtView getReader(NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        NbtView wrapper = new NbtView();
        wrapper.reader = NbtReadView.create(log, registry, nbt);
        wrapper.writer = null;
        return wrapper;
    }

    public static NbtView getWriter(@Nonnull DynamicRegistryManager registry)
    {
        NbtView wrapper = new NbtView();
        wrapper.reader = null;
        wrapper.writer = NbtWriteView.create(log, registry);
        return wrapper;
    }

    public ErrorReporter getLogger()
    {
        return log;
    }

    public boolean isReader() { return this.reader != null; }

    public boolean isWriter() { return this.writer != null; }

    public @Nullable ReadView getReader() { return this.reader; }

    public @Nullable WriteView getWriter() { return this.writer; }

    public @Nullable NbtCompound getNbt()
    {
        if (this.isWriter())
        {
            return ((IMixinNbtWriteView) this.writer).malilib_getNbt();
        }
        else if (this.isReader())
        {
            return ((IMixinNbtReadView) this.reader).malilib_getNbt();
        }

        return null;
    }

    public @Nullable NbtView writeNbt(@Nonnull NbtCompound nbtIn)
    {
        if (this.isReader())
        {
            return null;
        }

        for (String key : nbtIn.getKeys())
        {
            Objects.requireNonNull(this.getNbt()).put(key, nbtIn.get(key));
        }

        return this;
    }
}
