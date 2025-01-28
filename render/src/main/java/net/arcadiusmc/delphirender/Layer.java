package net.arcadiusmc.delphirender;

import static net.arcadiusmc.delphirender.content.TextContent.NIL_COLOR;

import java.util.Objects;
import net.arcadiusmc.delphidom.Rect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Layer {

  public Display entity;

  public final Vector2f size = new Vector2f(0);
  public final Rect borderSize = new Rect();
  public boolean alwaysSpawn;
  public Color color;

  public final Vector3f translate = new Vector3f(0);
  public final Vector3f rotatedTranslate = new Vector3f(0);
  public final Quaternionf leftRotation = new Quaternionf();
  public final Vector3f scale = new Vector3f(1);
  public final Quaternionf rightRotation = new Quaternionf();

  public Layer() {
    nullify();
  }

  public void nullify() {
    size.set(0);
    borderSize.set(0);

    translate.set(0);
    rotatedTranslate.set(0);
    leftRotation.identity();
    scale.set(1);
    rightRotation.identity();
  }

  public boolean isSpawned() {
    return entity != null && !entity.isDead();
  }

  public void killEntity() {
    if (entity == null) {
      return;
    }

    entity.remove();
    entity = null;
  }

  public boolean shouldSpawn() {
    return alwaysSpawn || borderSize.isNotZero();
  }

  public void updateEntity() {
    if (entity instanceof TextDisplay td) {
      td.setBackgroundColor(Objects.requireNonNullElse(color, NIL_COLOR));
    }
  }

  public void spawn(Location location, World w) {
    entity = w.spawn(location, TextDisplay.class);
    updateEntity();
  }

  public void updateTransform() {
    if (!isSpawned()) {
      return;
    }

    Transformation trans = entity.getTransformation();
    Vector3f sc = trans.getScale();
    Vector3f tr = trans.getTranslation();

    tr.x = rotatedTranslate.x;
    tr.y = rotatedTranslate.y;
    tr.z = rotatedTranslate.z;

    sc.x = scale.x;
    sc.y = scale.y;
    sc.z = scale.z;

    trans.getLeftRotation().set(leftRotation);
    trans.getRightRotation().set(rightRotation);

    entity.setTransformation(trans);
  }
}
