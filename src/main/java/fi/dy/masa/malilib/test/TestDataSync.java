package fi.dy.masa.malilib.test;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.interfaces.ISyncProvider;
import fi.dy.masa.malilib.mixin.IMixinDataQueryHandler;
import fi.dy.masa.malilib.sync.data.SyncData;
import fi.dy.masa.malilib.sync.SyncDataCache;

public class TestDataSync<B extends SyncData, E extends SyncData> implements ISyncProvider, IClientTickHandler, AutoCloseable
{
    private static final TestDataSync<?, ?> INSTANCE = new TestDataSync<>();

    public static TestDataSync<?, ?> getInstance() { return INSTANCE; }

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final Map<Integer, Either<BlockPos, Integer>> transactionToBlockPosOrEntityId = new HashMap<>();
    private final SyncDataCache<B, E> cache = new SyncDataCache<>();
    private World world;
    private static final int TIMEOUT = 5;
    private long lastTick;

    @Override
    public void onInstanceStart()
    {
        System.out.print("sync - onInstanceStart()\n");
    }

    @Override
    public void onInstanceStop()
    {
        System.out.print("sync - onInstanceStop()\n");
    }

    @Override
    public void onStartServices(World world)
    {
        System.out.print("sync - onStartServices()\n");
        this.world = world;
        this.cache.setWorld(world);
        this.lastTick = System.currentTimeMillis();
    }

    @Override
    public void onStopServices()
    {
        System.out.print("sync - onStopServices()\n");
        this.cache.clear();
        this.world = null;
    }

    @Override
    public World getWorld()
    {
        if (this.world == null && mc.world != null)
        {
            this.world = mc.world;
        }

        return this.world;
    }

    @Override
    public SyncDataCache<B, E> getCache()
    {
        return this.cache;
    }

    @Override
    public void onClientTick(MinecraftClient mc)
    {
        long tickTime = TIMEOUT * 1000L;
        long now = System.currentTimeMillis();
        long elapsed = now - this.lastTick;

        if (elapsed > tickTime)
        {
            // Tick the Cache
            this.getCache().tickEntities(now, tickTime);
            this.lastTick = System.currentTimeMillis();
        }
        else if (this.lastTick > now)
        {
            this.lastTick = now;
        }
    }

    private static ClientPlayNetworkHandler getVanillaHandler()
    {
        if (mc.player != null)
        {
            return mc.player.networkHandler;
        }

        return null;
    }

    @Override
    public void requestBlockEntity(BlockPos pos, @Nullable BlockState state)
    {
        if (this.world == null)
        {
            return;
        }
        System.out.printf("sync - requestBlockEntity() at [%s] state [%s]\n", pos.toShortString(), state != null ? state.toString() : "empty");
        this.requestQueryBlockEntity(pos);
    }

    @Override
    public void requestBlockEntityAt(World world, BlockPos pos)
    {
        if (this.world == null)
        {
            this.world = world;
        }
        if (!(world instanceof ServerWorld))
        {
            BlockState state = world.getBlockState(pos);
            this.requestBlockEntity(pos, state);

            if (state.getBlock() instanceof ChestBlock)
            {
                ChestType type = state.get(ChestBlock.CHEST_TYPE);

                if (type != ChestType.SINGLE)
                {
                    this.requestBlockEntity(pos.offset(ChestBlock.getFacing(state)), state);
                }
            }
        }
    }

    @Override
    public void requestEntity(int entityId)
    {
        if (this.world == null)
        {
            return;
        }
        System.out.printf("sync - requestEntity() id [%d]\n", entityId);
        this.requestQueryEntityData(entityId);
    }

    private void requestQueryBlockEntity(BlockPos pos)
    {
        if (!MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
        {
            return;
        }

        ClientPlayNetworkHandler handler = getVanillaHandler();

        if (handler != null)
        {
            System.out.printf("sync - requestQueryBlockEntity() pos [%s]\n", pos.toShortString());

            handler.getDataQueryHandler().queryBlockNbt(pos, nbtCompound -> this.onReceiveBlockEntity(pos, nbtCompound));
            this.transactionToBlockPosOrEntityId.put(((IMixinDataQueryHandler) handler.getDataQueryHandler()).malilib_currentTransactionId(), Either.left(pos));
        }
    }

    private void requestQueryEntityData(int entityId)
    {
        if (!MaLiLibConfigs.Test.TEST_SYNC_ENABLE.getBooleanValue())
        {
            return;
        }

        ClientPlayNetworkHandler handler = getVanillaHandler();

        if (handler != null)
        {
            System.out.printf("sync - requestQueryEntityData() entityId [%d]\n", entityId);

            handler.getDataQueryHandler().queryEntityNbt(entityId, nbtCompound -> this.onReceiveEntity(entityId, nbtCompound));
            this.transactionToBlockPosOrEntityId.put(((IMixinDataQueryHandler) handler.getDataQueryHandler()).malilib_currentTransactionId(), Either.right(entityId));
        }
    }

    @Override
    public void handleVanillaQueryNbt(int transactionId, NbtCompound nbt)
    {
        Either<BlockPos, Integer> either = this.transactionToBlockPosOrEntityId.remove(transactionId);

        if (either != null)
        {
            System.out.printf("sync - handleVanillaQueryNbt() transactionId [%d] nbt [%s]\n", transactionId, nbt.toString());
            either.ifLeft(pos -> onReceiveBlockEntity(pos, nbt)).ifRight(entityId -> onReceiveEntity(entityId, nbt));
        }
    }

    @Override
    public void onReceiveBlockEntity(BlockPos pos, NbtCompound nbt)
    {
        System.out.printf("sync - onReceiveBlockEntity() at [%s] nbt [%s]\n", pos.toShortString(), nbt.toString());
    }

    @Override
    public void onReceiveEntity(int entityId, NbtCompound nbt)
    {
        System.out.printf("sync - onReceiveEntity() id [%d] nbt [%s]\n", entityId, nbt.toString());
    }

    @Override
    public boolean hasBlockEntity(BlockPos pos)
    {
        return this.cache.hasBlockEntity(pos);
    }

    @Override
    public boolean hasEntity(int entityId)
    {
        return this.cache.hasEntity(entityId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable B getBlockEntity(BlockPos pos)
    {
        System.out.printf("sync - getBlockEntity() at [%s]\n", pos.toShortString());

        return (B) this.cache.getBlockEntity(pos);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable E getEntity(int entityId)
    {
        System.out.printf("sync - getEntity() id [%d]\n", entityId);

        return (E) this.cache.getEntity(entityId);
    }

    public void clear()
    {
        this.cache.clear();
    }

    @Override
    public void close() throws Exception
    {
        this.clear();
        this.cache.close();
    }
}
