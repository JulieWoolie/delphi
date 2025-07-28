package com.juliewoolie.dom.event;

import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * Event interface used to provide contextual information about an event.
 */
public interface Event {

  /**
   * Gets the event type.
   * @return Event type
   * @see EventTypes
   */
  String getType();

  /**
   * Gets the node this event was called on.
   * <p>
   * Will return {@code null} on events fired from {@link Document} instances.
   *
   * @return Event target
   */
  @Nullable
  Element getTarget();

  /**
   * Gets the event target currently executing this event
   * @return Current event target
   */
  EventTarget getCurrentTarget();

  /**
   * Gets the document this event was fired in.
   * @return Owning document
   */
  Document getDocument();

  /**
   * Gets the current event phase
   * @return Event phase
   */
  EventPhase getPhase();

  /**
   * Tests if the event has been cancelled
   * @return {@code true}, if {@link #preventDefault()} has been called at last once,
   *         {@code false} otherwise.
   */
  boolean isCancelled();

  /**
   * Tests if this event bubbles.
   * <p>
   * Bubbling means the event will first be executed at its origin node, and then bubble
   * up through the document tree until it reaches the root element (The body).
   *
   * @return {@code true}, if this event is set to bubble.
   */
  boolean isBubbling();

  /**
   * Tests if this event should no longer be dispatched to listeners
   * @return {@code true}, if this event should no longer be dispatched to listeners,
   *         {@code false} otherwise.
   */
  boolean isPropagationStopped();

  /**
   * Tests if this event can be cancelled
   * @return {@code true}, if the event can be cancelled, {@code false} otherwise.
   */
  boolean isCancellable();

  /**
   * Tests if an internal initialization method has been called, if it has, then this event
   * can be passed to {@link EventTarget#dispatchEvent(Event)} without issue.
   *
   * @return {@code true}, if the event has been initialized, {@code false}, otherwise.
   */
  boolean isComposed();

  /**
   * Stops this event from being propagated to other listeners
   */
  void stopPropagation();

  /**
   * Stops the 'default' behaviour.
   * <p>
   * If {@link #isCancellable()} return {@code false}, then this function does nothing.
   */
  void preventDefault();
}
