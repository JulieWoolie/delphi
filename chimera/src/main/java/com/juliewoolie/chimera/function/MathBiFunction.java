package com.juliewoolie.chimera.function;

import static com.juliewoolie.chimera.parse.Interpreter.postEval;
import static com.juliewoolie.chimera.parse.Interpreter.preEvalTranslate;
import static com.juliewoolie.chimera.parse.Interpreter.testCompatibility;

import java.util.function.DoubleBinaryOperator;
import com.juliewoolie.chimera.parse.ChimeraContext;
import com.juliewoolie.chimera.parse.Scope;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;

public class MathBiFunction implements ScssFunction {

  private final DoubleBinaryOperator operator;

  public MathBiFunction(DoubleBinaryOperator operator) {
    this.operator = operator;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) {
    Primitive lhs = arguments[0].primitive();
    Primitive rhs = arguments[1].primitive();

    if (lhs == null || rhs == null) {
      return null;
    }

    Unit left = lhs.getUnit();
    Unit right = rhs.getUnit();

    if (!testCompatibility(ctx.getErrors(), arguments[0].getStart(), left, right)) {
      return null;
    }

    float lv = preEvalTranslate(lhs);
    float rv = preEvalTranslate(rhs);

    float result = (float) operator.applyAsDouble(lv, rv);

    return postEval(result, left, right);
  }

  @Override
  public Range<Integer> argumentCount() {
    return Range.is(2);
  }
}
