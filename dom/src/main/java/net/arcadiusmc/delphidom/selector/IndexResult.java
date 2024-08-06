package net.arcadiusmc.delphidom.selector;

import java.util.List;
import java.util.function.Predicate;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;

public record IndexResult(int index, int count, int before) {

  public static IndexResult indexMatching(
      boolean invert,
      DelphiElement el,
      Predicate<DelphiElement> predicate
  ) {

    int gIndex = -1;
    int gLen = 0;
    int before = -1;

    DelphiElement p = el.getParent();
    if (p == null) {
      return new IndexResult(gIndex, gLen, before);
    }

    final List<DelphiNode> children = p.childList();
    int start = invert ? children.size() - 1 : 0;
    int dir = invert ? -1 : 1;

    for (int i = start; i < children.size() && i >= 0; i += dir) {
      DelphiNode node = children.get(i);
      if (!(node instanceof DelphiElement element)) {
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
