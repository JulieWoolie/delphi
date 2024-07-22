package net.arcadiusmc.delphi.dom.selector;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphi.dom.DelphiElement;

public class SelectorNode {

  final SelectorFunction[] functions;

  public SelectorNode(SelectorFunction[] functions) {
    this.functions = functions;

    Preconditions.checkArgument(functions != null, "Null functions array");
    Preconditions.checkArgument(functions.length >= 1, "At least 1 function required");
  }

  public boolean test(DelphiElement el) {
    for (SelectorFunction function : functions) {
      if (!function.test(el)) {
        return false;
      }
    }

    return true;
  }

  public List<DelphiElement> select(DelphiElement el) {
    List<DelphiElement> nodes = new ArrayList<>();
    nodes.addAll(functions[0].selectNext(el));

    if (functions.length > 1) {
      for (int i = 1; i < functions.length; i++) {
        SelectorFunction func = functions[i];
        nodes.removeIf(delphiNode -> !func.test(delphiNode));
      }
    }

    return nodes;
  }

  public void append(StringBuilder builder) {
    for (SelectorFunction function : functions) {
      function.append(builder);
    }
  }

  public void appendSpec(Spec spec) {
    for (SelectorFunction function : functions) {
      function.appendSpec(spec);
    }
  }
}
