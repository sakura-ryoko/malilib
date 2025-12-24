package fi.dy.masa.malilib.util;

import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import fi.dy.masa.malilib.MaLiLibReference;
import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.JsonOps;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.util.time.DurationFormat;

/**
 * File has been merged with Post-Rewrite StringUtils
 */
public class StringUtils
{
    @Nullable
    public static Identifier identifier(String fullPath)
    {
        try
        {
            return Identifier.of(fullPath);
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
            return Identifier.of(nameSpace, path);
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.error("Exception while trying to create a ResourceLocation: {}", e.getMessage());
            return null;
        }
    }

    public static String getModVersionString(String modId)
    {
        for (net.fabricmc.loader.api.ModContainer container : net.fabricmc.loader.api.FabricLoader.getInstance().getAllMods())
        {
            if (container.getMetadata().getId().equals(modId))
            {
                return container.getMetadata().getVersion().getFriendlyString();
            }
        }

        return "?";
    }

    public static String getMCVersionString()
    {
        return MaLiLibReference.MC_VERSION;
    }

    public static int getMCDataVersion()
    {
        return MaLiLibReference.MC_DATA_VERSION;
    }

    /**
     * Removes the string <b>extension</b> from the end of <b>str</b>,
     * if <b>str</b> ends in <b>extension</b>
     * @param str ()
     * @param extension ()
     * @return ()
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
     * Parses the given string as a hexadecimal value, if it begins with '#' or '0x'.
     * Otherwise tries to parse it as a regular base 10 integer.
     * @param colorStr ()
     * @param defaultColor ()
     * @return ()
     */
    public static int getColor(String colorStr, int defaultColor)
    {
        Pattern pattern = Pattern.compile("(?:0x|#)([a-fA-F0-9]{1,8})");
        Matcher matcher = pattern.matcher(colorStr);

        if (matcher.matches())
        {
            try { return (int) Long.parseLong(matcher.group(1), 16); }
            catch (NumberFormatException e) { return defaultColor; }
        }

        try { return Integer.parseInt(colorStr, 10); }
        catch (NumberFormatException e) { return defaultColor; }
    }

