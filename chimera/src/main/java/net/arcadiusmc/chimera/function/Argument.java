package net.arcadiusmc.chimera.function;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.CompilerErrors;
import net.arcadiusmc.chimera.Location;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.ArrayUtils;

@Getter @Setter
public class Argument {

  private Object value;
  private int argumentIndex;
  private Location location;
  private CompilerErrors errors;

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

    errors.error(location, "Expected argument %s to be a color value", argumentIndex);
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
      errors.error(location, "Argument %s cannot be less than %s", argumentIndex, minimumValue);
      return Primitive.create(minimumValue, prim.getUnit());
    }

    return prim;
  }

  public void cannotBeLessThan(float value) {
    errors.error(location, "Argument %s cannot be less than %s", argumentIndex, value);
  }

  public void cannotBeMoreThan(float value) {
    errors.error(location, "Argument %s cannot be greater than %s", argumentIndex, value);
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

      errors.error(location, "Expected argument %s to be a numeric value", argumentIndex);
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

    errors.error(location, "Unit %s not allowed here", prim.getUnit().name().toLowerCase());
    return null;
  }

  public ScssInvocationException error(String format, Object... args) {
    String message = String.format(format, args);
    return new ScssInvocationException(message, location, argumentIndex);
  }
}
