package net.arcadiusmc.delphiplugin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import net.arcadiusmc.delphi.PlayerSet;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphiplugin.math.RayScan;
import net.arcadiusmc.delphiplugin.math.Screen;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;

@Getter
public class ViewManager {

  private static final Logger LOGGER = Loggers.getLogger();

  static final int FAILED_TO_REGISTER = -1;
  static final String AUTO_INST_NAME = "delphi-page-";

  private final List<PageView> openViews = new ObjectArrayList<>(20);
  private final List<PageView> allPlayerViews = new ObjectArrayList<>(20);
  private final Map<String, PageView> byInstanceName = new Object2ObjectOpenHashMap<>(20);
  private final Map<Player, ViewEntry> byPlayer = new Object2ObjectOpenHashMap<>(20);

  private BukkitTask task;

  private final Plugin plugin;

  //
  // This may be bad practice but these variables are modified and changed each
  // tick per each entry
  //
  // They're used for page ray cast calculations to determine which page a player
  // is aiming at.
  //
  private final Vector3f targetPos = new Vector3f();
  private final Vector2f screenPos = new Vector2f();
  private final Vector2f cursorDif = new Vector2f();
  private float screenDist;

  public ViewManager(Plugin plugin) {
    this.plugin = plugin;
  }

  public String generateInstanceName() {
    return AUTO_INST_NAME + byInstanceName.size();
  }

  public void addView(PageView view) {
    openViews.add(view);
    byInstanceName.put(view.getInstanceName(), view);

    PlayerSet players = view.getPlayers();

    if (players.isServerPlayerSet()) {
      allPlayerViews.add(view);
      return;
    }

    for (Player player : players) {
      playerAdded(view, player);
    }
  }

  public void removeView(PageView view) {
    openViews.remove(view);
    byInstanceName.remove(view.getInstanceName());

    PlayerSet players = view.getPlayers();

    if (players.isServerPlayerSet()) {
      allPlayerViews.remove(view);
      return;
    }

    for (Player player : players) {
      playerRemoved(view, player);
    }
  }

  public void playerAdded(PageView view, Player player) {
    ViewEntry entry = byPlayer.computeIfAbsent(player, player1 -> new ViewEntry());
    entry.views.add(view);
  }

  public void playerRemoved(PageView view, Player player) {
    ViewEntry entry = byPlayer.get(player);
    if (entry == null) {
      return;
    }

    entry.views.remove(view);

    if (Objects.equals(view, entry.selected)) {
      entry.selected = null;
    }
  }

  public void startTicking() {
    stopTicking();

    BukkitScheduler scheduler = Bukkit.getScheduler();
    task = scheduler.runTaskTimer(plugin, this::tick, 1, 1);

    if (task.getTaskId() == FAILED_TO_REGISTER) {
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

  private void tick() {
    for (PageView openView : openViews) {
      openView.tick();
    }

    tickTargeting();
  }

  private void tickTargeting() {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      ViewEntry entry = byPlayer.computeIfAbsent(onlinePlayer, player -> new ViewEntry());
      update(onlinePlayer, entry);
    }
  }

  private void update(Player player, ViewEntry entry) {
    PageView selectedPre = entry.selected;
    PageView targeted = findTargeted(player, entry);

    if (targeted == null) {
      if (selectedPre == null) {
        return;
      }

      selectedPre.onUnselect();
      entry.selected = null;

      return;
    }

    if (Objects.equals(selectedPre, targeted)) {
      Vector2f currentScreenPos = selectedPre.cursorScreen;
      currentScreenPos.sub(screenPos, cursorDif);

      if (isZero(cursorDif)) {
        return;
      }

      targeted.cursorMoveTo(player, screenPos, targetPos);
      return;
    }

    if (selectedPre != null) {
      selectedPre.onUnselect();
    }

    targeted.onSelect(player, screenPos, targetPos);
    entry.selected = targeted;
  }

  private boolean isZero(Vector2f v) {
    return v.x == 0.0f && v.y == 0.0f;
  }

  private PageView findTargeted(Player player, ViewEntry entry) {
    PageView ownTargeted = rayCastPage(player, entry.views);
    float ownDist = screenDist;

    // Store the current screen and target positions, because the rayCastPage
    // call after these will change the screenPos and targetPos values, but
    // we'll need the original values if we still return ownTargeted, so store
    // them.
    Vector2f screenPosStore = new Vector2f(screenPos);
    Vector3f targetPosStore = new Vector3f(targetPos);

    PageView allTargeted = rayCastPage(player, allPlayerViews);
    float allDist = screenDist;

    if (allTargeted == null || ownDist < allDist) {
      screenPos.set(screenPosStore);
      targetPos.set(targetPosStore);

      return ownTargeted;
    }

    return allTargeted;
  }

  private PageView rayCastPage(Player player, List<PageView> views) {
    if (views.isEmpty()) {
      targetPos.set(0);
      screenPos.set(0);
      screenDist = Float.MAX_VALUE;

      return null;
    }

    World world = player.getWorld();
    RayScan scan = RayScan.ofPlayer(player);

    PageView closest = null;
    float closestDistSq = Float.MAX_VALUE;

    Vector2f closestScreenHit = new Vector2f(0);
    Vector3f closestHit = new Vector3f(0);

    Vector3f targetPos = new Vector3f();
    Vector2f screenPos = new Vector2f();

    for (int i = 0; i < views.size(); i++) {
      PageView view = views.get(i);

      if (view.getWorld() == null || !Objects.equals(view.getWorld(), world)) {
        continue;
      }

      Screen bounds = view.getScreen();

      if (bounds == null) {
        continue;
      }
      if (!bounds.castRay(scan, targetPos, screenPos)) {
        continue;
      }

      float distSq = targetPos.distanceSquared(scan.getOrigin());
      if (distSq >= scan.getMaxLengthSq() || distSq >= closestDistSq) {
        continue;
      }

      closestDistSq = distSq;
      closest = view;

      closestHit.set(targetPos);
      bounds.screenspaceToScreen(screenPos, closestScreenHit);
    }

    this.screenPos.set(closestScreenHit);
    this.targetPos.set(closestHit);
    this.screenDist = closestDistSq;

    return closest;
  }

  public static class ViewEntry {
    public final List<PageView> views = new ObjectArrayList<>();
    public PageView selected;
  }
}
