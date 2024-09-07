package net.arcadiusmc.chimera.parse;

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

  private final StringBuffer input;

  public ChimeraContext(StringBuffer input) {
    this.input = input;

    functions.put("rgb", ScssFunctions.RGB);
    functions.put("rgba", ScssFunctions.RGBA);
    functions.put("lighten", ScssFunctions.BRIGHTEN);
    functions.put("darken", ScssFunctions.DARKEN);
  }

  public String getInput(Location location, int len) {
    int idx = location.cursor();
    return input.substring(idx, idx + len);
  }

  public String getInput(Location start, Location end) {
    return input.substring(start.cursor(), end.cursor());
  }
}
