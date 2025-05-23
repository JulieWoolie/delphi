package net.arcadiusmc.delphidom.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.InputEvent;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;
import net.arcadiusmc.dom.event.MutationEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Getter @Setter
public class EventListenerList implements EventTarget {

  public static boolean testEnvironment = true;

  private static final Logger LOGGER = Loggers.getLogger();

  final Map<String, List<EventListener>> listenerMap = new HashMap<>();
  final Map<String, ListenerProperty> propertyMap = new HashMap<>();

  boolean ignorePropagationStops = false;
  boolean ignoreCancelled = true;
  EventTarget realTarget;

  Consumer<Event> postRunListener = null;
  List<EventListener> listenerBuffer = new ArrayList<>(10);

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
    if (!Bukkit.isPrimaryThread()) {
      throw new IllegalStateException("Events may only be dispatched from the main thread");
    }

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

    // Use a secondary array for calling the listeners to prevent
    // a concurrent modification exception, which may occur from
    // a listener removing itself
    listenerBuffer.clear();
    listenerBuffer.addAll(list);

    if (realTarget != null) {
      EventImpl impl = (EventImpl) event;
      impl.setCurrentTarget(realTarget);
    }

    for (int i = 0; i < listenerBuffer.size(); i++) {
      EventListener listener = listenerBuffer.get(i);
      dispatchSafe(event, listener);

      if (!ignorePropagationStops && event.isPropagationStopped()) {
        break;
      }
    }

    if (postRunListener == null) {
      return;
    }

