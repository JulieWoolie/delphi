package net.arcadiusmc.dom.event;

import org.joml.Vector2f;
import org.joml.Vector3f;

public interface MouseEvent extends Event {

  /**
   * Tests if the viewer has the shift key held down.
   *
   * @apiNote It is impossible to know if a player is actually holding shift, this will
   *          simply return if a player was sneaking when they pressed on the mouse,
   *
   * @return {@code true}, if the shift key was pressed {@code false} otherwise
   */
  boolean isShiftPressed();

  /**
   * Gets the button that was pressed to trigger this event, or {@link MouseButton#NONE} if
   * this button is not related to a mouse button action.
   *
   * @return Pressed button
   */
  MouseButton getButton();

  /**
   * Gets the scroll direction, or {@link ScrollDirection#NONE}, if this event is not
   * related to a scroll action.
   *
   * @return Scroll direction.
   */
  ScrollDirection getScrollDirection();

  /**
   * Gets the screen coordinates of the viewer's cursor
   * @return Cursor screen position
   */
  Vector2f getScreenPosition();

  /**
   * Gets the world space position of the viewer's cursor
   * @return Cursor world position
   */
  Vector3f getWorldPosition();
}
