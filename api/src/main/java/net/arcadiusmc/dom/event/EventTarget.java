package net.arcadiusmc.dom.event;

import net.arcadiusmc.dom.Document;

/**
 * An object which can host event listeners and be the target of {@link Event} dispatches
 */
public interface EventTarget {

  /**
   * Adds an event listener to the specified type.
   * @param eventType Event type
   * @param listener Event listener
   *
   * @throws NullPointerException if either {@code eventType} or {@code listener} is {@code null}
   */
  void addEventListener(String eventType, EventListener listener);

  /**
   * Removes an event listener.
   * <p>
   * If either the {@code eventType} or {@code listener} is {@code null}, this
   * return false.
   *
   * @param eventType Event type
   * @param listener Event listener
   *
   * @return {@code true}, if the listener was registered for the specified {@code eventType} and
   *         was removed, {@code false} otherwise.
   *
   * @throws NullPointerException if either {@code eventType} or {@code listener} is {@code null}
   */
  boolean removeEventListener(String eventType, EventListener listener);

  /**
   * Dispatches an event.
   * <p>
   * Events are executed first by the target they are dispatched on. Then, if the event
   * is set to bubble (with {@link Event#isBubbling()}) then it will bubble up through
   * the document tree.
   * <br>
   * Finally, the event will be dispatched to {@link Document#getGlobalTarget()}, unless the
   * event has been cancelled.
   *
   * @param event Event to dispatch
   *
   * @throws NullPointerException if {@code event} is {@code null}
   */
  void dispatchEvent(Event event);
}
