package fi.dy.masa.malilib.util.data_syncer;

import org.jspecify.annotations.NonNull;

import fi.dy.masa.malilib.util.data.tag.CompoundData;

public record EntityDataEntry(long time, CompoundData data)
{
	@Override
	public @NonNull String toString()
	{
		return "EntityDataEntry{time=" + this.time() + ", data=[" + this.data().toString() + "]}";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		EntityDataEntry that = (EntityDataEntry) o;
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
