package net.arcadiusmc.delphiplugin.math;

import java.util.Objects;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class RayScan {

  private final Vector3f origin;
  private final Vector3f direction;
  private final Vector3f end;

  private final float maxLength;

  public RayScan(Vector3f origin, Vector3f direction, float maxLength) {
    Objects.requireNonNull(origin, "Null origin");
    Objects.requireNonNull(direction, "Null direction");

    this.origin = origin;
    this.direction = direction.normalize();
    this.maxLength = maxLength;

    this.end = new Vector3f(direction);
    this.end.mul(maxLength);
    this.end.add(origin);
  }
}
