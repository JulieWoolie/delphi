package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.selector.PseudoFunc.SelectorPseudoFunc;

public interface PseudoFunctions {

  PseudoFunc<Integer> NTH_CHILD = new PseudoFunc<>() {
    @Override
    public boolean test(DelphiElement root, DelphiElement el, Integer value) {
      DelphiElement parent = el.getParent();
      if (parent == null) {
        return false;
      }

      if (value < 0) {
        value = parent.getChildCount() + value;
      }

      return el.getSiblingIndex() == value;
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("nth-child");
    }
  };

  PseudoFunc<SelectorGroup> IS = new SelectorPseudoFunc() {
    @Override
    public boolean test(DelphiElement root, DelphiElement el, SelectorGroup value) {
      return value.test(root, el);
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("is");
    }
  };

  PseudoFunc<SelectorGroup> NOT = new SelectorPseudoFunc() {
    @Override
    public boolean test(DelphiElement root, DelphiElement el, SelectorGroup value) {
      return !value.test(root, el);
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("not");
    }
  };

}
