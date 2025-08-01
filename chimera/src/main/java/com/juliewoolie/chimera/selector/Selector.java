package com.juliewoolie.chimera.selector;

import java.util.function.Predicate;
import com.juliewoolie.dom.Element;

public interface Selector extends Predicate<Element> {

  Selector MATCH_ALL = MatchAll.MATCH_ALL;

  boolean test(Element element);

  void append(StringBuilder builder);

  void appendSpec(Spec spec);

  default String getCssString() {
    StringBuilder builder = new StringBuilder();
    append(builder);
    return builder.toString();
  }
}
