package net.arcadiusmc.delphidom.selector;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;

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
    List<DelphiElement> result = new ArrayList<>();

    for (DelphiNode delphiNode : el.childList()) {
      if (delphiNode instanceof DelphiElement e) {
        result.add(e);
      }
    }

    for (SelectorFunction function : functions) {
      result = function.selectNext(result);
    }

    return result;
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
