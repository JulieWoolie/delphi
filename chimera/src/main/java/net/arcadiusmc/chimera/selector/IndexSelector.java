package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;

public interface IndexSelector {

  boolean test(boolean inverted, Element root, Element el);

  void append(StringBuilder builder);

  static int getIndex(Element element, boolean inverted) {
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
