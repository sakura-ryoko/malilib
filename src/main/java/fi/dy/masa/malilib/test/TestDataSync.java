package fi.dy.masa.malilib.test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.interfaces.ISyncProvider;
import fi.dy.masa.malilib.mixin.IMixinDataQueryHandler;
import fi.dy.masa.malilib.sync.cache.SyncCache;
import fi.dy.masa.malilib.sync.fbe.FakeBlockEntity;
import fi.dy.masa.malilib.sync.fe.FakeEntity;

public class TestDataSync implements ISyncProvider
{
    private static final TestDataSync INSTANCE = new TestDataSync();
    public static TestDataSync getInstance() { return INSTANCE; }

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final Map<Integer, Either<BlockPos, Integer>> transactionToBlockPosOrEntityId = new HashMap<>();
    private SyncCache cache;
    private World world;

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
        this.cache = new SyncCache(world);
    }

    @Override
    public void onStopServices()
    {
        System.out.print("sync - onStartServices()\n");
        this.cache.clear();
        this.world = null;
    }

    @Override
    public World getWorld()
    {
        return this.world;
    }

    @Override
    public SyncCache getCache()
    {
        return this.cache;
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
        System.out.printf("sync - requestBlockEntity() at [%s] state [%s]\n", pos.toShortString(), state != null ? state.toString() : "empty");
        this.requestQueryBlockEntity(pos);
    }

    @Override
    public void requestBlockEntityAt(World world, BlockPos pos)
    {
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
        System.out.printf("sync - requestEntity() id [%d]\n", entityId);
        this.requestQueryEntityData(entityId);
    }

    @Override
    public void requestEntity(UUID uuid)
    {
        System.out.printf("sync - requestEntity() uuid [%s]\n", uuid.toString());
        PlayerEntity player = this.getWorld().getPlayerByUuid(uuid);

        if (player != null)
        {
            this.requestQueryEntityData(player.getId());
        }
    }

    private void requestQueryBlockEntity(BlockPos pos)
    {
        if (!MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
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
        if (!MaLiLibConfigs.Test.TEST_CONFIG_BOOLEAN.getBooleanValue())
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
        return false;
    }

    @Override
    public boolean hasEntity(int entityId)
    {
        return false;
    }

    @Override
    public boolean hasEntity(UUID uuid)
    {
        return false;
    }

    @Override
    public @Nullable FakeBlockEntity getBlockEntity(BlockPos pos)
    {
        System.out.printf("sync - getBlockEntity() at [%s]\n", pos.toShortString());
        return null;
    }

    @Override
    public @Nullable FakeEntity getEntity(int entityId)
    {
        System.out.printf("sync - getEntity() id [%d]\n", entityId);
        return null;
    }

    @Override
    public @Nullable FakeEntity getEntity(UUID uuid)
    {
        System.out.printf("sync - getEntity() uuid [%s]\n", uuid.toString());
        return null;
    }
}
