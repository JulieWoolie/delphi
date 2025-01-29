package net.arcadiusmc.chimera;

import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class Property<T> {

  private final Class<T> type;
  private final T defaultValue;
  private final boolean cascading;
  private final int dirtyBits;
  private final PropertyValidator<T> validator;

  int id = -1;
  String key;

  @Builder
  public Property(
      Class<T> type,
      T defaultValue,
      boolean layoutAffecting,
      boolean contentAffecting,
      boolean visualAffecting,
      boolean cascading,
      PropertyValidator<T> validator
  ) {
    Objects.requireNonNull(type, "Null type");
    Objects.requireNonNull(defaultValue, "Null default value");

    this.type = type;
    this.defaultValue = defaultValue;
    this.cascading = cascading;
    this.validator = validator;

    int m = 0;

    if (layoutAffecting) {
      m |= DirtyBit.LAYOUT.mask;
    }
    if (contentAffecting) {
      m |= DirtyBit.CONTENT.mask;
    }
    if (visualAffecting) {
      m |= DirtyBit.VISUAL.mask;
    }

    this.dirtyBits = m;
  }

  public static <T> PropertyBuilder<T> builder(Class<T> type) {
    PropertyBuilder<T> builder = new PropertyBuilder<>();
    builder.type(type);
    return builder;
  }

  public Optional<String> validateValue(T value) {
    if (validator == null) {
      return Optional.empty();
    }

    return validator.validate(value);
  }
}
