package net.arcadiusmc.dom.style;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

final class PrimitiveImpl implements Primitive {

  private final float value;
  private final Unit unit;

  public PrimitiveImpl(float value, Unit unit) {
    this.value = value;
    this.unit = Objects.requireNonNullElse(unit, Unit.NONE);
  }

  @Override
  public float getValue() {
    return value;
  }

  @Override
  public @NotNull Unit getUnit() {
    return unit;
  }

  @Override
  public boolean isZero() {
    return value == 0.0f;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PrimitiveImpl primitive)) {
      return false;
    }
    return Float.compare(value, primitive.value) == 0 && unit == primitive.unit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, unit);
  }

  @Override
  public String toString() {
    return value + unit.getUnit();
  }
}
