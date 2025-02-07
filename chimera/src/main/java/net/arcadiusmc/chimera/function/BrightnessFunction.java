package net.arcadiusmc.chimera.function;

import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;

public class BrightnessFunction implements ScssFunction {

  static final float DEFAULT_CHANGE = 0.25f;

  private final boolean darken;

  public BrightnessFunction(boolean darken) {
    this.darken = darken;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) {
    Color color = arguments[0].color();
    if (color == null) {
      return null;
    }

    float changeModifier;

    if (arguments.length > 1) {
      Primitive prim = arguments[1].primitive(0f, Unit.NONE, Unit.PERCENT);

      if (prim == null) {
        changeModifier = DEFAULT_CHANGE;
      } else {
        if (prim.getUnit() == Unit.PERCENT) {
          changeModifier = prim.getValue() / 100.0f;
        } else {
          changeModifier = prim.getValue();
        }
      }
    } else {
      changeModifier = DEFAULT_CHANGE;
    }

    if (darken) {
      return color.darken(changeModifier);
    } else {
      return color.brighten(changeModifier);
    }
  }

  @Override
  public Range<Integer> argumentCount() {
    return Range.between(1, 2);
  }
}
