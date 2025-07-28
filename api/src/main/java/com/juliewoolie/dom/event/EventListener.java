package com.juliewoolie.dom.event;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;

/**
 * Event listener functional interface
 */
@FunctionalInterface
@OverrideOnly
public interface EventListener {

  /**
   * Handle the event
   * @param event Event
   */
  void onEvent(Event event);

  /**
   * Typed event listener
   * @param <T> Event type
   */
  @FunctionalInterface
  @OverrideOnly
  interface Typed<T extends Event> extends EventListener {

    /**
     * Handle the event
     * @param event Event
     */
    void handleEvent(T event);

    @Override
    default void onEvent(Event event) {
      handleEvent((T) event);
    }
  }
}
