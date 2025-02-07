package net.arcadiusmc.chimera.function;

import static net.arcadiusmc.chimera.parse.Interpreter.testCompatibility;

import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.parse.Interpreter;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Primitive;
import org.apache.commons.lang3.Range;

public class ClampFunction implements ScssFunction {

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) {
    Argument a1 = arguments[0];
    Argument a2 = arguments[1];
    Argument a3 = arguments[2];

    Primitive minPrim = a1.primitive();
    Primitive valPrim = a2.primitive();
    Primitive maxPrim = a3.primitive();

    if (minPrim == null || maxPrim == null || valPrim == null) {
      return null;
    }

    CompilerErrors errors = ctx.getErrors();
    if (!testCompatibility(errors, a1.getStart(), minPrim.getUnit(), valPrim.getUnit())) {
      return null;
    }
    if (!testCompatibility(errors, a3.getStart(), maxPrim.getUnit(), valPrim.getUnit())) {
      return null;
    }

    float val = Interpreter.preEvalTranslate(valPrim);
    float min = Interpreter.preEvalTranslate(minPrim);
    float max = Interpreter.preEvalTranslate(maxPrim);

    float clamped = Math.clamp(val, min, max);

    return Interpreter.postEval(clamped, valPrim.getUnit(), valPrim.getUnit());
  }

  @Override
  public Range<Integer> argumentCount() {
    return Range.is(3);
  }
}
