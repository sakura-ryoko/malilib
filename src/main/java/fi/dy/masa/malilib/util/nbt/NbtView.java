package fi.dy.masa.malilib.util.nbt;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadContext;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.world.level.storage.*;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.mixin.nbt.IMixinNbtReadView;
import fi.dy.masa.malilib.mixin.nbt.IMixinNbtWriteView;
import fi.dy.masa.malilib.util.data.tag.CompoundData;
import fi.dy.masa.malilib.util.data.tag.converter.DataConverterNbt;
import fi.dy.masa.malilib.util.data.tag.util.DataTypeUtils;

/**
 * This is a wrapper to the new "ReadView / WriteView" that Mojang made; and provides a seamless way to extract an NbtCompound to / from it.
 * This Wrapper also 'manages' the ErrorReporter for you.
 */
public class NbtView
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MaLiLibReference.MOD_ID+"-NbtView");
    private static final ErrorReporter log = new ErrorReporter.Logging(LOGGER);
    private ReadView reader;
    private WriteView writer;

    private NbtView() { }

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
	 * Build a Reader instance.
	 * @param data ()
	 * @param registry ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public static NbtView getReader(CompoundData data, @Nonnull DynamicRegistryManager registry)
	{
		NbtView wrapper = new NbtView();
		wrapper.reader = NbtReadView.create(log, registry, DataConverterNbt.toVanillaCompound(data));
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

    public ErrorReporter getErrorReporter()
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

        LOGGER.error("getReaderContext(): Called from a Writer Context");
        return null;
    }

    public @Nullable DynamicOps<?> getWriterOps()
    {
        if (this.isWriter())
        {
            return ((IMixinNbtWriteView) this.writer).malilib_getOps();
        }

        LOGGER.error("getWriterOps(): Called from a Reader Context");
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

        LOGGER.error("readNbt(): General failure");
        return null;
    }

	/**
	 * Return whatever NbtCompound that this Reader/Writer contains; but as a CompoundData
	 * @return ()
	 */
	public @Nullable CompoundData readData()
	{
		NbtCompound nbt = this.readNbt();

		if (nbt != null)
		{
			return DataConverterNbt.fromVanillaCompound(nbt);
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
            LOGGER.error("writeNbt(): Called from a Reader Context");
            return null;
        }

        for (String key : nbtIn.getKeys())
        {
            Objects.requireNonNull(this.readNbt()).put(key, nbtIn.get(key));
        }

        return this;
    }

	/**
	 * Copy an CompoundData into a Writer instance.  NOTE; that a Reader instance is Read-Only.
	 * @param dataIn ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public @Nullable NbtView writeData(@Nonnull CompoundData dataIn)
	{
		NbtCompound nbt = DataConverterNbt.toVanillaCompound(dataIn);

		if (nbt != null)
		{
			return this.writeNbt(nbt);
		}

		return null;
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
            LOGGER.error("readFlatMap(): Called from a Writer Context");
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
            LOGGER.error("readCodec(): Called from a Writer Context");
            return Optional.empty();
        }

        try
        {
            return this.reader.read(key, codec);
        }
        catch (Exception err)
        {
            LOGGER.warn("readCodec(): Exception reading from key '{}'; {}", key, err.getLocalizedMessage());
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
            LOGGER.error("writeFlatMap(): Called from a Reader Context");
            return new NbtCompound();
        }

        this.writeNbt(NbtUtils.writeFlatMap(mapCodec, value));
        return this.readNbt();
    }

	/**
	 * Writes a Flat Map value to the Data Tag
	 * @param <T> ()
	 * @param mapCodec ()
	 * @param value ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public <T> CompoundData writeFlatMapNew(MapCodec<T> mapCodec, T value)
	{
		if (this.isReader())
		{
			LOGGER.error("writeFlatMapNew(): Called from a Reader Context");
			return new CompoundData();
		}

		this.writeData(DataTypeUtils.writeFlatMap(mapCodec, value));
		return this.readData();
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
            LOGGER.error("writeCodec(): Called from a Reader Context");
            return new NbtCompound();
        }

        try
        {
            this.writer.put(key, codec, value);
            return this.readNbt();
        }
        catch (Exception err)
        {
            LOGGER.warn("writeCodec(): Exception writing to key '{}'; {}", key, err.getLocalizedMessage());
            return new NbtCompound();
        }
    }

	/**
	 * Writes a CODEC utilizing 'key' to the Data Tag
	 * @param <T> ()
	 * @param key ()
	 * @param codec ()
	 * @param value ()
	 * @return ()
	 */
	@ApiStatus.Experimental
	public <T> CompoundData writeCodecNew(String key, Codec<T> codec, T value)
	{
		if (this.isReader())
		{
			LOGGER.error("writeCodecNew(): Called from a Reader Context");
			return new CompoundData();
		}

		try
		{
			this.writer.put(key, codec, value);
			return this.readData();
		}
		catch (Exception err)
		{
			LOGGER.warn("writeCodecNew(): Exception writing to key '{}'; {}", key, err.getLocalizedMessage());
			return new CompoundData();
		}
	}
}
