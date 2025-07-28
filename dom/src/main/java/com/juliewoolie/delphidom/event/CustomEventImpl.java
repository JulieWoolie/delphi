package com.juliewoolie.delphidom.event;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.event.CustomEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomEventImpl extends EventImpl implements CustomEvent {

  private Map<String, Object> properties;

  public CustomEventImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  @Override
  public @Nullable Object getProperty(@NotNull String propertyName) {
    Objects.requireNonNull(propertyName, "Null propertyName");
    return properties == null ? null : properties.get(propertyName);
  }

  @Override
  public <T> @Nullable T getPropertyAs(@NotNull String propertyName, Class<T> type) {
    Objects.requireNonNull(propertyName, "Null propertyName");
    Objects.requireNonNull(type, "Null type");

    if (properties == null) {
      return null;
    }

    Object value = properties.get(propertyName);
    if (!type.isInstance(value)) {
      return null;
    }

    return type.cast(value);
  }

  @Override
  public @NotNull Set<String> getProperties() {
    return properties == null ? Set.of() : properties.keySet();
  }

  @Override
  public void initEvent(
      @Nullable Element target,
      boolean bubbles,
      boolean cancellable,
      @Nullable Map<String, Object> properties
  ) {
    super.initEvent((DelphiElement) target, bubbles, cancellable);

    if (properties == null || properties.isEmpty()) {
      this.properties = null;
    } else {
      this.properties = ImmutableMap.copyOf(properties);
    }
  }
}
