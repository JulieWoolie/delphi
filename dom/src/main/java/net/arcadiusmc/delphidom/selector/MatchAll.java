package net.arcadiusmc.delphidom.selector;

import java.util.List;
import net.arcadiusmc.delphidom.DelphiElement;

final class MatchAll implements SelectorFunction {

  @Override
  public boolean test(DelphiElement element) {
    return true;
  }

  @Override
  public void appendDebug(StringBuilder builder) {
    builder.append("    <match-all />");
  }

  @Override
  public List<DelphiElement> selectNext(List<DelphiElement> elements) {
    return elements;
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append("*");
  }

  @Override
  public void appendSpec(Spec spec) {

  }
}
