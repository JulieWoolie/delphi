package net.arcadiusmc.chimera.function;

import java.util.function.DoubleUnaryOperator;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.dom.style.Primitive;
import org.apache.commons.lang3.Range;

public class MathFunction implements ScssFunction {

  private final DoubleUnaryOperator operator;

  public MathFunction(DoubleUnaryOperator operator) {
    this.operator = operator;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Argument[] arguments) {
    Primitive prim = arguments[0].primitive();
    if (prim == null) {
      return null;
    }
    return Primitive.create((float) operator.applyAsDouble(prim.getValue()), prim.getUnit());
  }

  @Override
  public Range<Integer> argumentCount() {
    return Range.is(1);
  }
}
