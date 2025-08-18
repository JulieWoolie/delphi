package com.juliewoolie.nlayout;

import java.util.Stack;
import org.joml.Vector2f;

public class LayoutContext {

  final Vector2f screenSize;
  final Stack<Vector2f> parentSizes = new Stack<>();

  public LayoutContext(Vector2f screenSize) {
    this.screenSize = screenSize;
  }

  public void parentSize(Vector2f out) {
    if (parentSizes.isEmpty()) {
      out.set(screenSize);
      return;
    }

    out.set(parentSizes.peek());
  }
}
