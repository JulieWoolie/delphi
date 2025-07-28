package com.juliewoolie.delphirender.layout;

import java.util.Stack;
import com.juliewoolie.delphirender.RenderScreen;
import com.juliewoolie.delphirender.RenderSystem;
import org.joml.Vector2f;

public class MeasureContext {

  final Stack<Vector2f> parentSizes = new Stack<>();
  final Vector2f screenSize = new Vector2f(0);

  final RenderSystem system;
  final RenderScreen screen;

  public MeasureContext(RenderSystem system, RenderScreen screen) {
    this.system = system;
    this.screen = screen;
  }
}
