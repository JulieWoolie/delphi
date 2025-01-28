package net.arcadiusmc.delphidom;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Rect {

  public float left = 0;
  public float top = 0;
  public float bottom = 0;
  public float right = 0;

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
    return String.format("(left=%f, top=%f, bottom=%f, right=%f)", left, top, bottom, right);
  }

  public void mul(float scalar) {
    left *= scalar;
    top *= scalar;
    bottom *= scalar;
    right *= scalar;
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