    /**
     * Splits the given camel-case string into parts separated by a space
     * @param str ()
     * @return ()
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

    public static void sendOpenFileChatMessage(PlayerEntity sender, String messageKey, File file)
    {
        Text name = Text.literal(file.getName())
            .formatted(net.minecraft.util.Formatting.UNDERLINE)
            .styled((style) -> style.withClickEvent(new ClickEvent.OpenFile(file.getAbsolutePath())));

        sender.sendMessage(Text.translatable(messageKey, name), false);
    }

    public static void sendOpenFileChatMessage(PlayerEntity sender, String messageKey, Path file)
    {
        Text name = Text.literal(file.getFileName().toString())
                        .formatted(net.minecraft.util.Formatting.UNDERLINE)
                        .styled((style) -> style.withClickEvent(new ClickEvent.OpenFile(file.toAbsolutePath())));

        sender.sendMessage(Text.translatable(messageKey, name), false);
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
        String[] parts = translate(translationKey).split("\\\\n|\\n");
        Collections.addAll(linesOut, parts);
    }

    /**
     * Splits the given string into lines up to maxLineLength long
     * @param linesOut ()
     * @param textIn ()
     * @param maxLineLength ()
     */
    public static void splitTextToLines(List<String> linesOut, String textIn, int maxLineLength)
    {
        String[] lines = textIn.split("\\\\n|\\n");
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
                        sb.append(str.substring(0, end));
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

    public static String getClampedDisplayStringRenderlen(List<String> list, final int maxWidth,
                                                          String prefix, String suffix)
    {
        StringBuilder sb = new StringBuilder(128);
        sb.append(prefix);

        String entrySep = ", ";
        String dots = " ...";
        final int listSize = list.size();
        final int widthSep = getStringWidth(entrySep);
        final int widthDots = getStringWidth(dots);
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
                final int len = getStringWidth(str);

                if ((width + len) <= maxWidth)
                {
                    sb.append(str);
                    width += len;
                }
                else
                {
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

                    sb.append(dots);
                    width += widthDots;
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
     * @param text ()
     * @param maxWidth ()
     * @param side the side from which to shrink the string
     * @param indicator the appended shrinkage indicator, for example "..."
     * @return ()
     */
    
    public static String clampTextToRenderLength(String text, final int maxWidth, LeftRight side, String indicator)
    {
        int textWidth = getStringWidth(text);
        // The entire string fits, just return it as-is
        if (textWidth <= maxWidth) return text;

        StringBuilder sb = new StringBuilder(128);

        final int indicatorWidth = getStringWidth(indicator);
        final int stringLen = text.length();
        int usedWidth = indicatorWidth;
        int usableWidth = maxWidth - indicatorWidth;
        int left = side.choose(0, textWidth - usableWidth);
        
        int index = 0;
        
        while (usedWidth < left && index < stringLen){
            usedWidth += getStringWidth(text.substring(index, index + 1));
            ++index;
        }
        
        usedWidth = 0;
        
        if (side == LeftRight.LEFT) sb.append(indicator);

        while (index < stringLen) {
            String chr = text.substring(index, index + 1);
            if ((usedWidth += getStringWidth(chr)) > maxWidth) break;
            sb.append(chr);
            ++index;
        }

        if (side == LeftRight.RIGHT) sb.append(indicator);
        
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
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning())
        {
            IntegratedServer server = mc.getServer();

            if (server != null)
            {
                // This used to be just MinecraftServer::getLevelName().
                // Getting the name would now require an @Accessor for MinecraftServer.field_23784
                String name = server.getSaveProperties().getLevelName();
                // this was breaking non-US Locale file names
                //return FileUtils.generateSimpleSafeFileName(name);
                return FileNameUtils.generateSafeFileName(name);
            }
        }
        else
        {
            if (mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().isRealm())
            {
                if (MaLiLibConfigs.Generic.REALMS_COMMON_CONFIG.getBooleanValue())
                {
                    return "realms";
                }
                else
                {
                    ClientPlayNetworkHandler handler = mc.getNetworkHandler();
                    ClientConnection connection = handler != null ? handler.getConnection() : null;

                    if (connection != null)
                    {
                        return "realms_" + stringifyAddress(connection.getAddress());
                    }
                }
            }

            ServerInfo server = mc.getCurrentServerEntry();

            if (server != null)
            {
                return server.address.replace(':', '_');
            }

            return "multiplayer_fallback";
        }

        return null;
    }

    /**
     * Returns a file name based on the current server or world name.
     * If <b>globalData</b> is false, the name will also include the current dimension ID.
     * @param globalData ()
     * @param prefix ()
     * @param suffix ()
     * @param defaultName the default file name, if getting a per-server/world name fails
     * @return ()
     */
    public static String getStorageFileName(boolean globalData, String prefix, String suffix, String defaultName)
    {
        String name = getWorldOrServerName();

        if (name != null)
        {
            if (globalData)
            {
                return prefix + name + suffix;
            }
            else
            {
                World world = MinecraftClient.getInstance().world;

                if (world != null)
                {
                    return prefix + name + "_dim_" + WorldUtils.getDimensionId(world) + suffix;
                }
            }
        }
        else
        {
            name = prefix + defaultName + suffix;
        }

        return FileNameUtils.generateSafeFileName(name) + suffix;
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
        return Arrays.asList(translated.split("\\\\n|\\n"));
    }

    public static void translateAndLineSplit(Consumer<String> lineConsumer, String translationKey, Object... args)
    {
        String translated = translate(translationKey, args);

        for (String line : translated.split("\\\\n|\\n"))
        {
            lineConsumer.accept(line);
        }
    }

    @Nullable
    public static String getTranslatedOrFallback(String key, @Nullable String fallback)
    {
        String translated = translate(key);

        if (key.equals(translated) == false)
        {
            return translated;
        }

        return fallback;
    }

    public static Text getTranslatedAsTextOrFallback(String key, @Nullable String fallback)
    {
        String result = getTranslatedOrFallback(key, fallback);

        if (result == null)
        {
            return Text.empty();
        }

        return Text.of(result);
    }

    // Some MCP vs. Yarn vs. MC versions compatibility/wrapper stuff below this

    /**
     * Just a wrapper around I18n, to reduce the number of changed lines between MCP/Yarn versions of mods
     * @param translationKey ()
     * @param args ()
     * @return ()
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

            /*
            if (MaLiLibConfigs.Generic.TRANSLATION_OVERRIDES.getBooleanValue())
            {
                String translation = Registry.TRANSLATION_OVERRIDE_MANAGER.getOverriddenTranslation(translationKey, args);

                if (translation != null)
                {
                    return translation;
                }
            }
             */

            return I18n.translate(translationKey, args);
        }
        catch (Exception e)
        {
            return translationKey;
        }
    }

