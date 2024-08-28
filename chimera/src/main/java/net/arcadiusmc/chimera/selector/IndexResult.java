package net.arcadiusmc.chimera.selector;

import java.util.List;
import java.util.function.Predicate;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;

public record IndexResult(int index, int count, int before) {

  public static IndexResult indexMatching(
      boolean invert,
      Element el,
      Predicate<Element> predicate
  ) {

    int gIndex = -1;
    int gLen = 0;
    int before = -1;

    Element p = el.getParent();
    if (p == null) {
      return new IndexResult(gIndex, gLen, before);
    }

    final List<Node> children = p.getChildren();
    int start = invert ? children.size() - 1 : 0;
    int dir = invert ? -1 : 1;

    for (int i = start; i < children.size() && i >= 0; i += dir) {
      Node node = children.get(i);
      if (!(node instanceof Element element)) {
        continue;
      }

      boolean eq = element.equals(el);
      if (eq) {
        before = gLen;
      }

      if (!predicate.test(element)) {
        continue;
      }

      if (eq) {
        gIndex = gLen;
      }

      gLen++;
    }

    if (gIndex != -1) {
      gIndex++;
    }

    return new IndexResult(gIndex, gLen, before);
  }

  int indexOrBefore() {
    if (index != -1) {
      return index;
    }

    return before;
  }
}
