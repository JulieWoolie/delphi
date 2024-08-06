package net.arcadiusmc.delphidom.selector;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.arcadiusmc.delphidom.DelphiElement;

@Getter
public class SelectorNode {

  final SelectorFunction[] functions;
  final Combinator combinator;

  public SelectorNode(Combinator combinator, SelectorFunction[] functions) {
    this.functions = functions;
    this.combinator = combinator;

    Preconditions.checkArgument(combinator != null, "Null combinator");
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

  public void append(StringBuilder builder, boolean appendCombinator) {
    for (SelectorFunction function : functions) {
      function.append(builder);
    }

    if (appendCombinator) {
      combinator.append(builder);
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
    append(builder, false);
    return builder.toString();
  }
}
