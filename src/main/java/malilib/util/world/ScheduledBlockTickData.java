package malilib.util.world;

import malilib.util.position.BlockPos;

public class ScheduledBlockTickData
{
    public final BlockPos pos;
    public final String blockName;
    public final int priority;
    public final long delay;
    public final long tickId;

    public ScheduledBlockTickData(BlockPos pos, String blockName, int priority, long delay, long tickId)
    {
        this.pos = pos;
        this.blockName = blockName;
        this.priority = priority;
        this.delay = delay;
        this.tickId = tickId;
    }
}
