package net.arcadiusmc.delphiplugin;

import java.io.File;
import java.io.Reader;
import java.nio.file.Path;
import lombok.Getter;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphiplugin.command.Permissions;
import net.arcadiusmc.delphiplugin.listeners.PlayerListener;
import net.arcadiusmc.delphiplugin.listeners.PluginDisableListener;
import net.arcadiusmc.delphiplugin.resource.FontMetrics;
import net.arcadiusmc.delphiplugin.resource.PluginResources;
import net.arcadiusmc.hephaestus.Scripting;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
public class DelphiPlugin extends JavaPlugin {

  private ViewManager viewManager;
  private PluginResources pluginResources;
  private DelphiImpl manager;
  private FontMetrics metrics;

  @Override
  public void onEnable() {
    this.metrics = new FontMetrics(this);
    this.viewManager = new ViewManager(this);
    this.pluginResources = new PluginResources(this);
    this.manager = new DelphiImpl(this, pluginResources, viewManager);

    viewManager.startTicking();

    reloadConfig();
    registerEvents();
    Permissions.registerAll();
    Scripting.scriptingInit();

    printVersions();

    ServicesManager services = getServer().getServicesManager();
    services.register(Delphi.class, manager, this, ServicePriority.Highest);
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