    postRunListener.accept(event);
  }

  private void dispatchSafe(Event event, EventListener listener) {
    try {
      listener.onEvent(event);
    } catch (Exception exc) {
      if (!testEnvironment) {
        throw new RuntimeException(exc);
      }

      LOGGER.error("Error executing event '{}'", event.getType(), exc);
    }
  }

  private void setListenerProperty(String propertyName, String eventType, EventListener listener) {
    if (listener == null) {
      ListenerProperty prop = propertyMap.remove(propertyName);
      if (prop == null) {
        return;
      }

      removeEventListener(prop.eventType, prop.listener);
      return;
    }

    ListenerProperty prop = new ListenerProperty(eventType, listener);
    propertyMap.put(propertyName, prop);

    addEventListener(eventType, listener);
  }

  @Override
  public void onClick(@Nullable EventListener.Typed<MouseEvent> listener) {
    setListenerProperty(
        "left-click",
        EventTypes.CLICK,
        listener == null ? null : new MouseButtonListener(listener, MouseButton.LEFT)
    );
  }

  @Override
  public void onRightClick(@Nullable EventListener.Typed<MouseEvent> listener) {
    setListenerProperty(
        "right-click",
        EventTypes.CLICK,
        listener == null ? null : new MouseButtonListener(listener, MouseButton.RIGHT)
    );
  }

  @Override
  public void onMouseEnter(@Nullable EventListener.Typed<MouseEvent> listener) {
    setListenerProperty("mouse-enter", EventTypes.MOUSE_ENTER, listener);
  }

  @Override
  public void onMouseExit(@Nullable EventListener.Typed<MouseEvent> listener) {
    setListenerProperty("mouse-exit", EventTypes.MOUSE_LEAVE, listener);
  }

  @Override
  public void onMouseMove(@Nullable EventListener.Typed<MouseEvent> listener) {
    setListenerProperty("mouse-move", EventTypes.MOUSE_MOVE, listener);
  }

  @Override
  public void onAppendChild(@Nullable EventListener.Typed<MutationEvent> listener) {
    setListenerProperty("append-child", EventTypes.APPEND_CHILD, listener);
  }

  @Override
  public void onRemoveChild(@Nullable EventListener.Typed<MutationEvent> listener) {
    setListenerProperty("remove-child", EventTypes.REMOVE_CHILD, listener);
  }

  @Override
  public void onAttributeChange(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty("attr-change", EventTypes.MODIFY_ATTR, listener);
  }

  @Override
  public void onSetAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty(
        "attr-set",
        EventTypes.MODIFY_ATTR,
        listener == null ? null : new AttributeActionListener(listener, AttributeAction.SET)
    );
  }

  @Override
  public void onRemoveAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty(
        "attr-remove",
        EventTypes.MODIFY_ATTR,
        listener == null ? null : new AttributeActionListener(listener, AttributeAction.REMOVE)
    );
  }

  @Override
  public void onAddAttribute(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty(
        "attr-add",
        EventTypes.MODIFY_ATTR,
        listener == null ? null : new AttributeActionListener(listener, AttributeAction.ADD)
    );
  }

  @Override
  public void onOptionChange(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty("option-change", EventTypes.MODIFY_OPTION, listener);
  }

  @Override
  public void onSetOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty(
        "option-set",
        EventTypes.MODIFY_OPTION,
        listener == null ? null : new AttributeActionListener(listener, AttributeAction.SET)
    );
  }

  @Override
  public void onRemoveOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty(
        "option-remove",
        EventTypes.MODIFY_OPTION,
        listener == null ? null : new AttributeActionListener(listener, AttributeAction.REMOVE)
    );
  }

  @Override
  public void onAddOption(@Nullable EventListener.Typed<AttributeMutateEvent> listener) {
    setListenerProperty(
        "option-add",
        EventTypes.MODIFY_OPTION,
        listener == null ? null : new AttributeActionListener(listener, AttributeAction.ADD)
    );
  }

  @Override
  public void onInput(@Nullable EventListener.Typed<InputEvent> listener) {
    setListenerProperty("input", EventTypes.INPUT, listener);
  }

  @Override
  public void onLoaded(@Nullable EventListener listener) {
    setListenerProperty("loaded", EventTypes.DOM_LOADED, listener);
  }

  @Override
  public void onSpawned(@Nullable EventListener listener) {
    setListenerProperty("spawned", EventTypes.DOM_SPAWNED, listener);
  }

  @Override
  public void onClosing(@Nullable EventListener listener) {
    setListenerProperty("closing", EventTypes.DOM_CLOSING, listener);
  }

  private <T extends EventListener> T getListenerProperty(String name) {
    ListenerProperty listenerProperty = propertyMap.get(name);
    if (listenerProperty == null) {
      return null;
    }

    return (T) listenerProperty.listener;
  }

  @Override
  public @Nullable EventListener.Typed<MouseEvent> getOnClick() {
    return getListenerProperty("left-click");
  }

  @Override
  public @Nullable EventListener.Typed<MouseEvent> getOnRightClick() {
    return getListenerProperty("right-click");
  }

  @Override
  public @Nullable EventListener.Typed<MouseEvent> getOnMouseEnter() {
    return getListenerProperty("mouse-enter");
  }

  @Override
  public @Nullable EventListener.Typed<MouseEvent> getOnMouseExit() {
    return getListenerProperty("mouse-exit");
  }

  @Override
  public @Nullable EventListener.Typed<MouseEvent> getOnMouseMove() {
    return getListenerProperty("mouse-move");
  }

  @Override
  public @Nullable EventListener.Typed<MutationEvent> getOnAppendChild() {
    return getListenerProperty("append-child");
  }

  @Override
  public @Nullable EventListener.Typed<MutationEvent> getOnRemoveChild() {
    return getListenerProperty("remove-child");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnAttributeChange() {
    return getListenerProperty("attr-change");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnSetAttribute() {
    return getListenerProperty("attr-set");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnRemoveAttribute() {
    return getListenerProperty("attr-remove");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnAddAttribute() {
    return getListenerProperty("attr-add");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnOptionChange() {
    return getListenerProperty("option-change");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnSetOption() {
    return getListenerProperty("option-set");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnRemoveOption() {
    return getListenerProperty("option-remove");
  }

  @Override
  public @Nullable EventListener.Typed<AttributeMutateEvent> getOnAddOption() {
    return getListenerProperty("option-add");
  }

  @Override
  public @Nullable EventListener.Typed<InputEvent> getOnInput() {
    return getListenerProperty("input");
  }

  @Override
  public @Nullable EventListener.Typed<InputEvent> getOnLoaded() {
    return getListenerProperty("loaded");
  }

  @Override
  public @Nullable EventListener getOnSpawned() {
    return getListenerProperty("spawned");
  }

  @Override
  public @Nullable EventListener getOnClosing() {
    return getListenerProperty("closing");
  }

  record ListenerProperty(String eventType, EventListener listener) {

  }
}
