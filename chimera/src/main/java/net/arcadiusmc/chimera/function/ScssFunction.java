package net.arcadiusmc.chimera.function;

import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import org.apache.commons.lang3.Range;

public interface ScssFunction {

  Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments) throws ScssInvocationException;

  default Range<Integer> argumentCount() {
    return Range.between(0, Integer.MAX_VALUE);
  }
}
