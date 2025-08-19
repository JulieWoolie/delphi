package com.juliewoolie.delphiplugin;

import java.io.File;
import java.io.Reader;
import java.nio.file.Path;
import lombok.Getter;
import com.juliewoolie.delphi.Delphi;
import com.juliewoolie.delphiplugin.command.Permissions;
import com.juliewoolie.delphiplugin.listeners.PlayerListener;
import com.juliewoolie.delphiplugin.listeners.PluginDisableListener;
import com.juliewoolie.delphiplugin.resource.FontMetrics;
import com.juliewoolie.delphiplugin.resource.PluginResources;
import com.juliewoolie.hephaestus.Scripting;
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

  private Metrics bstats;

  @Override
  public void onEnable() {
    Path modulesDir = getDataPath().resolve("modules");

    this.metrics = new FontMetrics(this);
    this.viewManager = new ViewManager(this);
    this.pluginResources = new PluginResources(this, modulesDir);
    this.manager = new DelphiImpl(this, pluginResources, viewManager);

    viewManager.startTicking();

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
  }

  private void registerEvents() {
    PluginManager pl = getServer().getPluginManager();
    pl.registerEvents(new PlayerListener(this), this);
    pl.registerEvents(new PluginDisableListener(this), this);
  }

  @Override
  public void reloadConfig() {
    pluginResources.loadDefaultStyle();
    metrics.loadFonts();
  }

  @Override
  public void onDisable() {
    Scripting.shutdownScripting();

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
