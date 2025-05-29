package fi.dy.masa.malilib.util.nbt;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.storage.*;
import net.minecraft.util.ErrorReporter;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.mixin.nbt.IMixinNbtReadView;
import fi.dy.masa.malilib.mixin.nbt.IMixinNbtWriteView;

/**
 * This is a wrapper to the new "ReadView / WriteView" that Mojang made; and provides a seamless way to extract an NbtCompound to / from it.
 * This Wrapper also 'manages' the ErrorReporter for you.
 */
public class NbtView
{
    private static final ErrorReporter log = ErrorReporter.EMPTY;
    private ReadView reader;
    private WriteView writer;

    private NbtView() {}

    /**
     * Build a Reader instance.
     * @param nbt ()
     * @param registry ()
     * @return ()
     */
    public static NbtView getReader(NbtCompound nbt, @Nonnull DynamicRegistryManager registry)
    {
        NbtView wrapper = new NbtView();
        wrapper.reader = NbtReadView.create(log, registry, nbt);
        wrapper.writer = null;
        return wrapper;
    }

    /**
     * Build a Writer instance, with a new empty Writer.
     * @param registry ()
     * @return ()
     */
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

    public @Nullable NbtReadView asNbtReader() { return (NbtReadView) this.reader; }

    public @Nullable NbtWriteView asNbtWriter() { return (NbtWriteView) this.writer; }

    public @Nullable ReadContext getReaderContext()
    {
        if (this.isReader())
        {
            return ((IMixinNbtReadView) this.reader).malilib_getContext();
        }

        return null;
    }

    public @Nullable DynamicOps<?> getWriterOps()
    {
        if (this.isWriter())
        {
            return ((IMixinNbtWriteView) this.writer).malilib_getOps();
        }

        return null;
    }

    /**
     * Return whatever NbtCompound that this Reader/Writer contains.
     * @return ()
     */
    public @Nullable NbtCompound readNbt()
    {
        if (this.isReader())
        {
            return ((IMixinNbtReadView) this.reader).malilib_getNbt();
        }
        else if (this.isWriter())
        {
            return ((IMixinNbtWriteView) this.writer).malilib_getNbt();
        }

        return null;
    }

    /**
     * Copy an NbtCompound into a Writer instance.  NOTE; that a Reader instance is Read-Only.
     * @param nbtIn ()
     * @return ()
     */
    public @Nullable NbtView writeNbt(@Nonnull NbtCompound nbtIn)
    {
        if (this.isReader())
        {
            return null;
        }

        for (String key : nbtIn.getKeys())
        {
            Objects.requireNonNull(this.readNbt()).put(key, nbtIn.get(key));
        }

        return this;
    }

    /**
     * Reads a Flat Map value from the Nbt.
     * @param <T> ()
     * @param mapCodec ()
     * @return ()
     */
    public <T> Optional<T> readFlatMap(MapCodec<T> mapCodec)
    {
        if (this.isWriter())
        {
            return Optional.empty();
        }

       return NbtUtils.readFlatMap(Objects.requireNonNullElse(this.readNbt(), new NbtCompound()), mapCodec);
    }

    /**
     * Reads a CODEC utilizing 'key' from the Nbt
     * @param <T> ()
     * @param key ()
     * @param codec ()
     * @return ()
     */
    public <T> Optional<T> readCodec(String key, Codec<T> codec)
    {
        if (this.isWriter())
        {
            return Optional.empty();
        }

        try
        {
            return this.reader.read(key, codec);
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.warn("NbtView#readCodec: Exception reading from key '{}'; {}", key, err.getLocalizedMessage());
            return Optional.empty();
        }
    }

    /**
     * Writes a Flat Map value to the NBT
     * @param <T> ()
     * @param mapCodec ()
     * @param value ()
     * @return ()
     */
    public <T> NbtCompound writeFlatMap(MapCodec<T> mapCodec, T value)
    {
        if (this.isReader())
        {
            return new NbtCompound();
        }

        this.writeNbt(NbtUtils.writeFlatMap(mapCodec, value));
        return this.readNbt();
    }

    /**
     * Writes a CODEC utilizing 'key' to the Nbt
     * @param <T> ()
     * @param key ()
     * @param codec ()
     * @param value ()
     * @return ()
     */
    public <T> NbtCompound writeCodec(String key, Codec<T> codec, T value)
    {
        if (this.isReader())
        {
            return new NbtCompound();
        }

        try
        {
            this.writer.put(key, codec, value);
            return this.readNbt();
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.warn("NbtView#writeCodec: Exception writing to key '{}'; {}", key, err.getLocalizedMessage());
            return new NbtCompound();
        }
    }
}
