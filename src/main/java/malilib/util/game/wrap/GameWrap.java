package malilib.util.game.wrap;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import malilib.util.position.HitResult;

public class GameWrap
{
    public static Minecraft getClient()
    {
        return Minecraft.getMinecraft();
    }

    @Nullable
    public static WorldClient getClientWorld()
    {
        return getClient().world;
    }

    @Nullable
    public static WorldServer getClientPlayersServerWorld()
    {
        Entity player = getClientPlayer();
        MinecraftServer server = getIntegratedServer();
        return player != null && server != null ? server.getWorld(player.dimension) : null;
    }

    @Nullable
    public static EntityPlayerSP getClientPlayer()
    {
        return getClient().player;
    }

    @Nullable
    public static InventoryPlayer getPlayerInventory()
    {
        EntityPlayer player = getClient().player;
        return player != null ? player.inventory : null;
    }

    @Nullable
    public static Container getPlayerInventoryContainer()
    {
        EntityPlayer player = getClient().player;
        return player != null ? player.inventoryContainer : null;
    }

    @Nullable
    public static Container getCurrentInventoryContainer()
    {
        EntityPlayer player = getClient().player;
        return player != null ? player.openContainer : null;
    }

    public static PlayerControllerMP getInteractionManager()
    {
        return getClient().playerController;
    }

    public static void clickSlot(int syncId, int slotId, int mouseButton, ClickType clickType)
    {
        PlayerControllerMP controller = getInteractionManager();

        if (controller != null && getClientPlayer() != null)
        {
            controller.windowClick(syncId, slotId, mouseButton, clickType, getClientPlayer());
        }
    }

    public static double getPlayerReachDistance()
    {
        PlayerControllerMP controller = getInteractionManager();
        return controller != null ? controller.getBlockReachDistance() : 0.0;
    }

    public static float getRenderPartialTicks()
    {
        return getClient().getRenderPartialTicks();
    }

    @Nullable
    public static MinecraftServer getIntegratedServer()
    {
        return getClient().getIntegratedServer();
    }

    @Nullable
    public static NetHandlerPlayClient getNetworkConnection()
    {
        return getClient().getConnection();
    }

    public static GameSettings getOptions()
    {
        return getClient().gameSettings;
    }

    public static void printToChat(String msg)
    {
        getClient().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentString(msg));
    }

    public static void showHotbarMessage(String msg)
    {
        getClient().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentString(msg));
    }

    public static boolean sendChatMessage(String command)
    {
        EntityPlayerSP player = getClientPlayer();

        if (player != null)
        {
            player.sendChatMessage(command);
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
        Entity entity = mc.getRenderViewEntity();
        return entity != null ? entity : mc.player;
    }

    public static String getPlayerName()
    {
        Entity player = getClientPlayer();
        return player != null ? player.getName() : "?";
    }

    public static HitResult getHitResult()
    {
        return HitResult.of(getClient().objectMouseOver);
    }

    public static long getClientWorldTotalTick()
    {
        World world = getClientWorld();
        return getWorldTotalTick(world);
    }

    public static long getWorldTotalTick(World world)
    {
        return world != null ? world.getTotalWorldTime() : -1L;
    }

    public static boolean isCreativeMode()
    {
        EntityPlayer player = getClientPlayer();
        return player != null && player.capabilities.isCreativeMode;
    }

    public static int getRenderDistanceChunks()
    {
        return getOptions().renderDistanceChunks;
    }

    public static int getVanillaOptionsScreenScale()
    {
        return GameWrap.getOptions().guiScale;
    }

    public static boolean isSinglePlayer()
    {
        return getClient().isSingleplayer();
    }

    public static boolean isUnicode()
    {
        return getClient().isUnicode();
    }

    public static boolean isHideGui()
    {
        return getOptions().hideGUI;
    }

    public static void scheduleToClientThread(Runnable task)
    {
        Minecraft mc = getClient();

        if (mc.isCallingFromMinecraftThread())
        {
            task.run();
        }
        else
        {
            mc.addScheduledTask(task);
        }
    }

    public static void profilerPush(String name)
    {
        getClient().profiler.startSection(name);
    }

    public static void profilerPush(Supplier<String> nameSupplier)
    {
        getClient().profiler.m_4994039(nameSupplier);
    }

    public static void profilerSwap(String name)
    {
        getClient().profiler.endStartSection(name);
    }

    public static void profilerSwap(Supplier<String> nameSupplier)
    {
        getClient().profiler.m_3681950(nameSupplier);
    }

    public static void profilerPop()
    {
        getClient().profiler.endSection();
    }

    public static void openFile(Path file)
    {
        OpenGlHelper.openFile(file.toFile());
    }

    @Nullable
    public static Path getCurrentSinglePlayerWorldDirectory()
    {
        if (isSinglePlayer())
        {
            MinecraftServer server = getIntegratedServer();
            File file = server.getActiveAnvilConverter().getFile(server.getFolderName(), "icon.png");
            return file.getParentFile().toPath();
        }

        return null;
    }
}
