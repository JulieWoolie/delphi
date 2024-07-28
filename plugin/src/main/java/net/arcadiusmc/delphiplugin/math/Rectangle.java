package net.arcadiusmc.delphiplugin.math;

import lombok.Getter;
import org.joml.Vector2f;

@Getter
public class Rectangle {

  private final Vector2f position = new Vector2f(0);
  private final Vector2f size = new Vector2f(0);

  public void set(Vector2f position, Vector2f size) {
    this.position.set(position);
    this.size.set(size);
  }

  public boolean contains(Vector2f point) {
    float relX = point.x - position.x;
    float relY = point.y - position.y;

    return (relX >= 0 && relX <= size.x)
        && (relY >= 0 && relY <= size.y);
  }

  public void getMax(Vector2f out) {
    out.set(position).add(size);
  }

  @Override
  public String toString() {
    return "(position=" + position + ", size=" + size + ")";
  }
}
