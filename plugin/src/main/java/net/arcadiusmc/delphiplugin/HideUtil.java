package net.arcadiusmc.delphiplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class HideUtil {
  private HideUtil() {}

  static DelphiPlugin getPlugin() {
    return JavaPlugin.getPlugin(DelphiPlugin.class);
  }

  public static void hide(Entity entity) {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      onlinePlayer.hideEntity(getPlugin(), entity);
    }
  }

  public static void unhide(Entity entity) {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      onlinePlayer.showEntity(getPlugin(), entity);
    }
  }
}
