package com.juliewoolie.delphiplugin.gizmo;

import com.juliewoolie.delphiplugin.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.util.Transformation;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class GizmoHint {

  private TextDisplay display;

  private final DelphiGizmo gimbal;

  public GizmoHint(DelphiGizmo gimbal) {
    this.gimbal = gimbal;
  }

  Vector3d getHintPosition(GizmoPart part) {
    Vector3d p = new Vector3d();

    Vector3f off = part.offset;
    Vector3f size = part.size;

    Player player = gimbal.getPlayer();
    boolean below = (player.getY() + player.getEyeHeight()) < gimbal.position.y;
    if (below) {
      size.y = -size.y;
    }

    p.add(off.x, off.y, off.z);
    p.add(size.x * 0.5, size.y, size.z * 0.5);

    float dist = 0.1f;
    if (below) {
      p.y -= dist;
    } else {
      p.y += dist;
    }

    Transformation baseTransform = gimbal.getBaseTransform();

    baseTransform.getLeftRotation().transform(p);
    p.mul(baseTransform.getScale());
    baseTransform.getRightRotation().transform(p);

    p.add(baseTransform.getTranslation());
    p.add(gimbal.position);

    return p;
  }

  void setHint(Part part) {
    GizmoPart p = gimbal.getPart(part);
    Player player = gimbal.getPlayer();

    Vector3d spawnPoint = getHintPosition(p);
    Location location = new Location(gimbal.getWorld(), spawnPoint.x, spawnPoint.y, spawnPoint.z);

    if (display == null || display.isDead()) {
      display = gimbal.getWorld().spawn(location, TextDisplay.class);
    } else {
      display.teleport(location);
    }

    display.setAlignment(TextAlignment.CENTER);
    display.setShadowed(true);
    display.setSeeThrough(false);
    display.setBillboard(Billboard.CENTER);
    display.setPersistent(false);

    Transformation trans = display.getTransformation();
    trans.getScale().set(0.666666666666666666f);
    display.setTransformation(trans);

    Component hintText = TextUtil.translate(player, part.translationKey());
    display.text(hintText);
  }

  public void kill() {
    if (display == null) {
      return;
    }
    display.remove();
  }
}
