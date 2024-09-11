package net.arcadiusmc.delphiplugin;

import java.util.List;
import lombok.Getter;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphi.resource.JarResourceModule;
import net.arcadiusmc.delphiplugin.command.Permissions;
import net.arcadiusmc.delphiplugin.listeners.PlayerListener;
import net.arcadiusmc.delphiplugin.listeners.PluginDisableListener;
import net.arcadiusmc.delphiplugin.resource.Modules;
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
    this.manager = new PageManager(modules, sessions);

    if (getSLF4JLogger().isDebugEnabled()) {
      JarResourceModule jarResource = new JarResourceModule(getClassLoader(), "modules/test");
      jarResource.setFilePaths(List.of("index.xml", "item.json", "style.scss"));
      modules.registerModule("resource-test", jarResource);
    }

    sessions.startTicking();

    reloadConfig();
    registerEvents();
    Permissions.registerAll();

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
}
