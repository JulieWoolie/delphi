package com.juliewoolie.nlayout;

import org.joml.Vector2f;

public class LayoutItem extends LayoutNode {

  public MeasureFunc measureFunc;

  public LayoutItem() {
    super();
  }

  public void measure(Vector2f out) {
    if (measureFunc == null) {
      out.set(0);
      return;
    }

    measureFunc.measure(out);
  }
}
