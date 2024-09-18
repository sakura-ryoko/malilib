package fi.dy.masa.malilib.sync.fe;

import net.minecraft.entity.mob.PiglinActivity;

public interface IFakePiglin
{
    boolean canHunt();

    PiglinActivity getActivity();
}
