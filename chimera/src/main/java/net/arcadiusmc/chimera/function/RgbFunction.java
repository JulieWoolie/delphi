package net.arcadiusmc.chimera.function;

import static net.arcadiusmc.dom.style.Color.MAX_VALUE;
import static net.arcadiusmc.dom.style.Color.MIN_VALUE;

import net.arcadiusmc.chimera.ChimeraContext;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;

public class RgbFunction implements ScssFunction {

  private final boolean alphaAllowed;

  public RgbFunction(boolean alphaAllowed) {
    this.alphaAllowed = alphaAllowed;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Argument[] arguments) {
    int red = toChannelValue(arguments[1]);
    int green = toChannelValue(arguments[2]);
    int blue = toChannelValue(arguments[3]);

    int alpha;

    if (alphaAllowed) {
      Primitive a = arguments[3].primitive(0, Unit.NONE);

      if (a == null) {
        alpha = MAX_VALUE;
      } else {
        float mod = a.getValue();

        if (mod > 1.0f) {
          arguments[3].cannotBeMoreThan(1.0f);
          mod = 1.0f;
        }

        alpha = (int) (MAX_VALUE * a.getValue());
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
    if (alphaAllowed) {
      return Range.is(4);
    }

    return Range.is(5);
  }
}
