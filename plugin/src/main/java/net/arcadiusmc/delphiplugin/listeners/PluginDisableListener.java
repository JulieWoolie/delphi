package net.arcadiusmc.delphiplugin.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.arcadiusmc.delphiplugin.DelphiPlugin;
import net.arcadiusmc.delphiplugin.resource.Modules;
import net.arcadiusmc.delphiplugin.resource.Modules.RegisteredModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public class PluginDisableListener implements Listener {

  private final DelphiPlugin plugin;

  public PluginDisableListener(DelphiPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(ignoreCancelled = true)
  public void onPluginDisable(PluginDisableEvent event) {
    Plugin eventPlugin = event.getPlugin();
    if (Objects.equals(plugin, eventPlugin)) {
      return;
    }

    List<String> removed = new ArrayList<>();
    Modules modules = plugin.getModules();

    for (RegisteredModule value : modules.getRegistered().values()) {
      if (!Objects.equals(eventPlugin, value.plugin())) {
        continue;
      }

      removed.add(value.name());
    }

    for (String moduleName : removed) {
      modules.unregisterModule(moduleName);
    }
  }
}
