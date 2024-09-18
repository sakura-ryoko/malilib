package fi.dy.masa.malilib.mixin.test;

import net.minecraft.client.network.DataQueryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DataQueryHandler.class)
public interface IMixinDataQueryHandler
{
    @Accessor("expectedTransactionId")
    int malilib_currentTransactionId();
}
