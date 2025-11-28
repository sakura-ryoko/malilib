package fi.dy.masa.malilib.mixin.network;

import net.minecraft.client.DebugQueryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugQueryHandler.class)
public interface IMixinDataQueryHandler
{
    @Accessor("transactionId")
    int malilib_currentTransactionId();
}