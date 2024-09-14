package net.arcadiusmc.chimera.function;

import static net.arcadiusmc.chimera.parse.Interpreter.postEval;
import static net.arcadiusmc.chimera.parse.Interpreter.preEvalTranslate;
import static net.arcadiusmc.chimera.parse.Interpreter.testCompatibility;

import java.util.function.DoubleBinaryOperator;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;

public class MathBiFunction implements ScssFunction {

  private final DoubleBinaryOperator operator;

  public MathBiFunction(DoubleBinaryOperator operator) {
    this.operator = operator;
  }

  @Override
  public Object invoke(ChimeraContext ctx, Argument[] arguments) {
    Primitive lhs = arguments[0].primitive();
    Primitive rhs = arguments[1].primitive();

    if (lhs == null || rhs == null) {
      return null;
    }

    Unit left = lhs.getUnit();
    Unit right = rhs.getUnit();

    if (!testCompatibility(ctx.getErrors(), arguments[0].getLocation(), left, right)) {
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
