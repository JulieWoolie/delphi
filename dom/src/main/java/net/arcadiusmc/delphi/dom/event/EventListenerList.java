package net.arcadiusmc.delphi.dom.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.Loggers;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTarget;
import org.slf4j.Logger;

@Getter @Setter
public class EventListenerList implements EventTarget {

  private static final Logger LOGGER = Loggers.getLogger();

  final Map<String, List<EventListener>> listenerMap = new HashMap<>();

  boolean ignorePropagationStops = false;
  boolean ignoreCancelled = true;
  EventTarget realTarget;

  @Override
  public void addEventListener(String eventType, EventListener listener) {
    Objects.requireNonNull(eventType, "Null event type");
    Objects.requireNonNull(listener, "Null listener");

    List<EventListener> list = listenerMap.computeIfAbsent(eventType, s -> new ArrayList<>());
    list.add(listener);
  }

  @Override
  public boolean removeEventListener(String eventType, EventListener listener) {
    Objects.requireNonNull(eventType, "Null event type");
    Objects.requireNonNull(listener, "Null listener");

    List<EventListener> list = listenerMap.get(eventType);

    if (list == null || list.isEmpty()) {
      return false;
    }

    return list.remove(listener);
  }

  public void validateEventCall(Event event) {
    Objects.requireNonNull(event, "Null event");

    if (!event.isComposed()) {
      throw new IllegalArgumentException(
          "dispatchEvent called with non-composed " + event.getType() + " event"
      );
    }
  }

  @Override
  public void dispatchEvent(Event event) {
    validateEventCall(event);

    if (!ignoreCancelled && event.isCancelled()) {
      return;
    }
    if (!ignorePropagationStops && event.isPropagationStopped()) {
      return;
    }

    String type = event.getType();
    List<EventListener> list = listenerMap.get(type);

    if (list == null || list.isEmpty()) {
      return;
    }

    if (realTarget != null) {
      EventImpl impl = (EventImpl) event;
      impl.setCurrentTarget(realTarget);
    }

    for (EventListener listener : list) {
      dispatchSafe(event, listener);

      if (!ignorePropagationStops && event.isPropagationStopped()) {
        break;
      }
    }
  }

  private void dispatchSafe(Event event, EventListener listener) {
    try {
      listener.onEvent(event);
    } catch (Exception exc) {
      LOGGER.error("Error executing event '{}'", event.getType(), exc);
    }
  }
}
