package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;

public interface PseudoFunc<T> {

  boolean test(Element root, Element el, T value);

  void append(StringBuilder builder);

  default void appendValue(StringBuilder builder, T value) {
    builder.append(value);
  }

  interface SelectorPseudoFunc extends PseudoFunc<SelectorGroup> {

    @Override
    default void appendValue(StringBuilder builder, SelectorGroup value) {
      value.append(builder);
    }
  }
}
