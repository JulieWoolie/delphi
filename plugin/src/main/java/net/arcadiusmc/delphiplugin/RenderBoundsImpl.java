package net.arcadiusmc.delphiplugin;

import net.arcadiusmc.dom.RenderBounds;
import org.joml.Vector2f;

public class RenderBoundsImpl implements RenderBounds {

  private final Vector2f min;
  private final Vector2f max;
  private final Vector2f size;

  public RenderBoundsImpl(Vector2f position, Vector2f size) {
    // Noted in the render module, render elements have their origin point
    // on the top left of the element, even though the coordinate system
    // originates from the bottom left, so perform a quick calculation to
    // convert

    this.min = new Vector2f(position.x, position.y - size.y);
    this.max = new Vector2f(position.x + size.x, position.y);
    this.size = new Vector2f(size);
  }

  @Override
  public Vector2f getMinimum() {
    return new Vector2f(min);
  }

  @Override
  public Vector2f getMaximum() {
    return new Vector2f(max);
  }

  @Override
  public Vector2f getSize() {
    return new Vector2f(size);
  }
}
