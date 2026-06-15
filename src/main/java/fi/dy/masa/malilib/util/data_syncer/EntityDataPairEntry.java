package fi.dy.masa.malilib.util.data_syncer;

import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import fi.dy.masa.malilib.util.data.tag.CompoundData;

public record EntityDataPairEntry(long time, CompoundData data, @Nullable BlockEntity be, @Nullable Entity ent)
{
	@Override
	public @NonNull String toString()
	{
		return "EntityDataPairEntry{time=" + this.time() + ", data=[" + this.data().toString() + "]}";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		EntityDataPairEntry that = (EntityDataPairEntry) o;
		return this.time == that.time && this.data.equals(that.data);
	}

	@Override
	public int hashCode()
	{
		int result = (int) (this.time ^ (this.time >>> 32));
		result = 31 * result + this.data.hashCode();
		return result;
	}
}
