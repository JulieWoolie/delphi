package com.juliewoolie.chimera.function;

import com.juliewoolie.chimera.parse.ChimeraContext;
import com.juliewoolie.chimera.parse.Scope;
import org.apache.commons.lang3.Range;

public class IfFunction implements ScssFunction {

  @Override
  public Range<Integer> argumentCount() {
    return Range.is(3);
  }

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments)
      throws ScssInvocationException
  {
    Object o1 = arguments[1].getValue();
    Object o2 = arguments[2].getValue();
    boolean condition = arguments[0].bool();
    return condition ? o1 : o2;
  }
}
