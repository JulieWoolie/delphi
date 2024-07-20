package net.arcadiusmc.dom.event;

public interface EventListener {

  void onEvent(Event event);

  interface Typed<T extends Event> extends EventListener {

    void handleEvent(T event);

    @Override
    default void onEvent(Event event) {
      handleEvent((T) event);
    }
  }
}
