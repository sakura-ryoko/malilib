package fi.dy.masa.malilib.config;

import java.util.List;
import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.block.state.BlockState;

public interface IConfigBlockStateList extends IConfigBase
{
	List<BlockState> getBlockStates();

	ImmutableList<BlockState> getDefaultBlockStates();

	void setBlockStates(List<BlockState> states);

	void setModified();

	default List<BlockState> getLastBlockStateListValue() { return this.getDefaultBlockStates(); }

	default void updateLastBlockStateListValue() {}
}
