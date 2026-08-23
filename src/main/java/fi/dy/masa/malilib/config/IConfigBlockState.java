package fi.dy.masa.malilib.config;

import net.minecraft.block.BlockState;

public interface IConfigBlockState extends IConfigValue
{
	BlockState getBlockStateValue();

	BlockState getDefaultBlockStateValue();

	void setBlockStateValue(BlockState value);

	BlockState getLastBlockStateValue();

	void updateLastBlockStateValue();
}
