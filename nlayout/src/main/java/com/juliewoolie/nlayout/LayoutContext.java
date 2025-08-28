package com.juliewoolie.nlayout;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanStack;
import java.util.Stack;
import org.joml.Vector2f;

public class LayoutContext {

  final Vector2f screenSize;
  final Stack<Vector2f> parentSizes = new Stack<>();
  final BooleanStack definiteWidths = new BooleanArrayList();
  final BooleanStack definiteHeights = new BooleanArrayList();

  public LayoutContext(Vector2f screenSize) {
    this.screenSize = screenSize;
    definiteWidths.push(true);
    definiteHeights.push(true);
    parentSizes.push(screenSize);
  }

  boolean isWidthDefinite() {
    return definiteWidths.topBoolean();
  }

  boolean isHeightDefinite() {
    return definiteHeights.topBoolean();
  }
}
