package net.arcadiusmc.delphiplugin;

import lombok.Getter;
import net.arcadiusmc.delphiplugin.listeners.PlayerListener;
import net.arcadiusmc.delphiplugin.resource.Modules;
import org.bukkit.plugin.PluginManager;
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

    sessions.startTicking();

    reloadConfig();
    registerEvents();
  }

  private void registerEvents() {
    PluginManager pl = getServer().getPluginManager();
    pl.registerEvents(new PlayerListener(this), this);
  }

  @Override
  public void reloadConfig() {
    modules.loadDefaultStyle();
  }

  @Override
  public void onDisable() {

  }
}
