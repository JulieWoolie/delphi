package com.juliewoolie.chimera.selector;

import com.juliewoolie.dom.Element;

enum MatchAll implements Selector {
  MATCH_ALL,
  ;

  @Override
  public boolean test(Element element) {
    return true;
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append('*');
  }

  @Override
  public void appendSpec(Spec spec) {

  }

  @Override
  public String getCssString() {
    return "*";
  }

  @Override
  public String toString() {
    return "*";
  }
}
