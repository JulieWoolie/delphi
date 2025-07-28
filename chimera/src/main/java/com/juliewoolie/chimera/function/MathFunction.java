package com.juliewoolie.chimera.function;

import java.util.function.DoubleUnaryOperator;
import com.juliewoolie.chimera.parse.ChimeraContext;
import com.juliewoolie.chimera.parse.Scope;
import com.juliewoolie.dom.style.Primitive;
import org.apache.commons.lang3.Range;

public class MathFunction implements ScssFunction {

  private final DoubleUnaryOperator operator;

  public MathFunction(DoubleUnaryOperator operator) {
    this.operator = operator;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) {
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
