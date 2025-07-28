package com.juliewoolie.chimera.selector;

import com.google.common.base.Strings;
import com.juliewoolie.chimera.StringUtil;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Element;

public record ClassNameSelector(String className) implements Selector {

  @Override
  public boolean test(Element element) {
    String classList = element.getAttribute(Attributes.CLASS);
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
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }

  @Override
  public String getCssString() {
    return toString();
  }

  @Override
  public String toString() {
    return "." + className;
  }
}
