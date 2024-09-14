package net.arcadiusmc.chimera;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class PropertySet {

  private Value[] values;

  private boolean isEmpty() {
    if (values == null || values.length < 1) {
      return true;
    }

    for (Object ruleValue : values) {
      if (ruleValue == null) {
        continue;
      }

      return false;
    }

    return true;
  }

  public int setAll(PropertySet from) {
    int maxProperty = Math.max(this.length(), from.length());

    if (maxProperty < 1) {
      return 0;
    }

    int dirtyBits = 0;

    for (int i = 0; i < maxProperty; i++) {
      Property<Object> property = Properties.getById(i);
      Value<Object> fromValue = from.orNull(property);

      if (!setValue(property, fromValue)) {
        continue;
      }

      dirtyBits |= property.getDirtyBits();
    }

    return dirtyBits;
  }

  public int putAll(PropertySet from) {
    PropertyIterator it = from.iterator();
    int dirtyBits = 0;

    while (it.hasNext()) {
      it.next();
      if (!setValue(it.property(), it.value())) {
        continue;
      }

      dirtyBits |= it.property().getDirtyBits();
    }

    return dirtyBits;
  }

  public <T> boolean has(Property<T> property) {
    if (values == null || values.length <= property.id) {
      return false;
    }

    T v = (T) values[property.id];
    return v != null;
  }

  public <T> T getValue(Property<T> property) {
    if (!has(property)) {
      return property.getDefaultValue();
    }

    Value<T> val = values[property.id];
    return val.getValue();
  }

  public <T> Value<T> get(Property<T> property) {
    return orElse(property, property.getDefaultValue());
  }

  public <T> Value<T> orNull(Property<T> property) {
    return orElse(property, null);
  }

  public <T> Value<T> orElse(Property<T> property, T fallback) {
    if (!has(property)) {
      if (fallback == null) {
        return null;
      }

      return Value.create(fallback);
    }

    return (Value<T>) values[property.id];
  }

  public <T> boolean set(Property<T> property, @Nullable T value) {
    Value<T> scssValue = Value.create(value);
    return setValue(property, scssValue);
  }

  public <T> boolean setValue(Property<T> property, @Nullable Value<T> value) {
    Objects.requireNonNull(property, "Null rule");

    int id = property.id;

    if (value == null) {
      return remove(property);
    }

    if (values == null) {
      values = new Value[id + 1];
      values[id] = value;
      return true;
    }

    values = ObjectArrays.ensureCapacity(values, id + 1);

    Object current = values[id];
    values[id] = value;

    return !Objects.equals(value, current);
  }

  public <T> boolean remove(Property<T> property) {
    if (!has(property)) {
      return false;
    }

    values[property.id] = null;
    return true;
  }

  public String toParseString() {
    PropertyIterator it = iterator();

    if (!it.hasNext()) {
      return "";
    }

    StringBuilder builder = new StringBuilder();

    while (it.hasNext()) {
      it.next();

      Property<Object> prop = it.property();
      Value<Object> val = it.value();

      builder
          .append(prop.getKey())
          .append(": ")
          .append(val.getTextValue()).append(";");

      if (it.hasNext()) {
        builder.append(" ");
      }
    }

    return builder.toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(getClass().getSimpleName());
    builder.append("[");

    if (values == null || values.length < 1) {
      builder.append("]");
      return builder.toString();
    }

    boolean anyPrinted = false;

    for (int i = 0; i < values.length; i++) {
      Value<Object> v = values[i];

      if (v == null) {
        continue;
      }

      Property<Object> r = Properties.getById(i);

      if (r == null) {
        continue;
      }

      if (anyPrinted) {
        builder.append(", ");
      }

      builder.append(r.key)
          .append("=")
          .append(v.getTextValue());

      anyPrinted = true;
    }

    builder.append("]");
    return builder.toString();
  }

  public void clear() {
    if (values == null || values.length < 1) {
      return;
    }

    Arrays.fill(values, null);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof PropertySet rset)) {
      return false;
    }

    if (isEmpty()) {
      return rset.isEmpty();
    }

    if (rset.isEmpty()) {
      return false;
    }

    int slen = this.length();
    int olen = rset.length();
    int max = Math.max(slen, olen);

    for (int i = 0; i < max; i++) {
      Object self = this.safeGet(i);
      Object other = rset.safeGet(i);

      if (!Objects.equals(self, other)) {
        return false;
      }
    }

    return true;
  }

  private int length() {
    return values == null ? 0 : values.length;
  }

  private Object safeGet(int i) {
    if (values == null) {
      return null;
    }
    if (i < 0 || i >= values.length) {
      return null;
    }
    return values[i];
  }

  public PropertyIterator iterator() {
    return new PropertyIterator();
  }

  public class PropertyIterator {

    int index = 0;
    int currentId = -1;

    public boolean hasNext() {
      if (values == null) {
        return false;
      }

      while (index < values.length) {
        Object o = values[index];

        if (o == null) {
          index++;
          continue;
        }

        return true;
      }

      return false;
    }

    public void next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      currentId = index;
      index++;
    }

    private void ensureCurrent() {
      if (currentId >= 0) {
        return;
      }

      throw new IllegalStateException("next() has not been called");
    }

    public Value<Object> value() {
      ensureCurrent();
      return values[currentId];
    }

    public Property<Object> property() {
      ensureCurrent();
      return Properties.getById(currentId);
    }
  }
}