    public static Text translateAsText(String translationKey, Object... args)
    {
        return Text.of(translate(translationKey, args));
    }

    public static MutableText translateable(String translationKey)
    {
        return Text.translatable(translationKey);
    }

    public static MutableText translateable(String translationKey, Object... args)
    {
        return Text.translatable(translationKey, args);
    }

    /**
     * Return if this translationKey has been found
     * @param translationKey (Key th check)
     * @return (True|False)
     */
    public static boolean hasTranslation(String translationKey)
    {
        return I18n.hasTranslation(translationKey);
    }

    /**
     * Return a Read Friendly String from translationPath
     * @param translationPath Raw translationPath
     * @return Read Friendly String
     */
    public static String prettifyRawTranslationPath(@NotNull String translationPath)
    {
        return Arrays.stream(translationPath.split("_")).map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()).collect(Collectors.joining(" "));
    }

    /**
     * Just a wrapper to get the font height from the Font/TextRenderer
     * @return ()
     */
    public static int getFontHeight()
    {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }

    public static int getStringWidth(String text)
    {
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }

    public static void drawString(int x, int y, int color, String text, DrawContext drawContext)
    {
        drawContext.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, false);
    }

    /**
     * Get a nicely formatted Duration string (ex: X hours, X minutes, X seconds)
     * @param durationMs (Duration in Milliseconds (1 second * 1000L or 1 tick * 50L))
     * @return (The formatted string)
     */
    public static String getDurationString(long durationMs)
    {
        return DurationFormat.PRETTY.format(durationMs);
    }

    /**
     * A copy of the Legacy Text.Deserializer that was removed from Vanilla.
     * We need this for backwards compatibility with things like Litematica and NBT tags.
     * @param oldText ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable String legacyTextDeserializer(MutableText oldText, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            JsonElement element = TextCodecs.CODEC.encodeStart(registry.getOps(JsonOps.INSTANCE), oldText).getPartialOrThrow(JsonParseException::new);
            return new GsonBuilder().disableHtmlEscaping().create().toJson(element);
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("legacyTextDeserializer: Failed to convert MutableText to JSON; {}", err.getLocalizedMessage());
            return null;
        }
    }

    /**
     * A copy of the Legacy Text.Serializer that was removed from Vanilla.
     * We need this for backwards compatibility with things like Litematica and NBT tags.
     * @param json ()
     * @param registry ()
     * @return ()
     */
    public static @Nullable MutableText legacyTextSerializer(String json, @Nonnull DynamicRegistryManager registry)
    {
        try
        {
            return (MutableText) TextCodecs.CODEC.parse(registry.getOps(JsonOps.INSTANCE), JsonParser.parseString(json)).getOrThrow(JsonParseException::new);
        }
        catch (Exception err)
        {
            MaLiLib.LOGGER.error("legacyTextSerializer: Failed to convert JSON to MutableText; {}", err.getLocalizedMessage());
            return null;
        }
    }
}
