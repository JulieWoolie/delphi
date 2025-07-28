package com.juliewoolie.delphidom;

import lombok.EqualsAndHashCode;
import org.joml.Vector2f;

@EqualsAndHashCode
public class Rect {

  public float left = 0;
  public float top = 0;
  public float bottom = 0;
  public float right = 0;

  public Rect() {
  }

  public Rect(float value) {
    set(value);
  }

  public Rect(Rect o) {
    set(o);
  }


  public Rect set(Rect rect) {
    this.left = rect.left;
    this.top = rect.top;
    this.bottom = rect.bottom;
    this.right = rect.right;
    return this;
  }

  public void set(float left, float top, float bottom, float right) {
    this.left = left;
    this.top = top;
    this.bottom = bottom;
    this.right = right;
  }

  public void set(float f) {
    left = f;
    top = f;
    right = f;
    bottom = f;
  }

  public boolean isNotZero() {
    return left > 0 || bottom > 0 || top > 0 || right > 0;
  }

  @Override
  public String toString() {
    if (!isNotZero()) {
      return "(empty edges)";
    }

    return String.format("(left=%f, top=%f, bottom=%f, right=%f, x=%f, y=%f)", left, top, bottom, right, x(), y());
  }

  public Rect mul(float scalar) {
    left *= scalar;
    top *= scalar;
    bottom *= scalar;
    right *= scalar;
    return this;
  }

  public Rect mul(Vector2f v) {
    left *= v.x;
    top *= v.y;
    bottom *= v.y;
    right *= v.x;
    return this;
  }

  public float x() {
    return left + right;
  }

  public float y() {
    return top + bottom;
  }

  public Rect min(float v) {
    left = Math.min(left, v);
    top = Math.min(top, v);
    bottom  = Math.min(bottom, v);
    right = Math.min(right, v);
    return this;
  }

  public Rect max(float v) {
    left = Math.max(left, v);
    top = Math.max(top, v);
    bottom  = Math.max(bottom, v);
    right = Math.max(right, v);
    return this;
  }
}
