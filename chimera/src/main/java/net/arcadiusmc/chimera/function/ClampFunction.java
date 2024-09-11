package net.arcadiusmc.chimera.function;

import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.ast.BinaryExpr;
import net.arcadiusmc.dom.style.Primitive;
import org.apache.commons.lang3.Range;

public class ClampFunction implements ScssFunction {

  @Override
  public Object invoke(ChimeraContext ctx, Argument[] arguments) {
    Primitive minPrim = arguments[0].primitive();
    Primitive valPrim = arguments[1].primitive();
    Primitive maxPrim = arguments[2].primitive();

    if (minPrim == null || maxPrim == null || valPrim == null) {
      return null;
    }

    float val = BinaryExpr.preEvalTranslate(valPrim);
    float min = BinaryExpr.preEvalTranslate(minPrim);
    float max = BinaryExpr.preEvalTranslate(maxPrim);

    float clamped = Math.clamp(val, min, max);

    return BinaryExpr.postEval(clamped, valPrim.getUnit(), valPrim.getUnit());
  }

  @Override
  public Range<Integer> argumentCount() {
    return Range.is(3);
  }
}
