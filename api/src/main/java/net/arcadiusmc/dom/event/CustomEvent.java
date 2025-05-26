package net.arcadiusmc.dom.event;

import java.util.Map;
import java.util.Set;
import net.arcadiusmc.dom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom DOM event
 */
public interface CustomEvent extends Event {

  /**
   * Get a property value
   *
   * @param propertyName Property name
   * @return Property value, or {@code null}, if the property isn't set
   *
   * @throws NullPointerException If {@code propertyName} is {@code null}
   */
  @Nullable Object getProperty(@NotNull String propertyName);

  /**
   * Get a property value and cast it to a specified {@code type}
   *
   * @param propertyName Property name
   * @param type Property type
   *
   * @return Property value casted to the specified type, or {@code null}, if the property isn't
   *         set, or if the property's type wasn't an instance of the specified type.
   *
   * @throws NullPointerException If {@code propertyName} or {@code type} is {@code null}
   */
  @Nullable <T> T getPropertyAs(@NotNull String propertyName, Class<T> type);

  /**
   * Get a set of all property names
   * @return Property name set
   */
  @NotNull Set<String> getProperties();

  /**
   * Initializes the event, this <b>must be called</b> before dispatching the event.
   *
   * @param target Target element
   * @param bubbles Whether the event bubbles up through the document tree
   * @param cancellable Whether the event can be cancelled
   * @param properties Map of event properties, or {@code null}, if no extra
   *                   properties are required.
   */
  void initEvent(
      @Nullable Element target,
      boolean bubbles,
      boolean cancellable,
      @Nullable Map<String, Object> properties
  );
}
