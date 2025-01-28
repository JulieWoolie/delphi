package net.arcadiusmc.chimera;

import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;

public record ValueOrAuto(Primitive primitive) {

  public static final ValueOrAuto ZERO = new ValueOrAuto(Primitive.ZERO);
  public static final ValueOrAuto ONE = new ValueOrAuto(Primitive.ONE);

  public static final ValueOrAuto AUTO = new ValueOrAuto(null);

  public static ValueOrAuto valueOf(Primitive primitive) {
    if (primitive == null) {
      return AUTO;
    }
    if (primitive.getUnit() == Unit.NONE) {
      if (primitive.getValue() == 0) {
        return ZERO;
      }
      if (primitive.getValue() == 1) {
        return ONE;
      }
    }

    return new ValueOrAuto(primitive);
  }

  public boolean isAuto() {
    return primitive == null;
  }

  public boolean isPrimitive() {
    return primitive != null;
  }

  @Override
  public String toString() {
    if (isAuto()) {
      return "AUTO";
    }

    return primitive.toString();
  }
}
