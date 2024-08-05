package net.arcadiusmc.delphiplugin.render;

import net.arcadiusmc.delphidom.Rect;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Layer {

  public final RenderLayer layer;

  public Display entity;

  public final Vector2f size = new Vector2f();
  public final Rect borderSize = new Rect();

  public float depth = 0;

  public final Vector3f translate = new Vector3f();
  public final Vector3f rotatedTranslate = new Vector3f();
  public final Quaternionf leftRotation = new Quaternionf();
  public final Vector3f scale = new Vector3f();
  public final Quaternionf rightRotation = new Quaternionf();

  public Layer(RenderLayer layer) {
    this.layer = layer;
  }

  public void nullify() {
    size.set(0);
    borderSize.set(0);

    depth = 0;

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

  public void updateTransform() {
    if (!isSpawned()) {
      return;
    }

    Transformation trans = entity.getTransformation();
    Vector3f sc = trans.getScale();
    Vector3f tr = trans.getTranslation();

    // I assure you, dear maintainer, the axes on this translate
    // being screwed are completely vital to this system's
    // continued functioning.
    //
    // Honestly, though, I don't know why this is like this, but it
    // ensures that the Z axes acts like a depth value while X and Y
    // are screen translation values.
    tr.x = rotatedTranslate.z;
    tr.y = rotatedTranslate.y;
    tr.z = rotatedTranslate.x;

    sc.x = scale.x;
    sc.y = scale.y;
    sc.z = scale.z;

    trans.getLeftRotation().set(leftRotation);
    trans.getRightRotation().set(rightRotation);

    entity.setTransformation(trans);
  }
}
