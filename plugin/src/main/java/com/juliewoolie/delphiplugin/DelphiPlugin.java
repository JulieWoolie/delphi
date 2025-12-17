package com.juliewoolie.delphiplugin;

import com.juliewoolie.delphi.Delphi;
import com.juliewoolie.delphiplugin.PluginUpdater.PluginVersion;
import com.juliewoolie.delphiplugin.command.Permissions;
import com.juliewoolie.delphiplugin.gimbal.GizmoManager;
import com.juliewoolie.delphiplugin.listeners.ChunkListener;
import com.juliewoolie.delphiplugin.listeners.PlayerListener;
import com.juliewoolie.delphiplugin.listeners.PluginDisableListener;
import com.juliewoolie.delphiplugin.resource.FontMetrics;
import com.juliewoolie.delphiplugin.resource.PluginResources;
import com.juliewoolie.hephaestus.Scripting;
import java.io.File;
import java.io.Reader;
import java.nio.file.Path;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
public class DelphiPlugin extends JavaPlugin {

  static final int BSTATS_PLUGIN_ID = 26906;

  private ViewManager viewManager;
  private PluginResources pluginResources;
  private DelphiImpl manager;
  private FontMetrics metrics;
  private DelphiConfig delphiConfig;
  private GizmoManager gizmoManager;

  private PluginVersion foundLatest;

  private Metrics bstats;

  @Override
  public void onEnable() {
    Path modulesDir = getDataPath().resolve("modules");

    this.delphiConfig = new DelphiConfig();
    this.metrics = new FontMetrics(this);
    this.viewManager = new ViewManager(this);
    this.pluginResources = new PluginResources(this, modulesDir);
    this.manager = new DelphiImpl(this, pluginResources, viewManager);
    this.gizmoManager = new GizmoManager(this);

    viewManager.startTicking();
    gizmoManager.startTicking();

    saveDefaultConfig();
    reloadConfig();
    registerEvents();
    Permissions.registerAll();
    Scripting.scriptingInit();

    printVersions();

    ServicesManager services = getServer().getServicesManager();
    services.register(Delphi.class, manager, this, ServicePriority.Highest);

    // Only activate on non-debug servers
    if (!getSLF4JLogger().isDebugEnabled()) {
      bstats = new Metrics(this, BSTATS_PLUGIN_ID);
    }

    foundLatest = PluginUpdater.checkForUpdates(getPluginMeta().getVersion());

    if (delphiConfig.autoUpdatePlugin && foundLatest != null) {
      PluginUpdater.downloadUpdate(foundLatest);
    } else if (foundLatest != null) {
      getSLF4JLogger().warn("New plugin version available! Version: {}, download URL: {}",
          foundLatest.version(), foundLatest.downloadUrl()
      );
    }
  }

  private void registerEvents() {
    PluginManager pl = getServer().getPluginManager();
    pl.registerEvents(new PlayerListener(this), this);
    pl.registerEvents(new PluginDisableListener(this), this);
    pl.registerEvents(new ChunkListener(this),  this);
  }

  @Override
  public void reloadConfig() {
    super.reloadConfig();

    pluginResources.loadDefaultStyle();
    metrics.loadFonts();

    delphiConfig.load(getConfig());
  }

  @Override
  public void onDisable() {
    Scripting.shutdownScripting();

    if (gizmoManager != null) {
      gizmoManager.stopTicking();
    }

    if (bstats != null) {
      bstats.shutdown();
    }
  }

  private void printVersions() {
    Reader resource = getTextResource("versions.yml");

    if (resource == null) {
      return;
    }

    YamlConfiguration config = YamlConfiguration.loadConfiguration(resource);

    String apiVersion = config.getString("api", "UNKNOWN");
    String chimeraVersion = config.getString("chimera", "UNKNOWN");
    String domVersion = config.getString("dom", "UNKNOWN");
    String jsVersion = config.getString("js", "UNKNOWN");

    getSLF4JLogger().info(
        "Running delphi plugin: "
            + "version={}, "
            + "dom-impl-version={}, "
            + "api-version={}, "
            + "scss-engine-version={}, "
            + "js-engine-version={}",

        getPluginMeta().getVersion(),
        domVersion,
        apiVersion,
        chimeraVersion,
        jsVersion
    );
  }

  @Override
  public @NotNull File getFile() {
    return super.getFile();
  }

  public Path getJarPath() {
    return getFile().toPath();
  }

  public Path getInternalDataPath() {
    return getDataPath().resolve("data");
  }
}
