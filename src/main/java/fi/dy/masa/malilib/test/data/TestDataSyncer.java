package fi.dy.masa.malilib.test.data;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import fi.dy.masa.malilib.interfaces.IDataSyncer;
import fi.dy.masa.malilib.util.WorldUtils;

@ApiStatus.Experimental
public class TestDataSyncer implements IDataSyncer
{
    private static final TestDataSyncer INSTANCE = new TestDataSyncer();

    public TestDataSyncer() { }

    public static TestDataSyncer getInstance() { return INSTANCE; }

    @Override
    public Level getWorld()
    {
        return WorldUtils.getBestWorld(Minecraft.getInstance());
    }
}
