package malilib.util;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;

import malilib.MaLiLib;
import malilib.MaLiLibConfigs;
import malilib.registry.Registry;
import malilib.util.data.Identifier;
import malilib.util.data.LeftRight;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.WorldWrap;

public class StringUtils
{
    /**
     * @return true if the given String is null or has a length of 0
     */
    public static boolean isEmpty(@Nullable String str)
    {
        return str == null || str.length() == 0;
    }

    @Nullable
    public static Identifier identifier(String fullPath)
    {
        try
        {
            return new Identifier(fullPath);
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to create a ResourceLocation: {}", e.getMessage());
            return null;
        }
    }

    @Nullable
    public static Identifier identifier(String nameSpace, String path)
    {
        try
        {
            return new Identifier(nameSpace, path);
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to create a ResourceLocation: {}", e.getMessage());
            return null;
        }
    }

    public static String getModVersionString(String modId)
    {
        try
        {
            Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);

            if (container.isPresent())
            {
                return container.get().getMetadata().getVersion().getFriendlyString();
            }
        }
        catch (Exception ignore) {}

        return "?";
    }

    /**
     * Removes the string <b>extension</b> from the end of <b>str</b>,
     * if <b>str</b> ends in <b>extension</b>
     * @param str
     * @param extension
     * @return
     */
    public static String stripExtensionIfMatches(String str, String extension)
    {
        if (str.endsWith(extension) && str.length() > extension.length())
        {
            return str.substring(0, str.length() - extension.length());
        }

        return str;
    }

    /**
     * Splits the given camel-case string into parts separated by a space
     * @param str
     * @return
     */
    // https://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
    public static String splitCamelCase(String str)
    {
        str = str.replaceAll(
           String.format("%s|%s|%s",
              "(?<=[A-Z])(?=[A-Z][a-z])",
              "(?<=[^A-Z])(?=[A-Z])",
              "(?<=[A-Za-z])(?=[^A-Za-z])"
           ),
           " "
        );

        if (str.length() > 1 && str.charAt(0) > 'Z')
        {
            str = str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1);
        }

        return str;
    }

    /**
     * Returns true if all the characters from needle are found in haystack,
     * and they are found in the same order. There can be an arbitrary number of characters between
     * each found character in the haystack, as long as all of them are found,
     * and such that for example the third character of needle is found after the second character's
     * first valid match in haystack.
     */
    public static boolean containsOrderedCharacters(String needle, String haystack)
    {
        int needleLength = needle.length();
        int startIndex = 0;

        for (int i = 0; i < needleLength; ++i)
        {
            startIndex = haystack.indexOf(needle.charAt(i), startIndex);

            if (startIndex == -1)
            {
                return false;
            }

            ++startIndex;
        }

        return true;
    }

    public static void sendOpenFileChatMessage(String messageKey, Path file)
    {
        TextComponentString name = new TextComponentString(file.getFileName().toString());
        name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.toAbsolutePath().toString()));
        name.getStyle().setUnderlined(Boolean.TRUE);
        GameWrap.getClientPlayer().sendMessage(new TextComponentTranslation(messageKey, name));
    }

    public static int getMaxStringRenderWidth(String... strings)
    {
        return getMaxStringRenderWidth(Arrays.asList(strings));
    }

    public static int getMaxStringRenderWidth(List<String> lines)
    {
        return getMaxStringRenderWidth(lines, (l) -> l);
    }

    public static int getMaxStringRenderWidth(Function<String, String> translator, String... strings)
    {
        return getMaxStringRenderWidth(Arrays.asList(strings), translator);
    }

    public static int getMaxStringRenderWidth(List<String> lines, Function<String, String> translator)
    {
        int width = 0;

        for (String line : lines)
        {
            width = Math.max(width, getStringWidth(translator.apply(line)));
        }

        return width;
    }

    public static <T> int getMaxStringRenderWidthOfObjects(List<T> list, Function<T, String> translator)
    {
        int width = 0;

        for (T item : list)
        {
            width = Math.max(width, getStringWidth(translator.apply(item)));
        }

        return width;
    }

    public static void addTranslatedLines(List<String> linesOut, String translationKey)
    {
        String[] parts = translate(translationKey).split("\\\\n");
        Collections.addAll(linesOut, parts);
    }

    /**
     * Splits the given string into lines up to maxLineLength long
     * @param linesOut
     * @param textIn
     * @param maxLineLength
     */
    public static void splitTextToLines(List<String> linesOut, String textIn, int maxLineLength)
    {
        String[] lines = textIn.split("\\\\n");
        @Nullable String activeColor = null;

        for (String line : lines)
        {
            String[] parts = line.split(" ");
            StringBuilder sb = new StringBuilder(256);
            final int spaceWidth = getStringWidth(" ");
            int lineWidth = 0;

            for (String str : parts)
            {
                int width = getStringWidth(str);

                if ((lineWidth + width + spaceWidth) > maxLineLength)
                {
                    if (lineWidth > 0)
                    {
                        linesOut.add(sb.toString());
                        sb = new StringBuilder(256);
                        lineWidth = 0;
                    }

                    // Long continuous string
                    if (width > maxLineLength)
                    {
                        final int chars = str.length();

                        for (int i = 0; i < chars; ++i)
                        {
                            String c = str.substring(i, i + 1);

                            if (c.equals("§") && i < (chars - 1))
                            {
                                activeColor = str.substring(i, i + 2);
                                sb.append(activeColor);
                                ++i;
                                continue;
                            }

                            lineWidth += getStringWidth(c);

                            if (lineWidth > maxLineLength)
                            {
                                linesOut.add(sb.toString());
                                sb = new StringBuilder(256);
                                lineWidth = 0;

                                if (activeColor != null)
                                {
                                    sb.append(activeColor);
                                }
                            }

                            sb.append(c);
                        }

                        linesOut.add(sb.toString());
                        sb = new StringBuilder(256);
                        lineWidth = 0;
                    }
                }

                if (lineWidth > 0)
                {
                    sb.append(" ");
                }

                if (width <= maxLineLength)
                {
                    sb.append(str);
                    lineWidth += width + spaceWidth;
                }
            }

            linesOut.add(sb.toString());
        }
    }

    public static String getClampedDisplayStringStrlen(List<String> list, final int maxWidth, String prefix, String suffix)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append(prefix);
        int width = prefix.length() + suffix.length();
        final int size = list.size();

        if (size > 0)
        {
            for (int i = 0; i < size && width < maxWidth; i++)
            {
                if (i > 0)
                {
                    sb.append(", ");
                    width += 2;
                }

                String str = list.get(i);
                final int len = str.length();
                int end = Math.min(len, maxWidth - width);

                if (end < len)
                {
                    end = Math.max(0, Math.min(len, maxWidth - width - 3));

                    if (end >= 1)
                    {
                        sb.append(str, 0, end);
                    }

                    sb.append("...");
                    width += end + 3;
                }
                else
                {
                    sb.append(str);
                    width += len;
                }
            }
        }
        else
        {
            sb.append("<empty>");
        }

        sb.append(suffix);

        return sb.toString();
    }

    public static String getDisplayStringForList(List<String> list, final int maxWidth,
                                                 String quote, String prefix, String suffix)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append(prefix);

        String entrySep = ", ";
        String dots = " ...";
        final int listSize = list.size();
        final int widthQuotes = getStringWidth(quote) * 2;
        final int widthSep = getStringWidth(entrySep);
        final int widthDots = getStringWidth(dots);
        final int widthNextMin = widthSep + widthDots;
        int width = getStringWidth(prefix) + getStringWidth(suffix);

        if (listSize > 0)
        {
            for (int listIndex = 0; listIndex < listSize && width < maxWidth; ++listIndex)
            {
                if (listIndex > 0)
                {
                    sb.append(entrySep);
                    width += widthSep;
                }

                String str = list.get(listIndex);
                final int len = getStringWidth(str) + widthQuotes;
                int widthNext = listIndex < listSize - 1 ? widthNextMin : 0;

                if ((width + len + widthNext) <= maxWidth)
                {
                    sb.append(quote).append(str).append(quote);
                    width += len;
                }
                else
                {
                    if ((width + getStringWidth(str.substring(0, 1)) + widthDots) <= maxWidth)
                    {
                        sb.append(quote);
                        width += widthQuotes;

                        for (int i = 0; i < str.length(); ++i)
                        {
                            String c = str.substring(i, i + 1);
                            final int charWidth = getStringWidth(c);

                            if ((width + charWidth + widthDots) <= maxWidth)
                            {
                                sb.append(c);
                                width += charWidth;
                            }
                            else
                            {
                                break;
                            }
                        }

                        sb.append(quote);
                    }

                    sb.append(dots);
                    break;
                }
            }
        }
        else
        {
            sb.append("<empty>");
        }

        sb.append(suffix);

        return sb.toString();
    }

    /**
     * Shrinks the given string until it can fit into the provided maximum width,
     * and adds the provided clamping indicator to indicate that the string is longer than what is shown.
     * @param text
     * @param maxWidth
     * @param side the side from which to shrink the string
     * @param indicator the appended shrinkage indicator, for example "..."
     * @return
     */
    public static String clampTextToRenderLength(String text, final int maxWidth, LeftRight side, String indicator)
    {
        // The entire string fits, just return it as-is
        if (getStringWidth(text) <= maxWidth)
        {
            return text;
        }

        StringBuilder sb = new StringBuilder(128);

        final int indicatorWidth = getStringWidth(indicator);
        final int stringLen = text.length();
        int usedWidth = indicatorWidth;
        int index = 0;
        int lastIndex = stringLen - 1;
        int indexIncrement = 1;

        // Shrink from the left, so append/build from the right
        if (side == LeftRight.LEFT)
        {
            index = stringLen - 1;
            lastIndex = 0;
            indexIncrement = -1;
        }

        while (usedWidth < maxWidth)
        {
            String chr = text.substring(index, index + 1);
            int charWidth = getStringWidth(chr);

            if (usedWidth + charWidth > maxWidth)
            {
                break;
            }

            sb.append(chr);
            usedWidth += charWidth;

            if (index == lastIndex)
            {
                break;
            }

            index += indexIncrement;
        }

        if (side == LeftRight.LEFT)
        {
            return indicator + sb.reverse();
        }

        sb.append(indicator);

        return sb.toString();
    }

    @Nullable
    public static String getWorldOrServerNameOrDefault(String defaultStr)
    {
        String name = getWorldOrServerName();
        return name != null ? name : defaultStr;
    }

    @Nullable
    public static String getWorldOrServerName()
    {
        if (GameWrap.isSinglePlayer())
        {
            IntegratedServer server = GameWrap.getClient().getIntegratedServer();

            if (server != null)
            {
                return FileNameUtils.generateSimpleSafeFileName(server.getFolderName());
            }
        }
        else
        {
            Minecraft mc = GameWrap.getClient();

            if (mc.isConnectedToRealms())
            {
                if (MaLiLibConfigs.Generic.REALMS_COMMON_CONFIG.getBooleanValue())
                {
                    return "realms";
                }
                else
                {
                    NetHandlerPlayClient handler = mc.getConnection();
                    NetworkManager connection = handler != null ? handler.getNetworkManager() : null;

                    if (connection != null)
                    {
                        return "realms_" + stringifyAddress(connection.getRemoteAddress());
                    }
                }
            }

            ServerData server = GameWrap.getClient().getCurrentServerData();

            if (server != null)
            {
                return server.serverIP.trim().replace(':', '_');
            }
        }

        return null;
    }

    /**
     * Returns a file name based on the current server or world name.
     * @param globalData if false, then the name will also include the current dimension ID.
     * @param prefix the prefix to add to the name
     * @param suffix the suffix/file name extension to use
     * @param defaultName the default file name, if getting a per-server/world name fails
     * @return a safe file name for the current server or world
     */
    public static String getStorageFileName(boolean globalData, String prefix, String suffix, String defaultName)
    {
        String name = getWorldOrServerName();

        if (name != null)
        {
            if (globalData)
            {
                name = prefix + name;
            }
            else
            {
                World world = GameWrap.getClientWorld();

                if (world != null)
                {
                    name = prefix + name + "_dim" + WorldWrap.getDimensionIdAsString(world);
                }
            }
        }
        else
        {
            name = prefix + defaultName;
        }

        return FileNameUtils.generateSimpleSafeFileName(name) + suffix;
    }

    public static String stringifyAddress(SocketAddress address)
    {
        String str = address.toString();

        if (str.contains("/"))
        {
            str = str.substring(str.indexOf('/') + 1);
        }

        return str.replace(':', '_');
    }

    public static String getPrettyFileSizeText(long fileSize, int decimalPlaces)
    {
        String[] units = {"B", "KiB", "MiB", "GiB", "TiB"};
        String unitStr = "";
        double size = fileSize;

        for (String unit : units)
        {
            unitStr = unit;

            if (size < 1024.0)
            {
                break;
            }

            size /= 1024.0;
        }

        String fmt = "%." + decimalPlaces + "f %s";
        return String.format(fmt, size, unitStr);
    }

    public static List<String> translateAndLineSplit(String translationKey, Object... args)
    {
        String translated = translate(translationKey, args);
        return Arrays.asList(translated.split("\\n"));
    }

    public static void translateAndLineSplit(Consumer<String> lineConsumer, String translationKey, Object... args)
    {
        String translated = translate(translationKey, args);

        for (String line : translated.split("\\n"))
        {
            lineConsumer.accept(line);
        }
    }

    // Some MCP vs. Yarn vs. MC versions compatibility/wrapper stuff below this

    /**
     * Just a wrapper around I18n, to reduce the number of changed lines between MCP/Yarn versions of mods
     */
    public static String translate(String translationKey, Object... args)
    {
        try
        {
            if (MaLiLibConfigs.Debug.PRINT_TRANSLATION_KEYS.getBooleanValue() &&
                hasTranslation(translationKey))
            {
                MaLiLib.LOGGER.info("Translation key: {}", translationKey);
            }

            if (MaLiLibConfigs.Generic.TRANSLATION_OVERRIDES.getBooleanValue())
            {
                String translation = Registry.TRANSLATION_OVERRIDE_MANAGER.getOverriddenTranslation(translationKey, args);

                if (translation != null)
                {
                    return translation;
                }
            }

            return net.minecraft.client.resources.I18n.format(translationKey, args);
        }
        catch (Exception e)
        {
            return translationKey;
        }
    }

    public static boolean hasTranslation(String translationKey)
    {
        return net.minecraft.client.resources.I18n.hasKey(translationKey);
    }

    /**
     * Just a wrapper to get the font height from the Font/TextRenderer
     */
    public static int getFontHeight()
    {
        return GameWrap.getClient().fontRenderer.FONT_HEIGHT;
    }

    /**
     * Returns the render width of the given string
     */
    public static int getStringWidth(String text)
    {
        return GameWrap.getClient().fontRenderer.getStringWidth(text);
    }
}
