package net.arcadiusmc.delphiplugin;

import java.io.Reader;
import lombok.Getter;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphiplugin.command.Permissions;
import net.arcadiusmc.delphiplugin.listeners.PlayerListener;
import net.arcadiusmc.delphiplugin.listeners.PluginDisableListener;
import net.arcadiusmc.delphiplugin.resource.Modules;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DelphiPlugin extends JavaPlugin {

  private SessionManager sessions;
  private Modules modules;
  private PageManager manager;

  @Override
  public void onEnable() {
    this.sessions = new SessionManager(this);
    this.modules = new Modules(getDataPath().resolve("modules"));
    this.manager = new PageManager(this, modules, sessions);

    sessions.startTicking();

    reloadConfig();
    registerEvents();
    Permissions.registerAll();

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
    modules.loadDefaultStyle();
  }

  @Override
  public void onDisable() {

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

    getSLF4JLogger().info(
        "Running delphi plugin: version={}, dom-impl-version={} api-version={}, scss-engine-version={}",
        getPluginMeta().getVersion(),
        domVersion,
        apiVersion,
        chimeraVersion
    );
  }
}
