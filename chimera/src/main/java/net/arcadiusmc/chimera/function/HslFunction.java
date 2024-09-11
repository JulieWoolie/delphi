package net.arcadiusmc.chimera.function;

import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;

public class HslFunction implements ScssFunction {

  static final float MAX_DEG = Primitive.DEGREES_IN_CIRCLE;

  private final boolean alphaAllowed;

  public HslFunction(boolean alphaAllowed) {
    this.alphaAllowed = alphaAllowed;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Argument[] arguments) {
    float h = getHue(arguments[0]);
    float s = getChannelValue(arguments[1]);
    float v = getChannelValue(arguments[2]);
    float a = getAlpha(arguments);

    return Color.hsva(h, s, v, a);
  }

  private float getAlpha(Argument[] arguments) {
    if (!alphaAllowed) {
      return 1.0f;
    }

    Argument arg = arguments[3];
    Primitive prim = arg.primitive(0f, Unit.NONE);

    if (prim == null) {
      return 1.0f;
    }

    if (prim.getValue() > 1) {
      arg.cannotBeMoreThan(1f);
      return 1f;
    }

    return prim.getValue();
  }

  private float getHue(Argument arg) {
    Primitive prim = arg.primitive(0f, Unit.angleUnits());

    if (prim == null) {
      return 0f;
    }

    float degrees = prim.toDegrees() % MAX_DEG;
    return degrees / MAX_DEG;
  }

  private float getChannelValue(Argument arg) {
    Primitive prim = arg.primitive(0f, Unit.PERCENT);

    if (prim == null) {
      return 0f;
    }

    if (prim.getValue() > 100.0f) {
      arg.cannotBeMoreThan(100.0f);
      return 1f;
    }

    return prim.getValue() / 100.0f;
  }

  @Override
  public Range<Integer> argumentCount() {
    return alphaAllowed ? Range.is(4) : Range.is(3);
  }
}
