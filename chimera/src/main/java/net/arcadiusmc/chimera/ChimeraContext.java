package net.arcadiusmc.chimera;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.function.ScssFunction;
import net.arcadiusmc.chimera.function.ScssFunctions;

@Getter @Setter
public class ChimeraContext {

  private final Map<String, ScssFunction> functions = new HashMap<>();
  private Map<String, Object> variables = new HashMap<>();
  private CompilerErrors errors;

  public ChimeraContext() {
    functions.put("rgb", ScssFunctions.RGB);
    functions.put("rgba", ScssFunctions.RGBA);
  }
}
