package net.arcadiusmc.delphiplugin;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerSession {

  private final Player player;
  private final List<PageView> views = new ArrayList<>();

  public PlayerSession(Player player) {
    this.player = player;
  }

  public void addView(PageView view) {
    views.add(view);
    view.setSession(this);
  }

  public void tick() {

  }

  public void removeView(PageView view) {
    if (!views.remove(view)) {
      return;
    }

    view.setSession(null);
    view.kill();
  }
}
