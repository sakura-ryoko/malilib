package fi.dy.masa.malilib.config;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.level.block.state.BlockState;

@ApiStatus.Experimental
public interface IConfigBlockState extends IConfigValue
{
	BlockState getBlockStateValue();

	BlockState getDefaultBlockStateValue();

	void setBlockStateValue(BlockState value);

	BlockState getLastBlockStateValue();

	void updateLastBlockStateValue();
}
