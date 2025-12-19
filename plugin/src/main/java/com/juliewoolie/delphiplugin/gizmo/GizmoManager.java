package com.juliewoolie.delphiplugin.gizmo;

import static com.juliewoolie.delphiplugin.Debug.line;

import com.destroystokyo.paper.ParticleBuilder;
import com.juliewoolie.delphiplugin.Debug;
import com.juliewoolie.delphiplugin.DelphiPlugin;
import com.juliewoolie.delphiplugin.math.RayScan;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;

public class GizmoManager {

  private final List<DelphiGizmo> gizmos = new ArrayList<>();

  private final DelphiPlugin plugin;
  private BukkitTask tickTask;

  public GizmoManager(DelphiPlugin plugin) {
    this.plugin = plugin;
  }

  public DelphiGizmo getSelectedByPlayer(Player player) {
    return gizmos.stream()
        .filter(gizmo -> player.equals(gizmo.getPlayer()))
        .filter(DelphiGizmo::isSelected)
        .findFirst()
        .orElse(null);
  }

  public void startTicking() {
    if (tickTask != null) {
      stopTicking();
    }

    tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1, 1);
  }

  public void stopTicking() {
    tickTask.cancel();
    tickTask = null;
  }

  public DelphiGizmo createGimbal() {
    DelphiGizmo gimbal = new DelphiGizmo(plugin);
    gizmos.addLast(gimbal);
    return gimbal;
  }

  final Matrix4d invMat = new Matrix4d();
  final Vector4d middle = new Vector4d();

  static Matrix4d transformationToMatrix(Transformation t, Vector3d gizmoPosition) {
    Vector3f scale = t.getScale();

    return new Matrix4d()
        .translate(t.getTranslation())
        .translate(gizmoPosition)
        .rotate(t.getLeftRotation())
        .scale(scale.x, scale.y, scale.z)
        .rotate(t.getRightRotation());
  }

  public void transform(Matrix4d mat, Vector3d in, Vector3d out) {
    invMat.set(mat);
    middle.set(in, 1.0f);
    invMat.transform(middle);
    out.set(middle);
  }

  public void tick() {
    for (DelphiGizmo gimbal : gizmos) {
      World world = gimbal.getWorld();
      Player player = gimbal.getPlayer();

      if (player == null || world == null) {
        continue;
      }
      if (!gimbal.isActive()) {
        continue;
      }
      if (!player.getWorld().equals(world)) {
        continue;
      }

      gimbal.updatePartOffsets();

      Vector3d origin = new Vector3d();
      Vector3d direction = new Vector3d();

      Location eyeLocation = player.getEyeLocation();
      origin.x = eyeLocation.getX();
      origin.y = eyeLocation.getY();
      origin.z = eyeLocation.getZ();

      Vector direction1 = eyeLocation.getDirection();
      direction.x = direction1.getX();
      direction.y = direction1.getY();
      direction.z = direction1.getZ();

      Transformation baseTrans = gimbal.getBaseTransform();
      Matrix4d baseMatrix = transformationToMatrix(baseTrans, gimbal.position);
      invMat.set(baseMatrix).invert();

      // Inverse transform the origin
      middle.set(origin, 1.0f);
      invMat.transform(middle);
      origin.set(middle);

      // Inverse transform the Direction
      invMat.transformDirection(direction);

      RayScan rayscan = new RayScan(origin, direction, RayScan.MAX_USE_DIST * 0.5f);

      double closest = Double.MAX_VALUE;
      Part hitPart = null;

      for (Part value : Part.VALUES) {
        GizmoPart part = gimbal.getPart(value);
        if (!part.isAlive()) {
          continue;
        }

        double dist = part.castRay(rayscan);
        if (dist < 0) {
          continue;
        }
        if (dist >= closest) {
          continue;
        }

        closest = dist;
        hitPart = value;
      }

      if (hitPart == null) {
        gimbal.onNotHit();
        continue;
      }

      Vector3d hitPosition = new Vector3d();
      hitPosition.set(origin);
      hitPosition.x += closest * direction.x;
      hitPosition.y += closest * direction.y;
      hitPosition.z += closest * direction.z;

      transform(baseMatrix, hitPosition, hitPosition);

      gimbal.onPartHit(hitPosition, hitPart);
    }
  }

  static void partOutline(GizmoPart part, DelphiGizmo gimbal, Matrix4d mat) {
    Vector3d min = new Vector3d();
    Vector3d max = new Vector3d();

//    min.set(gimbal.position);
    min.add(part.offset);

    max.set(min);
    max.add(part.size);

    ParticleBuilder builder = Debug.particleBuilder(Color.RED);
    cubeOutline(builder, gimbal.position, min, max, gimbal.getWorld(), mat);
  }

  static void cubeOutline(
      ParticleBuilder builder,
      Vector3d worldPos,
      Vector3d min,
      Vector3d max,
      World world,
      Matrix4d mat
  ) {
    Vector3d[] points = new Vector3d[8];

    for (int i = 1; i < 4; i++) {
      points[i] = new Vector3d(min);
    }
    for (int i = 5; i < points.length; i++) {
      points[i] = new Vector3d(max);
    }

    points[0] = min;
    points[1].x = max.x;
    points[2].z = max.z;
    points[3].set(max.x, min.y, max.z);
    points[4] = max;
    points[5].x = min.x;
    points[6].z = min.z;
    points[7].set(min.x, max.y, min.z);

    Vector4d v4 = new Vector4d();
    for (int i = 0; i < points.length; i++) {
      Vector3d p = points[i];
      v4.set(p.x, p.y, p.z, 1.0);
      mat.transform(v4);
      p.set(v4);
    }

    for (int i = 0; i < points.length; i++) {
      points[i].add(worldPos);
    }

    // Bottom
    line(points[0], points[1], builder, world);
    line(points[0], points[2], builder, world);
    line(points[1], points[3], builder, world);
    line(points[2], points[3], builder, world);

    if (min.y() != max.y()) {
      // Top
      line(points[4], points[5], builder, world);
      line(points[4], points[6], builder, world);
      line(points[5], points[7], builder, world);
      line(points[6], points[7], builder, world);

      // Sides
      line(points[0], points[7], builder, world);
      line(points[1], points[6], builder, world);
      line(points[2], points[5], builder, world);
      line(points[3], points[4], builder, world);
    }
  }

  public void killAll() {
    for (DelphiGizmo gizmo : gizmos) {
      gizmo.kill();
    }
    gizmos.clear();
  }
}
