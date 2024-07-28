package net.arcadiusmc.delphidom.selector;

import com.google.common.base.Strings;
import net.arcadiusmc.delphidom.StringUtil;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.Attr;

public record ClassNameFunction(String className) implements FilteringFunction {

  @Override
  public boolean test(DelphiElement element) {
    String classList = element.getAttribute(Attr.CLASS);
    if (Strings.isNullOrEmpty(classList)) {
      return false;
    }
    return StringUtil.containsWord(classList, className);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append('.').append(className);
  }

  @Override
  public void appendDebug(StringBuilder builder) {
    builder.append("    <classname value=").append('"').append(className).append('"').append(" />");
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }
}
