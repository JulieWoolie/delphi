package net.arcadiusmc.delphi.dom.selector;

import net.arcadiusmc.delphi.StringUtil;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.dom.Attr;

public record ClassNameFunction(String className) implements FilteringFunction {

  @Override
  public boolean test(DelphiElement element) {
    String classList = element.getAttribute(Attr.CLASS);
    return StringUtil.containsWord(classList, className);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append('.').append(className);
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }
}
