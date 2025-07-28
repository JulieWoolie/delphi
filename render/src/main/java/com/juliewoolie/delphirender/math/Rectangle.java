package com.juliewoolie.delphirender.math;

import lombok.Getter;
import org.joml.Vector2f;

@Getter
public class Rectangle {

  public final Vector2f position = new Vector2f(0);
  public final Vector2f size = new Vector2f(0);

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

  public void set(Rectangle other) {
    this.position.set(other.position);
    this.size.set(other.size);
  }

  public void encompass(Rectangle other) {
    encompass(other, this);
  }

  public void encompass(Rectangle other, Rectangle dest) {
    if (size.x <= 0 && size.y <= 0) {
      set(other);
      return;
    }

    // Encompass X
    if (position.x < other.position.x) {
      float xDif = (position.x - other.position.x) + other.size.x;
      dest.size.x = Math.max(size.x, xDif);
    } else {
      float xDif = (other.position.x - position.x) + other.size.x;
      dest.size.x = Math.max(size.x, xDif);
      dest.position.x = other.position.x;
    }

    // Encompass Y
    if (position.y < other.position.y) {
      float yDif = (position.y - other.position.y) + other.size.y;
      dest.size.y = Math.max(size.y, yDif);
    } else {
      float yDif = (other.position.y - position.y) + other.size.y;
      dest.size.y = Math.max(size.y, yDif);
      dest.position.y = other.position.y;
    }
  }
}
