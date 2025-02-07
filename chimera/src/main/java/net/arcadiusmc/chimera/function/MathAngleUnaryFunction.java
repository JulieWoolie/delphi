package net.arcadiusmc.chimera.function;

import java.util.function.DoubleUnaryOperator;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Interpreter;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;

public class MathAngleUnaryFunction implements ScssFunction {

  private final DoubleUnaryOperator operator;

  public MathAngleUnaryFunction(DoubleUnaryOperator operator) {
    this.operator = operator;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) {
    Primitive prim = arguments[0].primitive();
    if (prim == null) {
      return Primitive.NAN;
    }

    float value = prim.getValue();
    if (value == Float.NEGATIVE_INFINITY || value == Float.POSITIVE_INFINITY) {
      return Primitive.NAN;
    }

    double in;
    Unit outUnit;

    if (Interpreter.isAngular(prim.getUnit())) {
      in = Math.toRadians(prim.toDegrees());
      outUnit = Unit.RAD;
    } else {
      in = prim.getValue();
      outUnit = prim.getUnit();
    }

    double out = operator.applyAsDouble(in);

    return Primitive.create((float) out, outUnit);
  }

  @Override
  public Range<Integer> argumentCount() {
    return Range.is(1);
  }
}
