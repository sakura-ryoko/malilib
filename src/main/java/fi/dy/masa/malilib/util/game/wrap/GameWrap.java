package fi.dy.masa.malilib.util.game.wrap;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public class GameWrap
{
    public static MinecraftClient getClient()
    {
        return MinecraftClient.getInstance();
    }

    @Nullable
    public static ClientWorld getClientWorld()
    {
        return getClient().world;
    }

    @Nullable
    public static ServerWorld getClientPlayersServerWorld()
    {
        Entity player = getClientPlayer();
        MinecraftServer server = getIntegratedServer();
        return player != null && server != null ? server.getWorld(player.getEntityWorld().getRegistryKey()) : null;
    }

    @Nullable
    public static DynamicRegistryManager getClientRegistryManager()
    {
        return getClientWorld() != null ? getClientWorld().getRegistryManager() : null;
    }

    @Nullable
    public static DynamicRegistryManager getServerRegistryManager()
    {
        return getClientPlayersServerWorld() != null ? getClientPlayersServerWorld().getRegistryManager() : null;
    }

    @Nullable
    public static PlayerEntity getClientPlayer()
    {
        return getClient().player;
    }

    @Nullable
    public static PlayerInventory getPlayerInventory()
    {
        PlayerEntity player = getClient().player;
        return player != null ? player.getInventory() : null;
    }

    public static ClientPlayerInteractionManager getInteractionManager()
    {
        return getClient().interactionManager;
    }

    public static void clickSlot(int syncId, int slotId, int mouseButton, SlotActionType clickType)
    {
        ClientPlayerInteractionManager controller = getInteractionManager();

        if (controller != null)
        {
            controller.clickSlot(syncId, slotId, mouseButton, clickType, getClientPlayer());
        }
    }

    public static double getPlayerReachDistance()
    {
        if (getClientPlayer() != null)
        {
            return getClientPlayer().getBlockInteractionRange();
        }

        return 4.5d;
    }

    @Nullable
    public static MinecraftServer getIntegratedServer()
    {
        return getClient().getServer();
    }

    @Nullable
    public static ClientPlayNetworkHandler getNetworkConnection()
    {
        return getClient().getNetworkHandler();
    }

    public static GameOptions getOptions()
    {
        return getClient().options;
    }

    public static GameRules getGameRules()
    {
        if (getClient().isIntegratedServerRunning())
        {
            if (getClient().getServer() != null)
            {
                return getClient().getServer().getGameRules();
            }
        }
        else
        {
            if (getClient().getNetworkHandler() != null)
            {
                return new GameRules(getClient().getNetworkHandler().getEnabledFeatures());
            }
        }

        return new GameRules(FeatureSet.empty());
    }

    public static void printToChat(String msg)
    {
        if (getClient().world != null)
        {
            getClient().inGameHud.getChatHud().addMessage(Text.of(msg));
        }
    }

    public static void showHotbarMessage(String msg)
    {
        if (getClient().world != null)
        {
            getClient().inGameHud.setOverlayMessage(Text.of(msg), false);
        }
    }

    public static boolean sendChatMessage(String command)
    {
        PlayerEntity player = getClientPlayer();

        if (player != null)
        {
            player.sendMessage(Text.of(command), false);
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
        MinecraftClient mc = getClient();
        Entity entity = mc.getCameraEntity();
        return entity != null ? entity : mc.player;
    }

    public static String getPlayerName()
    {
        Entity player = getClientPlayer();
        return player != null ? player.getName().getLiteralString() : "?";
    }

    public static HitResult getHitResult()
    {
        return getClient().crosshairTarget;
    }

    public static long getCurrentWorldTick()
    {
        World world = getClientWorld();
        return world != null ? world.getTime() : -1L;
    }

    public static boolean isCreativeMode()
    {
        PlayerEntity player = getClientPlayer();
        return player != null && player.isInCreativeMode();
    }

    public static int getRenderDistanceChunks()
    {
        return getOptions().getClampedViewDistance();
    }

    public static int getVanillaOptionsScreenScale()
    {
        return getOptions().getGuiScale().getValue();
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isInSingleplayer();
    }

    public static boolean isUnicode()
    {
        return getClient().forcesUnicodeFont();
    }

    public static boolean isHideGui()
    {
        return getOptions().hudHidden;
    }

    public static void scheduleToClientThread(Runnable task)
    {
        MinecraftClient mc = getClient();

        if (mc.isOnThread())
        {
            task.run();
        }
        else
        {
            mc.createTask(task);
        }
    }

    public static void profilerPush(String name)
    {
        Profilers.get().push(name);
    }

    public static void profilerPush(Supplier<String> nameSupplier)
    {
        Profilers.get().push(nameSupplier);
    }

    public static void profilerSwap(String name)
    {
        Profilers.get().swap(name);
    }

    public static void profilerSwap(Supplier<String> nameSupplier)
    {
        Profilers.get().swap(nameSupplier);
    }

    public static void profilerPop()
    {
        Profilers.get().pop();
    }

    public static void openFile(Path file)
    {
        //OpenGlHelper.openFile(file.toFile());
        Util.getOperatingSystem().open(file);
    }

    @Nullable
    public static Path getCurrentSinglePlayerWorldDirectory()
    {
        if (isSinglePlayer())
        {
            MinecraftServer server = getIntegratedServer();

            if (server != null)
            {
                return server.getSavePath(WorldSavePath.ROOT);
            }
        }

        return null;
    }
}
