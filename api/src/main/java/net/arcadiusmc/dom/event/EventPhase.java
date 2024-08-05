package net.arcadiusmc.dom.event;

import net.arcadiusmc.dom.Document;

/**
 * Event execution phase.
 */
public enum EventPhase {
  /**
   * Event handling is taking place in the element that triggered the event
   */
  ORIGIN,

  /**
   * Event is bubbling up through the DOM tree
   */
  BUBBLING,

  /**
   * Event is being handled by the global event handler {@link Document#getGlobalTarget()}.
   * @see Document#getGlobalTarget()
   */
  GLOBAL;
}
