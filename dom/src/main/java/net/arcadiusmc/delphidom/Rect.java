package net.arcadiusmc.delphidom;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Rect {

  public float left = 0;
  public float top = 0;
  public float bottom = 0;
  public float right = 0;

  public void set(Rect rect) {
    this.left = rect.left;
    this.top = rect.top;
    this.bottom = rect.bottom;
    this.right = rect.right;
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
}
