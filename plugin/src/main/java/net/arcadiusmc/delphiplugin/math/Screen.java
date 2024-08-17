package net.arcadiusmc.delphiplugin.math;

import org.bukkit.util.Transformation;
import org.joml.Intersectionf;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Screen implements net.arcadiusmc.delphi.Screen {

  static final float EPSILON = 0.0000001f;

  final Vector2f dimensions = new Vector2f(0);
  final Vector3f center = new Vector3f(0);

  // The actual dimensions of the screen in world space
  final Vector2f worldDimensions = new Vector2f(0);

  // Scale of the screen relative to the dimensions field
  public final Vector2f screenScale = new Vector2f(1);

  // Screen plane normal
  final Vector3f normal = new Vector3f(0, 0, 1);

  // Points of the screen
  final Vector3f loRight = new Vector3f(0);
  final Vector3f hiRight = new Vector3f(0);
  final Vector3f loLeft = new Vector3f(0);
  final Vector3f hiLeft = new Vector3f(0);

  // Transformations
  public final Vector3f scale = new Vector3f(1);
  public final Quaternionf leftRotation = new Quaternionf();
  public final Quaternionf rightRotation = new Quaternionf();

  public static void lookInDirection(Quaternionf lrot, Vector3f dir) {
    Vector3f globalUp = new Vector3f(0, -1, 0);
    Vector3f right = new Vector3f();
    Vector3f up = new Vector3f();

    dir.cross(globalUp, right);
    right.normalize();
    dir.cross(right, up);
    up.normalize();

    lrot.lookAlong(dir, up);
    lrot.invert();
  }

  /* --------------------------- mutation ---------------------------- */

  public void apply(Transformation trans) {
    translate(trans.getTranslation());
    multiply(trans.getScale(), trans.getLeftRotation(), trans.getRightRotation());
  }

  public void translate(Vector3f offset) {
    center.add(offset);
  }

  public void multiply(Vector3f scale, Quaternionf lrot, Quaternionf rrot) {
    this.scale.mul(scale);
    this.leftRotation.mul(lrot);
    this.rightRotation.mul(rrot);

    recalculate();
  }

  public void setCenter(Vector3f center) {
    this.center.set(center);
    recalculate();
  }

  public void setDimensions(float width, float height) {
    dimensions.x = Math.abs(width);
    dimensions.y = Math.abs(height);
    recalculate();
  }

  public void set(Vector3f center, float w, float h) {
    this.center.set(center);
    this.dimensions.set(w, h).absolute();
    recalculate();
  }

  public void transformPoint(Vector3f point) {
    leftRotation.transform(point);
    point.mul(scale);
    rightRotation.transform(point);
  }

  public void recalculate() {
    normal.set(0, 0, 1);
    transformPoint(normal);
    normal.normalize();

    findPoints();

    Vector3f lrDif = new Vector3f(); // Left-Right dif
    Vector3f udDif = new Vector3f(); // Up-Down dif

    loRight.sub(hiRight, udDif);
    loRight.sub(loLeft, lrDif);

    worldDimensions.x = lrDif.length();
    worldDimensions.y = udDif.length();

    worldDimensions.div(dimensions, screenScale);
  }

  void findPoints() {
    Vector3f up = new Vector3f(0, 1, 0);

    // Transform so it's screen's up direction
    transformPoint(up);
    up.normalize();

    Vector3f right = new Vector3f();
    normal.cross(up, right);

    right.mul(dimensions.x * 0.5f);
    up.mul(dimensions.y * 0.5f);

    right.mul(scale);
    up.mul(scale);

    // This may seem to be the opposite of what it should be, left == center + right (???)
    // But this is because the left and right are from the viewer's perspective, looking at
    // the screen, not from the screen looking at the player's perspective, so left and
    // right are flipped.
    loLeft.set(center).add(right).sub(up);
    loRight.set(center).sub(right).sub(up);
    hiLeft.set(center).add(right).add(up);
    hiRight.set(center).sub(right).add(up);
  }

  /* --------------------------- coordinate space conversion ---------------------------- */

  public void screenToWorld(Vector2f screenPoint, Vector3f out) {
    Vector2f in = new Vector2f();
    screenToScreenspace(screenPoint, in);
    screenspaceToWorld(in, out);
  }

  public void screenToScreenspace(Vector2f in, Vector2f out) {
    out.set(in).div(dimensions.x, dimensions.y);
  }

  public void screenspaceToScreen(Vector2f in, Vector2f out) {
    out.set(in).mul(dimensions.x, dimensions.y);
  }

  public void screenspaceToWorld(Vector2f screenPoint, Vector3f out) {
    Vector3f height = new Vector3f(hiLeft).sub(loLeft);
    Vector3f width = new Vector3f(loRight).sub(loLeft);

    height.mul(screenPoint.y);
    width.mul(screenPoint.x);

    out.set(loLeft);
    out.add(width);
    out.add(height);
  }

  /* --------------------------- ray casting ---------------------------- */

  public boolean castRay(RayScan scan, Vector3f out, Vector2f screenOut) {
    if (!planeIntersect(scan, out)) {
      return false;
    }

    screenHitPoint(out, screenOut);

    return (screenOut.x >= 0 && screenOut.x <= 1)
        && (screenOut.y >= 0 && screenOut.y <= 1);
  }

  public void screenHitPoint(Vector3f hitPoint, Vector2f out) {
    Vector3f height = new Vector3f(hiLeft).sub(loLeft);
    Vector3f width = new Vector3f(loRight).sub(loLeft);

    Vector3f relativePoint = new Vector3f(hitPoint).sub(loLeft);

    float x = relativePoint.dot(width) / width.lengthSquared();
    float y = relativePoint.dot(height) / height.lengthSquared();

    out.set(x, y);
  }

  public boolean planeIntersect(RayScan scan, Vector3f out) {
    float t = Intersectionf.intersectRayPlane(
        scan.getOrigin(),
        scan.getDirection(),
        center,
        normal,
        EPSILON
    );

    if (t < 0) {
      return false;
    }

    scan.getDirection().mul(t, out);
    out.add(scan.getOrigin());

    return true;
  }

  /* --------------------------- API impl ---------------------------- */

  @Override
  public float getWidth() {
    return dimensions.x;
  }

  @Override
  public float getHeight() {
    return dimensions.y;
  }

  @Override
  public Vector3f normal() {
    return new Vector3f(normal);
  }

  @Override
  public Vector3f center() {
    return new Vector3f(center);
  }

  @Override
  public Vector2f getDimensions() {
    return new Vector2f(dimensions);
  }

  public void getDimensions(Vector2f out) {
    out.set(dimensions);
  }

  @Override
  public Vector3f getLowerLeft() {
    return new Vector3f(loLeft);
  }

  @Override
  public Vector3f getLowerRight() {
    return new Vector3f(loRight);
  }

  @Override
  public Vector3f getUpperLeft() {
    return new Vector3f(hiLeft);
  }

  @Override
  public Vector3f getUpperRight() {
    return new Vector3f(hiRight);
  }

  /* --------------------------- to string ---------------------------- */

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
        .append(getClass().getSimpleName())
        .append('[');

    appendInfo(builder, -1);

    builder.append(']');
    return builder.toString();
  }

  private static StringBuilder nlIndent(StringBuilder builder, int in) {
    if (in < 0) {
      return builder.append(", ");
    }

    return builder
        .append('\n')
        .append("  ".repeat(in));
  }

  public void appendInfo(StringBuilder builder, int indent) {
    nlIndent(builder, indent).append("width: ").append(dimensions.x);
    nlIndent(builder, indent).append("height: ").append(dimensions.y);
    nlIndent(builder, indent).append("normal: ").append(normal);
    nlIndent(builder, indent).append("center: ").append(center);
    nlIndent(builder, indent).append("world-width: ").append(worldDimensions.x);
    nlIndent(builder, indent).append("world-height: ").append(worldDimensions.y);
    nlIndent(builder, indent).append("screen-scale: ").append(screenScale);
    nlIndent(builder, indent).append("scale: ").append(scale);
    nlIndent(builder, indent).append("left-rotation: ").append(leftRotation);
    nlIndent(builder, indent).append("right-rotation: ").append(rightRotation);
    nlIndent(builder, indent).append("lo-left: ").append(loLeft);
    nlIndent(builder, indent).append("hi-left: ").append(hiLeft);
    nlIndent(builder, indent).append("lo-right: ").append(loRight);
    nlIndent(builder, indent).append("hi-right: ").append(hiRight);
  }
}
