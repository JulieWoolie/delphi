package net.arcadiusmc.chimera.function;

import java.util.Objects;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.parse.Location;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.ArrayUtils;

@Getter @Setter
public class Argument {

  private Object value;
  private int argumentIndex;
  private Location start;
  private Location end;
  private CompilerErrors errors;

  public String string() {
    return Objects.toString(value);
  }

  public @Nullable Color color() {
    if (value instanceof Color color) {
      return color;
    }

    if (value instanceof String str) {
      Color named = NamedColor.named(str);

      if (named != null) {
        return named;
      }
    }

    errors.error(start, "Expected argument %s to be a color value", argumentIndex);
    return null;
  }

  public Primitive primitive(Unit... allowedUnits) {
    return coerceToPrimitive(allowedUnits);
  }

  public @Nullable Primitive primitive(float minimumValue, Unit... allowedUnits) {
    Primitive prim = coerceToPrimitive(allowedUnits);
    if (prim == null) {
      return null;
    }

    if (prim.getValue() < minimumValue) {
      cannotBeLessThan(minimumValue);
      return Primitive.create(minimumValue, prim.getUnit());
    }

    return prim;
  }

  public void cannotBeLessThan(float value) {
    errors.warn(start, "Argument %s cannot be less than %s", argumentIndex, value);
  }

  public void cannotBeMoreThan(float value) {
    errors.warn(start, "Argument %s cannot be greater than %s", argumentIndex, value);
  }

  private Primitive coerceToPrimitive(Unit... allowedUnits) {
    if (!(value instanceof Primitive prim)) {
      if (value instanceof Number num) {
        if (allowedUnits.length < 1 || ArrayUtils.contains(allowedUnits, Unit.NONE)) {
          return Primitive.create(num.floatValue(), Unit.NONE);
        }
        if (allowedUnits.length == 1) {
          return Primitive.create(num.floatValue(), allowedUnits[0]);
        }
      }

      errors.error(start, "Expected argument %s to be a numeric value", argumentIndex);
      return null;
    }

    if (allowedUnits.length < 1) {
      return prim;
    }

    for (Unit allowedUnit : allowedUnits) {
      if (prim.getUnit() == allowedUnit) {
        return prim;
      }
    }

    errors.error(start, "Unit %s not allowed here", prim.getUnit().name().toLowerCase());
    return null;
  }

  public ScssInvocationException error(String format, Object... args) {
    String message = String.format(format, args);
    return new ScssInvocationException(message, start, argumentIndex);
  }

  public boolean bool() {
    Boolean b = Chimera.coerceValue(Boolean.class, value);
    return b != null && b;
  }
}
