package com.juliewoolie.delphiplugin.gimbal;

import com.juliewoolie.delphiplugin.math.RayScan;
import com.juliewoolie.delphirender.object.RenderObject;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.Intersectiond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class GizmoPart {

  private final DelphiGizmo gimbal;

  final Vector3f size = new Vector3f(1.0f);
  final Vector3f offset = new Vector3f(0.0f);

  private BlockDisplay entity;

  @Setter
  private BlockData blockState;

  private Color glowColor = null;

  public GizmoPart(DelphiGizmo gimbal) {
    this.gimbal = gimbal;
  }

  public void setBlockType(Material material) {
    setBlockState(material.createBlockData());
  }

  void moveTo(float x, float y, float z) {
    if (x == offset.x && y == offset.y && offset.z == z) {
      return;
    }

    offset.set(x, y, z);

    if (entity != null && !entity.isDead()) {
      updateTransform();
    }
  }

  Location getSpawnLocation() {
    Vector3d position = gimbal.position;
    return new Location(gimbal.getWorld(), position.x, position.y, position.z);
  }

  public void spawn() {
    Location location = getSpawnLocation();

    if (entity == null || entity.isDead()) {
      entity = gimbal.getWorld().spawn(location, BlockDisplay.class);
    } else {
      entity.teleport(location);
    }

    updateTransform();

    entity.setBlock(blockState);
    entity.setBrightness(RenderObject.BRIGHTNESS);
    entity.setPersistent(false);
    entity.setGlowColorOverride(glowColor);
  }

  void combineTrans(Transformation a, Transformation b, Transformation out) {
    a.getTranslation().add(b.getTranslation(), out.getTranslation());
    a.getLeftRotation().mul(b.getLeftRotation(), out.getLeftRotation());
    a.getScale().mul(b.getScale(), out.getScale());
    a.getRightRotation().mul(b.getRightRotation(), out.getRightRotation());
  }

  void updateTransform() {
    Transformation trans = RenderObject.newTransform();
    Transformation baseTransform = gimbal.baseTransform;

    Vector3f offset = trans.getTranslation();
    getTransformedOffset(offset);

    trans.getScale().set(this.size);

    combineTrans(trans, baseTransform, trans);

    entity.setTransformation(trans);
  }

  public void kill() {
    if (entity == null) {
      return;
    }

    entity.remove();
    entity = null;
  }

  void getTransformedOffset(Vector3f out) {
    Transformation baseTransform = gimbal.baseTransform;

    out.set(this.offset);

    baseTransform.getLeftRotation().transform(out);
    out.mul(baseTransform.getScale());
    baseTransform.getRightRotation().transform(out);

  }

  public double castRay(RayScan scan) {
    Vector3d min = new Vector3d();
    Vector3d max = new Vector3d();

    min.add(offset);

    max.set(min);
    max.add(size);

    Vector2d result = new Vector2d();

    int r = Intersectiond.intersectLineSegmentAab(
        scan.getOrigin(),
        scan.getEnd(),
        min, max,
        result
    );

    if (r == Intersectiond.OUTSIDE) {
      return -1;
    }

    return result.x;
  }

  public boolean isAlive() {
    return entity != null && !entity.isDead();
  }

  public void setGlowing(boolean glow) {
    if (!isAlive()) {
      return;
    }

    entity.setGlowing(glow);
  }

  public void setGlowColor(Color glowColor) {
    this.glowColor = glowColor;

    if (isAlive()) {
      entity.setGlowColorOverride(glowColor);
    }
  }
}
