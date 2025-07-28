package com.juliewoolie.chimera.function;

import com.juliewoolie.chimera.parse.ChimeraContext;
import com.juliewoolie.chimera.parse.Scope;
import org.apache.commons.lang3.Range;

public interface ScssFunction {

  Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) throws ScssInvocationException;

  default Range<Integer> argumentCount() {
    return Range.between(0, Integer.MAX_VALUE);
  }
}
