package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.Node;

public enum Combinator {
  DESCENDANT ("") {
    @Override
    void append(StringBuilder builder) {
      builder.append(' ');
    }

    @Override
    DelphiElement findNextMatching(DelphiElement root, DelphiElement el, SelectorNode node) {
      DelphiElement p = el.getParent();
      int minDepth = root == null ? -1 : root.getDepth();

      while (p != null) {
        if (node.test(root, p)) {
          return p;
        }

        p = p.getParent();
        if (p == null || p.getDepth() <= minDepth) {
          return null;
        }
      }

      return null;
    }
  },

  PARENT (">") {
    @Override
    DelphiElement findNextMatching(DelphiElement root, DelphiElement el, SelectorNode node) {
      DelphiElement p = el.getParent();
      int minDepth = root == null ? -1 : root.getDepth();

      if (p == null || p.getDepth() <= minDepth) {
        return null;
      }
      if (!node.test(root, p)) {
        return null;
      }

      return p;
    }
  },

  DIRECT_SIBLING ("+") {
    @Override
    DelphiElement findNextMatching(DelphiElement root, DelphiElement el, SelectorNode node) {
      Node previous = el.previousSibling();

      if (!(previous instanceof DelphiElement prevEl)) {
        return null;
      }
      if (!node.test(root, prevEl)) {
        return null;
      }

      return prevEl;
    }
  },

  SIBLING ("~") {
    @Override
    DelphiElement findNextMatching(DelphiElement root, DelphiElement el, SelectorNode node) {
      Node n = el.previousSibling();

      while (n != null) {
        if (!(n instanceof DelphiElement prevEl)) {
          n = n.previousSibling();
          continue;
        }

        if (node.test(root, prevEl)) {
          return prevEl;
        }

        n = n.previousSibling();
      }

      return null;
    }
  },
  ;

  private final String combinator;

  Combinator(String string) {
    this.combinator = string;
  }

  void append(StringBuilder builder) {
    builder.append(' ')
        .append(combinator)
        .append(' ');
  }

  abstract DelphiElement findNextMatching(DelphiElement root, DelphiElement el, SelectorNode node);
}
