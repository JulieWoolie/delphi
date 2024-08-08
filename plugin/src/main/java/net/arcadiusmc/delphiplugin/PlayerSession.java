package net.arcadiusmc.delphiplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import net.arcadiusmc.delphiplugin.math.RayScan;
import net.arcadiusmc.delphiplugin.math.Screen;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class PlayerSession {

  private final Player player;
  private final List<PageView> views = new ArrayList<>();

  private PageView selectedView;
  private PageView targetedView;

  private final Vector3f targetPos = new Vector3f();
  private final Vector2f screenPos = new Vector2f();

  public PlayerSession(Player player) {
    this.player = player;
  }

  public void addView(PageView view) {
    views.add(view);
    view.setSession(this);
  }

  public void closeView(PageView view) {
    if (!views.remove(view)) {
      return;
    }

    view.setSession(null);
    view.onClose();
  }

  public void tick() {
    triggerTicking();
    recalculateTarget();
    switchSelection();
  }

  private void triggerTicking() {
    for (PageView view : views) {
      view.tick();
    }
  }

  private void recalculateTarget() {
    if (views.isEmpty()) {
      if (targetedView == null) {
        return;
      }

      targetedView = null;
      targetPos.set(0, 0, 0);
      screenPos.set(0, 0);

      return;
    }

    World world = player.getWorld();
    RayScan scan = RayScan.ofPlayer(player);

    for (PageView view : views) {
      if (view.getWorld() == null || !Objects.equals(view.getWorld(), world)) {
        continue;
      }

      Screen bounds = view.getScreen();

      if (bounds == null) {
        continue;
      }

      boolean wasHit = bounds.castRay(scan, targetPos, screenPos);

      if (!wasHit) {
        continue;
      }
      if (targetPos.distanceSquared(scan.getOrigin()) >= scan.getMaxLengthSq()) {
        continue;
      }

      targetedView = view;
      bounds.screenspaceToScreen(screenPos, screenPos);

      return;
    }

    targetedView = null;
    targetPos.set(0, 0, 0);
    screenPos.set(0, 0);
  }


  private void switchSelection() {
    if (targetedView == null) {
      if (selectedView == null) {
        return;
      }

      selectedView.onUnselect();
      selectedView = null;

      return;
    }

    if (Objects.equals(targetedView, selectedView)) {
      targetedView.cursorMoveTo(screenPos, targetPos);
      return;
    }

    if (selectedView != null) {
      selectedView.onUnselect();
    }

    targetedView.onSelect(screenPos, targetPos);
    selectedView = targetedView;
  }
}
