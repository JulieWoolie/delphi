package com.juliewoolie.delphiplugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.juliewoolie.delphidom.Loggers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PluginUpdater {

  private static final String MODRINTH_SLUG = "RCMZuaza";

  private static final String MODRINTH_VERSION_URL
      = "https://api.modrinth.com/v2/project/" + MODRINTH_SLUG + "/version";

  private static final Logger LOGGER = Loggers.getLogger();

  public static final int HTTP_OK = 200;

  static void downloadUpdate(PluginVersion version) {
    Path pluginsDirectory = Bukkit.getServer().getPluginsFolder().toPath();
    Path updateDir = pluginsDirectory.resolve("update");

    if (!Files.isDirectory(updateDir)) {
      try {
        Files.createDirectories(updateDir);
      } catch (IOException io) {
        LOGGER.error("Error creating {} directory", updateDir, io);
      }
    }

    Path updatedJar = updateDir.resolve(version.filename);

    URL url;
    try {
      url = URI.create(version.downloadUrl).toURL();
    } catch (MalformedURLException exc) {
      LOGGER.error("Invalid Download URL: {}", version.downloadUrl, exc);
      return;
    }

    HttpURLConnection con;
    try {
      con = openGetConnection(url);
    } catch (IOException e) {
      LOGGER.error("Error opening connection to plugin download URL", e);
      return;
    }

    try {
      int status = con.getResponseCode();
      if (status != HTTP_OK) {
        return;
      }

      try (
          InputStream in = con.getInputStream();
          OutputStream os = Files.newOutputStream(updatedJar)
      ) {
        in.transferTo(os);
      }
    } catch (IOException exc) {
      LOGGER.error("Error downloading updated plugin jar", exc);
    } finally {
      con.disconnect();
    }
  }

  static PluginVersion checkForUpdates(String pluginVersion) {
    URL url;

    try {
      url = URI.create(MODRINTH_VERSION_URL).toURL();
    } catch (MalformedURLException e) {
      LOGGER.error("Error creating Modrinth URL: {}", MODRINTH_VERSION_URL, e);
      return null;
    }

    HttpURLConnection connection;

    try {
      connection = openGetConnection(url);
    } catch (IOException e) {
      LOGGER.error("Error opening connection to {}:", MODRINTH_VERSION_URL, e);
      return null;
    }

    List<PluginVersion> versions;
    try {
      versions = requestVersions(connection);
    } catch (IOException exc) {
      LOGGER.error("Error attempting to get plugin versions from Modrinth:", exc);
      return null;
    }

    if (versions == null || versions.isEmpty()) {
      LOGGER.error("No plugin versions found, cannot check for plugin updates");
      return null;
    }

    PluginVersion first = versions.getFirst();
    String currentVersion = pluginVersion;
    if (pluginVersion.contains("-")) {
      currentVersion = pluginVersion.substring(pluginVersion.indexOf('-')+1);
    }

    int cmp = SemanticVersions.compareVersions(currentVersion, first.version);
    if (cmp >= 0) {
      return null;
    }

    return first;
  }

  private static HttpURLConnection openGetConnection(URL url) throws IOException {
    HttpURLConnection con = (HttpURLConnection) url.openConnection();

    con.setRequestMethod("GET");

    con.setConnectTimeout(5000);
    con.setReadTimeout(5000);
    con.setInstanceFollowRedirects(true);

    return con;
  }

  private static List<PluginVersion> requestVersions(HttpURLConnection connection)
      throws IOException
  {
    int status = connection.getResponseCode();
    if (status != HTTP_OK) {
      LOGGER.error("HTTP Error while checking for plugin updates: {}", status);
      return null;
    }

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
    );

    JsonArray element = JsonParser.parseReader(reader).getAsJsonArray();

    reader.close();
    connection.disconnect();

    List<PluginVersion> versions = parseVersions(element);
    String mcVersion = Bukkit.getMinecraftVersion();

    versions.removeIf(v -> {
      boolean contains = v.gameVersions.contains(mcVersion);
      return !contains;
    });
    versions.sort(Comparator.naturalOrder());

    return versions;
  }

  private static List<PluginVersion> parseVersions(JsonArray array) {
    List<PluginVersion> versions = new ArrayList<>(array.size());
    for (int i = 0; i < array.size(); i++) {
      JsonObject obj = array.get(i).getAsJsonObject();

      JsonObject file = obj.getAsJsonArray("files").get(0).getAsJsonObject();
      String publishedDate = obj.get("date_published").getAsString();

      ZonedDateTime dt = ZonedDateTime.parse(publishedDate);
      long publishedDateTs = dt.toInstant().toEpochMilli();

      PluginVersion version = new PluginVersion(
          obj.get("version_number").getAsString(),
          StreamSupport.stream(obj.get("game_versions").getAsJsonArray().spliterator(), false)
              .map(JsonElement::getAsString)
              .collect(ObjectOpenHashSet.toSet()),
          file.get("url").getAsString(),
          file.get("filename").getAsString(),
          publishedDateTs
      );

      versions.addLast(version);
    }

    return versions;
  }

  public record PluginVersion(
      String version,
      Set<String> gameVersions,
      String downloadUrl,
      String filename,
      long publishDate
  ) implements Comparable<PluginVersion> {

    @Override
    public int compareTo(@NotNull PluginUpdater.PluginVersion o) {
      return Long.compare(o.publishDate, publishDate);
    }
  }
}
