package fi.dy.masa.malilib.util.game.wrap;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.HitResult;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class GameWrap
{
    public static Minecraft getClient()
    {
        return Minecraft.getInstance();
    }

    @Nullable
    public static ClientLevel getClientWorld()
    {
        return getClient().level;
    }

    @Nullable
    public static ServerLevel getClientPlayersServerWorld()
    {
        Entity player = getClientPlayer();
        MinecraftServer server = getIntegratedServer();
        return player != null && server != null ? server.getLevel(player.level().dimension()) : null;
    }

    @Nullable
    public static RegistryAccess getClientRegistryManager()
    {
        return getClientWorld() != null ? getClientWorld().registryAccess() : null;
    }

    @Nullable
    public static RegistryAccess getServerRegistryManager()
    {
        return getClientPlayersServerWorld() != null ? getClientPlayersServerWorld().registryAccess() : null;
    }

    @Nullable
    public static Player getClientPlayer()
    {
        return getClient().player;
    }

    @Nullable
    public static Inventory getPlayerInventory()
    {
        Player player = getClient().player;
        return player != null ? player.getInventory() : null;
    }

    public static MultiPlayerGameMode getInteractionManager()
    {
        return getClient().gameMode;
    }

    public static void clickSlot(int syncId, int slotId, int mouseButton, ClickType clickType)
    {
        MultiPlayerGameMode controller = getInteractionManager();

        if (controller != null)
        {
            controller.handleInventoryMouseClick(syncId, slotId, mouseButton, clickType, getClientPlayer());
        }
    }

    public static double getPlayerReachDistance()
    {
        if (getClientPlayer() != null)
        {
            return getClientPlayer().blockInteractionRange();
        }

        return 4.5d;
    }

    @Nullable
    public static MinecraftServer getIntegratedServer()
    {
        return getClient().getSingleplayerServer();
    }

    @Nullable
    public static ClientPacketListener getNetworkConnection()
    {
        return getClient().getConnection();
    }

    public static Options getOptions()
    {
        return getClient().options;
    }

    public static GameRules getGameRules()
    {
        if (getClient().hasSingleplayerServer())
        {
            if (getClient().getSingleplayerServer() != null)
            {
                getClient().getSingleplayerServer().findRespawnDimension().getGameRules();
            }
        }
        else
        {
            if (getClient().getConnection() != null)
            {
                return new GameRules(getClient().getConnection().enabledFeatures());
            }
        }

        return new GameRules(FeatureFlagSet.of());
    }

    public static void printToChat(String msg)
    {
        if (getClient().level != null)
        {
            getClient().gui.getChat().addMessage(Component.nullToEmpty(msg));
        }
    }

    public static void showHotbarMessage(String msg)
    {
        if (getClient().level != null)
        {
            getClient().gui.setOverlayMessage(Component.nullToEmpty(msg), false);
        }
    }

    public static boolean sendChatMessage(String command)
    {
        Player player = getClientPlayer();

        if (player != null)
        {
            player.displayClientMessage(Component.nullToEmpty(command), false);
            return true;
        }

        return false;
    }

    public static boolean sendCommand(String command)
    {
        if (command.startsWith("/") == false)
        {
            command = "/" + command;
        }

        return sendChatMessage(command);
    }

    /**
     * @return The camera entity, if it's not null, otherwise returns the client player entity.
     */
    @Nullable
    public static Entity getCameraEntity()
    {
        Minecraft mc = getClient();
        Entity entity = mc.getCameraEntity();
        return entity != null ? entity : mc.player;
    }

    public static String getPlayerName()
    {
        Entity player = getClientPlayer();
        return player != null ? player.getName().tryCollapseToString() : "?";
    }

    public static HitResult getHitResult()
    {
        return getClient().hitResult;
    }

    public static long getCurrentWorldTick()
    {
        Level world = getClientWorld();
        return world != null ? world.getGameTime() : -1L;
    }

    public static boolean isCreativeMode()
    {
        Player player = getClientPlayer();
        return player != null && player.hasInfiniteMaterials();
    }

    public static int getRenderDistanceChunks()
    {
        return getOptions().getEffectiveRenderDistance();
    }

    public static int getVanillaOptionsScreenScale()
    {
        return getOptions().guiScale().get();
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isLocalServer();
    }

    public static boolean isUnicode()
    {
        return getClient().isEnforceUnicode();
    }

    public static boolean isHideGui()
    {
        return getOptions().hideGui;
    }

    public static void scheduleToClientThread(Runnable task)
    {
        Minecraft mc = getClient();

        if (mc.isSameThread())
        {
            task.run();
        }
        else
        {
            mc.wrapRunnable(task);
        }
    }

    public static void profilerPush(String name)
    {
        Profiler.get().push(name);
    }

    public static void profilerPush(Supplier<String> nameSupplier)
    {
        Profiler.get().push(nameSupplier);
    }

    public static void profilerSwap(String name)
    {
        Profiler.get().popPush(name);
    }

    public static void profilerSwap(Supplier<String> nameSupplier)
    {
        Profiler.get().popPush(nameSupplier);
    }

    public static void profilerPop()
    {
        Profiler.get().pop();
    }

    public static void openFile(Path file)
    {
        //OpenGlHelper.openFile(file.toFile());
        Util.getPlatform().openPath(file);
    }

    @Nullable
    public static Path getCurrentSinglePlayerWorldDirectory()
    {
        if (isSinglePlayer())
        {
            MinecraftServer server = getIntegratedServer();

            if (server != null)
            {
                return server.getWorldPath(LevelResource.ROOT);
            }
        }

        return null;
    }
}
