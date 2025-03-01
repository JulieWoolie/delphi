package net.arcadiusmc.delphi;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The plane a {@link DocumentView} exists on.
 * <p>
 * Screen coordinates exist in one of 3 spaces
 * <dl>
 *   <dt>World space coordinates</dt>
 *   <dd>They denote the actual size and dimensions of a screen in a minecraft world.</dd>
 *
 *   <dt>Intermediate coordinates</dt>
 *   <dd>
 *     Set with the {@code screen-width} and {@code screen-height} document options. They may not
 *     always be actual sizes of the screen.
 *   </dd>
 *
 *   <dt>Screen coordinates</dt>
 *   <dd>{@code 0} to {@code 1} coordinates relative to the width and height of the screen.</dd>
 * </dl>
 *
 * Screens store an internal {@link org.bukkit.util.Transformation} that is applied to the screen.
 * This may mean that (if scaling is involved) world and intermediate coordinates may differ in
 * sizes.
 */
public interface Screen {

  /**
   * Default width of the screen: {@code 3}
   */
  float DEFAULT_WIDTH = 3;

  /**
   * Default height of the screen: {@code 2}
   */
  float DEFAULT_HEIGHT = 2;

  /**
   * Minimum screen size: {@code 1}
   */
  float MIN_SCREEN_SIZE = 1;

  /**
   * Maximum screen size: {@code 10}
   */
  float MAX_SCREEN_SIZE = 10;

  /**
   * Gets the width of the screen
   * @return Screen width
   */
  float getWidth();

  /**
   * Get the actual width of the screen in world space size
   * @return Actual screen width
   */
  float getWorldWidth();

  /**
   * Gets the height of the screen
   * @return Screen height
   */
  float getHeight();

  /**
   * Get the actual height of the screen in the world space size
   * @return Actual screen height
   */
  float getWorldHeight();

  /**
   * Gets the normal (direction) of the screen's plane
   * @return Screen normal
   */
  Vector3f normal();

  /**
   * Gets the center point of the screen
   * @return Screen center
   */
  Vector3f center();

  /**
   * Gets the width and height of the screen
   * @return Screen dimensions, (width, height)
   */
  Vector2f getDimensions();

  /**
   * Gets the lower left corner of the screen from the viewer's perspective
   * @return Lower left corner
   */
  Vector3f getLowerLeft();

  /**
   * Gets the lower right corner of the screen from the viewer's perspective
   * @return Lower right corner
   */
  Vector3f getLowerRight();

  /**
   * Gets the upper left corner of the screen from the viewer's perspective
   * @return Upper left corner
   */
  Vector3f getUpperLeft();

  /**
   * Gets the upper right corner of the screen from the viewer's perspective
   * @return Upper right corner
   */
  Vector3f getUpperRight();

  /**
   * Maps screen coordinates in range [0..{@link #getDimensions()}] to world coordinates and stores
   * the result in the {@code out} argument.
   *
   * @param screenPoint Screen point, in space [0..{@link #getDimensions()}]
   * @param out Result destination
   */
  void screenToWorld(Vector2f screenPoint, Vector3f out);

  /**
   * Maps screen coordinates in range [0..{@link #getDimensions()}] to [0..1] space and stores the
   * result in the {@code out} argument.
   *
   * @param in Input in range [0..{@link #getDimensions()}]
   * @param out Result output, in range [0..1]
   */
  void screenToScreenspace(Vector2f in, Vector2f out);

  /**
   * Maps screen coordinates in range [0..1] to [0..{@link #getDimensions()}] space and stores the
   * result in the {@code out} argument.
   *
   * @param in Input in range [0..1]
   * @param out Result output, in range [0..{@link #getDimensions()}]
   */
  void screenspaceToScreen(Vector2f in, Vector2f out);

  /**
   * Maps screen coordinates in range [0..1] to world coordinates and stores the
   * result in the {@code out} argument.
   *
   * @param screenPoint Screen point, in space [0..1]
   * @param out Result destination
   */
  void screenspaceToWorld(Vector2f screenPoint, Vector3f out);
}
