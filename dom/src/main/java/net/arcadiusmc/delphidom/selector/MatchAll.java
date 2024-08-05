package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;

final class MatchAll implements SelectorFunction {

  @Override
  public boolean test(DelphiElement root, DelphiElement element) {
    return true;
  }

  @Override
  public void appendDebug(StringBuilder builder) {
    builder.append("    <match-all />");
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append("*");
  }

  @Override
  public void appendSpec(Spec spec) {

  }
}
