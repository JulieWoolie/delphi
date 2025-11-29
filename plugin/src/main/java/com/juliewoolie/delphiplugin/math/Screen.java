package com.juliewoolie.delphiplugin.math;

import com.juliewoolie.delphirender.RenderScreen;
import org.bukkit.util.Transformation;
import org.joml.Intersectiond;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Screen implements RenderScreen {

  static final float EPSILON = 0.0000001f;

  final Vector2d dimensions = new Vector2d(0);
  public final Vector3d center = new Vector3d(0);

  // The actual dimensions of the screen in world space
  final Vector2d worldDimensions = new Vector2d(0);

  // Scale of the screen relative to the dimensions field
  public final Vector2d screenScale = new Vector2d(1);

  // Screen plane normal
  final Vector3d normal = new Vector3d(0, 0, 1);

  // Points of the screen
  public final Vector3d loRight = new Vector3d(0);
  public final Vector3d hiRight = new Vector3d(0);
  public final Vector3d loLeft = new Vector3d(0);
  public final Vector3d hiLeft = new Vector3d(0);

  // Transformations
  public final Vector3f scale = new Vector3f(1);
  public final Quaternionf leftRotation = new Quaternionf();
  public final Quaternionf rightRotation = new Quaternionf();

  public final Vector3d boundingBoxSize = new Vector3d();
  public final Vector3d boundingBoxMin = new Vector3d();
  public final Vector3d boundingBoxMax = new Vector3d();

  public static void lookInDirection(Quaternionf lrot, Vector3f dir) {
    // I've definitely fucked up some order of operations here,
    // because why is the global up = -1
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

  public void translate(Vector3d offset) {
    center.add(offset);
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

  public void set(Vector3d center, float w, float h) {
    this.center.set(center);
    this.dimensions.set(w, h).absolute();
    recalculate();
  }

  public void transformPoint(Vector3d point) {
    leftRotation.transform(point);
    point.mul(scale);
    rightRotation.transform(point);
  }

  public void recalculate() {
    normal.set(0, 0, 1);
    transformPoint(normal);
    normal.normalize();

    findPoints();

    Vector3d lrDif = new Vector3d(); // Left-Right dif
    Vector3d udDif = new Vector3d(); // Up-Down dif

    loRight.sub(hiRight, udDif);
    loRight.sub(loLeft, lrDif);

    worldDimensions.x = lrDif.length();
    worldDimensions.y = udDif.length();

    worldDimensions.div(dimensions, screenScale);

    // Calculate bounding box
    boundingBoxMin.set(loLeft).min(loRight).min(hiLeft).min(hiRight);
    boundingBoxMax.set(loLeft).max(loRight).max(hiLeft).max(hiRight);
    boundingBoxMax.sub(boundingBoxMin, boundingBoxSize);
  }

  void findPoints() {
    Vector3d up = new Vector3d(0, 1, 0);

    // Transform so it's screen's up direction
    transformPoint(up);
    up.normalize();

    Vector3d right = new Vector3d();
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

  public void screenToWorld(Vector2f screenPoint, Vector3d out) {
    Vector2f in = new Vector2f();
    screenToScreenspace(screenPoint, in);
    screenspaceToWorld(in, out);
  }

  public void screenToScreenspace(Vector2f in, Vector2f out) {
    out.x = (float) (in.x / dimensions.x);
    out.y = (float) (in.y / dimensions.y);
  }

  public void screenspaceToScreen(Vector2f in, Vector2f out) {
    out.x = (float) (in.x * dimensions.x);
    out.y = (float) (in.y * dimensions.y);
  }

  public void screenspaceToWorld(Vector2f screenPoint, Vector3d out) {
    Vector3d height = new Vector3d(hiLeft).sub(loLeft);
    Vector3d width = new Vector3d(loRight).sub(loLeft);

    height.mul(screenPoint.y);
    width.mul(screenPoint.x);

    out.set(loLeft);
    out.add(width);
    out.add(height);
  }

  /* --------------------------- ray casting ---------------------------- */

  public boolean castRay(RayScan scan, Vector3d out, Vector2f screenOut) {
    if (!planeIntersect(scan, out)) {
      return false;
    }

    screenHitPoint(out, screenOut);

    return (screenOut.x >= 0 && screenOut.x <= 1)
        && (screenOut.y >= 0 && screenOut.y <= 1);
  }

  public void screenHitPoint(Vector3d hitPoint, Vector2f out) {
    Vector3d height = new Vector3d(hiLeft).sub(loLeft);
    Vector3d width = new Vector3d(loRight).sub(loLeft);

    Vector3d relativePoint = new Vector3d(hitPoint).sub(loLeft);

    double x = relativePoint.dot(width) / width.lengthSquared();
    double y = relativePoint.dot(height) / height.lengthSquared();

    out.set(x, y);
  }

  public boolean planeIntersect(RayScan scan, Vector3d out) {
    double t = Intersectiond.intersectRayPlane(
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

  /* --------------------------- Projection --------------------------- */

  @Override
  public void project(Transformation transform) {
    project(this, transform);
  }

  protected void project(Screen screen, Transformation transform) {
    Vector3f translate = transform.getTranslation();
    Vector3f scale = transform.getScale();

    Vector3f screenWorldScale = screen.getScale();

    Quaternionf lrot = screen.getLeftRotation();
    Quaternionf rrot = screen.getRightRotation();

    translate.x *= (float) screen.getScreenScale().x;
    translate.y *= (float) screen.getScreenScale().y;

    scale.mul(screenWorldScale);

    lrot.transform(translate);
    rrot.transform(translate);

    transform.getLeftRotation().mul(lrot);
    transform.getRightRotation().mul(rrot);
  }

  /* --------------------------- API impl ---------------------------- */

  @Override
  public Quaternionf getLeftRotation() {
    return leftRotation;
  }

  @Override
  public Quaternionf getRightRotation() {
    return rightRotation;
  }

  @Override
  public Vector2d getScreenScale() {
    return screenScale;
  }

  @Override
  public Vector3f getScale() {
    return scale;
  }

  @Override
  public double getWidth() {
    return dimensions.x;
  }

  @Override
  public double getHeight() {
    return dimensions.y;
  }

  @Override
  public double getWorldWidth() {
    return worldDimensions.x;
  }

  @Override
  public double getWorldHeight() {
    return worldDimensions.y;
  }

  @Override
  public Vector3d normal() {
    return new Vector3d(normal);
  }

  @Override
  public Vector3d center() {
    return new Vector3d(center);
  }

  @Override
  public Vector2f getDimensions() {
    return new Vector2f(dimensions);
  }

  @Override
  public void getDimensions(Vector2f out) {
    out.set(dimensions);
  }

  @Override
  public Vector3d getLowerLeft() {
    return new Vector3d(loLeft);
  }

  @Override
  public Vector3d getLowerRight() {
    return new Vector3d(loRight);
  }

  @Override
  public Vector3d getUpperLeft() {
    return new Vector3d(hiLeft);
  }

  @Override
  public Vector3d getUpperRight() {
    return new Vector3d(hiRight);
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
    nlIndent(builder, indent).append("boundingbox.min: ").append(boundingBoxMin);
    nlIndent(builder, indent).append("boundingbox.max: ").append(boundingBoxMax);
    nlIndent(builder, indent).append("boundingbox.size: ").append(boundingBoxSize);
  }
}
