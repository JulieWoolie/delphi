package net.arcadiusmc.delphi.dom.scss;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import net.arcadiusmc.delphi.Screen;

@Getter
public class Property<T> {

  private final T defaultValue;
  private final Class<T> type;
  private final boolean cascading;
  private final int dirtyBits;
  private final StyleFunction<T> applicator;

  int id;
  String key;

  @Builder
  public Property(
      Class<T> type,
      T defaultValue,
      boolean layoutAffecting,
      boolean contentAffecting,
      boolean cascading,
      StyleFunction<T> function
  ) {
    Objects.requireNonNull(type, "Null type");
    Objects.requireNonNull(defaultValue, "Null default value");

    this.type = type;
    this.defaultValue = defaultValue;
    this.cascading = cascading;
    this.applicator = function;

    int db = DirtyBit.VISUAL.mask;

    if (layoutAffecting) {
      db |= DirtyBit.LAYOUT.mask;
    }
    if (contentAffecting) {
      db |= DirtyBit.CONTENT.mask;
    }

    this.dirtyBits = db;

    this.id = -1;
    this.key = null;
  }

  public static <T> PropertyBuilder<T> builder(Class<T> type) {
    PropertyBuilder<T> builder = new PropertyBuilder<>();
    builder.type = type;
    return builder;
  }

  public interface StyleFunction<T> {
    void apply(ComputedStyle s, Screen screen, T t);
  }
}
