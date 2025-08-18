package com.juliewoolie.nlayout;

import org.joml.Vector2f;

public abstract class LayoutNode {

  public final Vector2f position = new Vector2f();
  public final Vector2f size = new Vector2f();

  public int domIndex = 0;

  public LayoutNode() {

  }

  protected void moveTo(float x, float y) {
    position.x = x;
    position.y = y;
  }
}
