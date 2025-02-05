package net.arcadiusmc.delphirender.layout;

import java.util.Stack;
import net.arcadiusmc.delphirender.RenderScreen;
import net.arcadiusmc.delphirender.RenderSystem;
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
