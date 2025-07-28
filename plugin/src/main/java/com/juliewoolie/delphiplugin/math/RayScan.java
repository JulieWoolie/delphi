package com.juliewoolie.delphiplugin.math;

import java.util.Objects;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

@Getter
public class RayScan {

  public static final float MAX_USE_DIST = 10;
  public static final float MAX_USE_DIST_SQ = MAX_USE_DIST * MAX_USE_DIST;

  private final Vector3f origin;
  private final Vector3f direction;
  private final Vector3f end;

  private final float maxLength;
  private final float maxLengthSq;

  public RayScan(Vector3f origin, Vector3f direction, float maxLength) {
    Objects.requireNonNull(origin, "Null origin");
    Objects.requireNonNull(direction, "Null direction");

    this.origin = origin;
    this.direction = direction.normalize();
    this.maxLength = maxLength;
    this.maxLengthSq = maxLength * maxLength;

    this.end = new Vector3f(direction);
    this.end.mul(maxLength);
    this.end.add(origin);
  }

  public static RayScan ofPlayer(Player player) {
    Location location = player.getEyeLocation();
    Vector dir = location.getDirection();

    return new RayScan(
        new Vector3f((float) location.x(), (float) location.y(), (float) location.z()),
        new Vector3f((float) dir.getX(), (float) dir.getY(), (float) dir.getZ()),
        MAX_USE_DIST
    );
  }
}
