package net.arcadiusmc.hephaestus.typemappers;

import java.util.function.Function;
import java.util.function.Predicate;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

public class EnumTypeMapper<E extends Enum<E>> implements Predicate<Value>, Function<Value, E> {

  private final Class<E> type;
  private final E[] values;

  public EnumTypeMapper(Class<E> type) {
    this.type = type;
    this.values = type.getEnumConstants();
  }

  public static <E extends Enum<E>> void addMapper(HostAccess.Builder builder, Class<E> type) {
    EnumTypeMapper<E> mapper = new EnumTypeMapper<>(type);
    builder.targetTypeMapping(Value.class, type, mapper, mapper);
  }

  @Override
  public E apply(Value value) {
    if (value.isHostObject()) {
      return value.asHostObject();
    }

    String stringValue = value.asString().toUpperCase();
    for (E e : values) {
      if (e.name().equals(stringValue)) {
        return e;
      }
    }

    return null;
  }

  @Override
  public boolean test(Value value) {
    if (value.isHostObject()) {
      Object host = value.asHostObject();
      return type.isInstance(host);
    }

    if (value.isString()) {
      String str = value.asString();

      for (E e : values) {
        if (e.name().equalsIgnoreCase(str)) {
          return true;
        }
      }
    }

    return false;
  }
}
