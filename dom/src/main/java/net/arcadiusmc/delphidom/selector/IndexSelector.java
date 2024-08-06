package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;

public interface IndexSelector {

  IndexSelector ODD = new IndexSelector() {
    @Override
    public boolean test(boolean inverted, DelphiElement root, DelphiElement el) {
      int idx = getIndex(el, inverted);
      return idx != -1 && idx % 2 != 0;
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("odd");
    }
  };

  IndexSelector EVEN = new IndexSelector() {
    @Override
    public boolean test(boolean inverted, DelphiElement root, DelphiElement el) {
      return getIndex(el, inverted) % 2 == 0;
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("even");
    }
  };

  boolean test(boolean inverted, DelphiElement root, DelphiElement el);

  void append(StringBuilder builder);

  static int getIndex(DelphiElement element, boolean inverted) {
    if (element.getParent() == null) {
      return -1;
    }

    int idx = element.getSiblingIndex() + 1;

    if (!inverted) {
      return idx;
    }

    int len = element.getParent().getChildCount() + 1;
    return len - idx;
  }

}
