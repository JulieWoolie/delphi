package net.arcadiusmc.delphiplugin.listeners;

import java.util.Optional;
import net.arcadiusmc.delphiplugin.DelphiPlugin;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.PlayerSession;
import net.arcadiusmc.dom.event.MouseButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

  private final DelphiPlugin plugin;

  public PlayerListener(DelphiPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    plugin.getSessions().endSession(player.getUniqueId());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action act = event.getAction();

    // Dumb but it stops actions we don't care about from affecting
    // page interaction
    switch (act) {
      case LEFT_CLICK_BLOCK:
      case LEFT_CLICK_AIR:
      case RIGHT_CLICK_AIR:
      case RIGHT_CLICK_BLOCK:
        break;

      default:
        return;
    }

    Optional<PlayerSession> opt = plugin.getSessions().getSession(player.getUniqueId());

    if (opt.isEmpty()) {
      return;
    }

    PlayerSession session = opt.get();
    PageView selected = session.getSelectedView();

    if (selected == null) {
      return;
    }

    MouseButton button = act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK
        ? MouseButton.LEFT
        : MouseButton.RIGHT;

    boolean shift = player.isSneaking();

    selected.onInteract(button, shift);
  }
}
