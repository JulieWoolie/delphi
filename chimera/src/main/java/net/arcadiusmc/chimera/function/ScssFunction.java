package net.arcadiusmc.chimera.function;

import net.arcadiusmc.chimera.ChimeraContext;
import org.apache.commons.lang3.Range;

public interface ScssFunction {

  Object invoke(ChimeraContext ctx, Argument[] arguments) throws ScssInvocationException;

  default Range<Integer> argumentCount() {
    return Range.between(0, Integer.MAX_VALUE);
  }
}
