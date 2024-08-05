package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;

public interface PseudoFunc<T> {

  boolean test(DelphiElement root, DelphiElement el, T value);

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
