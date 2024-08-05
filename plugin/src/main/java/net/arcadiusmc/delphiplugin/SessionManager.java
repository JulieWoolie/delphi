package net.arcadiusmc.delphiplugin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.arcadiusmc.delphidom.Loggers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;

public class SessionManager {

  private static final Logger LOGGER = Loggers.getLogger();

  static final int FAILED_TO_REGISTER = -1;

  private BukkitTask task;
  private final DelphiPlugin plugin;

  private final Map<UUID, PlayerSession> sessionMap = new Object2ObjectOpenHashMap<>();

  public SessionManager(DelphiPlugin plugin) {
    this.plugin = plugin;
  }

  public void startTicking() {
    stopTicking();

    BukkitScheduler scheduler = Bukkit.getScheduler();
    task = scheduler.runTaskTimer(plugin, this::tick, 1, 1);

    if (task == null || task.getTaskId() == FAILED_TO_REGISTER) {
      LOGGER.error("Failed to schedule page view tick updates, pages will not function correctly.");
    }

  }

  public void stopTicking() {
    if (task == null || task.isCancelled()) {
      return;
    }

    task.cancel();
    task = null;
  }

  public Optional<PlayerSession> getSession(UUID playerId) {
    return Optional.ofNullable(sessionMap.get(playerId));
  }

  public PlayerSession getOrCreateSession(Player player) {
    return sessionMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSession(player));
  }

  private void tick() {
    for (PlayerSession value : sessionMap.values()) {
      try {
        value.tick();
      } catch (Exception t) {
        LOGGER.error("Failed to tick document view", t);
      }
    }
  }

  public void endSession(UUID playerId) {
    PlayerSession removed = sessionMap.remove(playerId);
    if (removed == null) {
      return;
    }

    for (PageView view : removed.getViews()) {
      view.onClose();
    }
  }
}
