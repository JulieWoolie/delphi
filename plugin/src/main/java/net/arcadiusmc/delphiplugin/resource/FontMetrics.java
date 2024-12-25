package net.arcadiusmc.delphiplugin.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2FloatMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Map;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphiplugin.DelphiPlugin;
import net.arcadiusmc.delphiplugin.render.FontMeasureCallback;
import net.arcadiusmc.delphiplugin.render.FontMeasureOutput;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.slf4j.Logger;

public class FontMetrics implements FontMeasureCallback {

  private static final Logger LOGGER = Loggers.getLogger();

  private final List<MeasuredFont> fonts = new ObjectArrayList<>();

  private final Path dataDirectoryPath;
  private Path jarResourcePath;

  public FontMetrics(DelphiPlugin plugin) {
    Path jarFile = plugin.getJarPath();
    Path dataDir = plugin.getDataPath();

    dataDirectoryPath = dataDir.resolve("data").resolve("fonts");
    jarResourcePath = null;

    initializeJarPath(jarFile);
  }

  private void initializeJarPath(Path jarFile) {
    FileSystemProvider provider = PluginResources.findZipProvider();
    if (provider == null) {
      LOGGER.error("Failed to get ZIP file system provider, cannot load font metrics");
      return;
    }

    FileSystem system;

    try {
      system = provider.newFileSystem(jarFile, Map.of());
    } catch (IOException exc) {
      LOGGER.error("Error loading plugin jar resource file system", exc);
      return;
    }

    jarResourcePath = system.getPath("data", "fonts");
  }

  public void loadFonts() {
    fonts.clear();

    if (jarResourcePath != null) {
      loadFromDirectory(jarResourcePath, true);
    }

    loadFromDirectory(dataDirectoryPath, false);
    fonts.sort(FontComparator.COMPARATOR);
  }

  private void loadFromDirectory(Path directory, boolean resource) {
    if (!Files.isDirectory(directory)) {
      if (!resource) {
        return;
      }

      LOGGER.error("Cannot load fonts from {}, is not a directory", directory);
      return;
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, JsonPathFilter.FILTER)) {
      for (Path path : stream) {
        loadFromFile(path, resource);
      }
    } catch (IOException exception) {
      LOGGER.error("Error loading font data files from {}", directory, exception);
    }
  }

  private void loadFromFile(Path path, boolean resource) {
    JsonElement element;

    try {
      BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
      element = JsonParser.parseReader(reader);
    } catch (IOException | JsonIOException exception) {
      LOGGER.error("IO error while reading font file {}", path, exception);
      return;
    } catch (JsonSyntaxException exception) {
      LOGGER.error("JSON syntax error while reading font file {}", path, exception);
      return;
    }

    MeasuredFont.CODEC.parse(JsonOps.INSTANCE, element)
        .mapError(s -> "Error loading font file " + path + ": " + s)
        .resultOrPartial(LOGGER::error)
        .ifPresent(font -> {
          font.setResourceLoaded(resource);
          fonts.add(font);
        });
  }

  /* --------------------------- Font measuring ---------------------------- */

  public boolean measureNextChar(String content, Style style, int start, FontMeasureOutput out) {
    final String sub = content.substring(start);
    boolean bold = style.decoration(TextDecoration.BOLD) == State.TRUE;
    Key fontKey = style.font();

    if (fontKey != null && measureWithMatchingKey(sub, fontKey, out, bold)) {
      return true;
    }

    return measureWithMatchingKey(sub, MeasuredFont.DEFAULT_FONT_ID, out, bold);
  }

  private boolean measureWithMatchingKey(String content, Key fontKey, FontMeasureOutput out, boolean bold) {
    for (MeasuredFont font : fonts) {
      if (!font.getFontId().equals(fontKey)) {
        continue;
      }

      if (!measureNextWith(font, content, out, bold)) {
        continue;
      }

      return true;
    }

    return false;
  }

  private boolean measureNextWith(
      MeasuredFont font,
      String content,
      FontMeasureOutput out,
      boolean bold
  ) {
    for (Entry<String> stringEntry : font.getSizeMap().object2FloatEntrySet()) {
      String token = stringEntry.getKey();

      if (!content.startsWith(token)) {
        continue;
      }

      out.consumedChars = token.length();
      out.width = stringEntry.getFloatValue();
      out.height = font.getHeight();
      out.descenderHeight = font.getDescenderHeight();

      if (bold) {
        out.width += font.getBoldModifier();
      }

      return true;
    }

    return false;
  }
}
