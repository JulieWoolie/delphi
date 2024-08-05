package net.arcadiusmc.delphidom.selector;

import com.google.common.base.Preconditions;
import net.arcadiusmc.delphidom.DelphiElement;

public class SelectorNode {

  final SelectorFunction[] functions;

  public SelectorNode(SelectorFunction[] functions) {
    this.functions = functions;

    Preconditions.checkArgument(functions != null, "Null functions array");
    Preconditions.checkArgument(functions.length >= 1, "At least 1 function required");
  }

  public boolean test(DelphiElement root, DelphiElement el) {
    for (SelectorFunction function : functions) {
      if (!function.test(root, el)) {
        return false;
      }
    }

    return true;
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    append(builder);
    return builder.toString();
  }
}
