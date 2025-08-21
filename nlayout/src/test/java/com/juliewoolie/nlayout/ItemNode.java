package com.juliewoolie.nlayout;

import org.joml.Vector2f;

class ItemNode extends TestNode implements MeasureFunc {

  float funcWidth;
  float funcHeight;

  @Override
  public void measure(Vector2f out) {
    out.x = funcWidth;
    out.y = funcHeight;
  }
}
