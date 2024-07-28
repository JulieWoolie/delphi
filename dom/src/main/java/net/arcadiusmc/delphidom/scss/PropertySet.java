package net.arcadiusmc.delphidom.scss;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nullable;

public final class PropertySet {

  private Object[] values;

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

  public int putAll(PropertySet from) {
    RuleIterator it = from.iterator();
    int dirtyBits = 0;

    while (it.hasNext()) {
      it.next();
      if (!set(it.property(), it.value())) {
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

  public <T> T get(Property<T> property) {
    if (!has(property)) {
      return property.getDefaultValue();
    }

    return (T) values[property.id];
  }

  public <T> boolean set(Property<T> property, @Nullable T value) {
    Objects.requireNonNull(property, "Null rule");

    int id = property.id;

    if (value == null) {
      return remove(property);
    }

    if (values == null) {
      values = new Object[id + 1];
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
    RuleIterator it = iterator();

    if (!it.hasNext()) {
      return "";
    }

    StringBuilder builder = new StringBuilder();

    while (it.hasNext()) {
      it.next();

      Property<Object> prop = it.property();
      Object val = it.value();

      builder.append(prop.getKey()).append(": ");

      if (val instanceof Enum<?> e) {
        builder.append(e.name().toLowerCase().replace("_", "-"));
      } else {
        builder.append(val);
      }

      builder.append(";");

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
      Object v = values[i];

      if (v == null) {
        continue;
      }

      Property r = Properties.getById(i);

      if (r == null) {
        continue;
      }

      if (anyPrinted) {
        builder.append(", ");
      }

      builder.append(r.key)
          .append("=")
          .append(v);

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

  public RuleIterator iterator() {
    return new RuleIterator();
  }

  public DifferenceIterator difference(PropertySet other) {
    return new DifferenceIterator(this, other);
  }

  public static class DifferenceIterator {

    private int idx = 0;
    private Property<Object> property;
    private Object self;
    private Object other;

    final PropertySet selfSet;
    final PropertySet otherSet;

    public DifferenceIterator(PropertySet selfSet, PropertySet otherSet) {
      this.selfSet = selfSet;
      this.otherSet = otherSet;
    }

    public boolean hasNext() {
      if (idx >= Properties.count()) {
        return false;
      }

      while (idx < Properties.count()) {
        Property<Object> r = Properties.getById(idx);

        if (r == null) {
          idx++;
          continue;
        }

        Object self = selfSet.get(r);
        Object other = otherSet.get(r);

        if (Objects.equals(self, other)) {
          idx++;
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

      property = Properties.getById(idx);
      self = selfSet.get(property);
      other = otherSet.get(property);

      idx++;
    }

    private void ensureSet() {
      if (property == null) {
        throw new IllegalStateException("next() has not been called once");
      }
    }

    public Property<Object> rule() {
      ensureSet();
      return property;
    }

    public Object selfValue() {
      ensureSet();
      return self;
    }

    public Object otherValue() {
      ensureSet();
      return other;
    }
  }

  public class RuleIterator {

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

    public Object value() {
      ensureCurrent();
      return values[currentId];
    }

    public Property<Object> property() {
      ensureCurrent();
      return Properties.getById(currentId);
    }
  }
}
