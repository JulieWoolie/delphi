package com.juliewoolie.chimera.function;

import static com.juliewoolie.dom.style.Color.MAX_VALUE;
import static com.juliewoolie.dom.style.Color.MIN_VALUE;

import com.juliewoolie.chimera.parse.ChimeraContext;
import com.juliewoolie.chimera.parse.Scope;
import com.juliewoolie.dom.style.Color;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;

public class RgbFunction implements ScssFunction {

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) {
    int red = toChannelValue(arguments[0]);
    int green = toChannelValue(arguments[1]);
    int blue = toChannelValue(arguments[2]);

    int alpha;

    if (arguments.length > 3) {
      Primitive a = arguments[3].primitive(0, Unit.NONE);

      if (a == null) {
        alpha = MAX_VALUE;
      } else {
        float mod = a.getValue();

        if (mod > 1.0f) {
          arguments[3].cannotBeMoreThan(1.0f);
          mod = 1.0f;
        }

        alpha = (int) (MAX_VALUE * mod);
      }
    } else {
      alpha = MAX_VALUE;
    }

    return Color.argb(alpha, red, green, blue);
  }

  private int toChannelValue(Argument argument) {
    Primitive primitive = argument.primitive(0, Unit.NONE, Unit.PERCENT);

    if (primitive == null) {
      return MIN_VALUE;
    }

    if (primitive.getUnit() == Unit.NONE) {
      int v = (int) primitive.getValue();

      if (v > MAX_VALUE) {
        argument.cannotBeMoreThan(MAX_VALUE);
      }

      return Math.clamp(v, MIN_VALUE, MAX_VALUE);
    }

    float mod = primitive.getValue();
    if (mod > 100) {
      argument.cannotBeMoreThan(100.0f);
    }

    return Math.clamp((int) (MAX_VALUE * (mod * 0.01f)), MIN_VALUE, MAX_VALUE);
  }

  @Override
  public Range<Integer> argumentCount() {
    return Range.between(3, 4);
  }
}
