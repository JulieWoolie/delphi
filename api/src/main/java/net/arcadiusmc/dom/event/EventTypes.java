package net.arcadiusmc.dom.event;

import net.arcadiusmc.dom.Document;

public interface EventTypes {
  /**
   * Called when a cursor enter an element's bounds.
   * @see MouseEvent
   */
  String MOUSE_ENTER = "mouse-over";

  /**
   * Called when a cursor leaves an element's bounds.
   * @see MouseEvent
   */
  String MOUSE_LEAVE = "mouse-exit";

  /**
   * Called when the mouse is moved inside of an element.
   * <p>
   * This event will only be called if the mouse movement happens inside of an element,
   * if the mouse moves in or out of an element, then {@link #MOUSE_LEAVE} or {@link #MOUSE_ENTER}
   * will be called instead.
   *
   * @see MouseEvent
   */
  String MOUSE_MOVE = "mouse-move";

  /**
   * Called when the mouse is clicked
   * @see MouseEvent
   */
  String MOUSE_DOWN = "mouse-down";

  /**
   * Called when an element stops being active after being clicked.
   * <p>
   * Length of time between clicking and the active state expiring is determined by
   * {@link Document#ACTIVE_TICKS}
   *
   * @see MouseEvent
   */
  String CLICK_EXPIRE = "click-expire";

  /**
   * Called when a node is appended onto another node.
   * @see MutationEvent
   */
  String APPEND_CHILD = "append-child";

  /**
   * Called when a node is removed from another node
   * @see MutationEvent
   */
  String REMOVE_CHILD = "remove-child";

  /**
   * Called when an attribute is modified.
   * @see AttributeMutateEvent
   */
  String MODIFY_ATTR = "modify-attribute";

  /**
   * Called when an option is modified.
   * @see AttributeMutateEvent
   * @see Document#setOption(String, String)
   */
  String MODIFY_OPTION = "modify-option";
}
