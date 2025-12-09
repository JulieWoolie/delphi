package com.juliewoolie.dom;

import com.juliewoolie.delphi.Screen;
import org.joml.Vector2f;

/**
 * 2D bounding box of coordinates on a {@link Screen}.
 * <p>
 * Both the minimum and maximum positions can be converted to world coordinates with
 * {@link Screen#screenToWorld(Vector2f, org.joml.Vector3d)}.
 */
public interface RenderBounds {

  /**
   * Get the minimum coordinate of the bounding box
   * @return Minimum bounding box position
   */
  Vector2f getMinimum();

  /**
   * Get the maximum coordinate of the bounding box
   * @return Maximum bounding box position
   */
  Vector2f getMaximum();

  /**
   * Get the difference between {@link #getMaximum()} and {@link #getMinimum()}
   * @return Bounding box size
   */
  Vector2f getSize();
}
