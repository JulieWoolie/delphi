package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;

public enum Combinator {
  DESCENDANT ("") {
    @Override
    void append(StringBuilder builder) {
      builder.append(' ');
    }

    @Override
    Element findNextMatching(Element el, SelectorNode node) {
      Element p = el.getParent();

      while (p != null) {
        if (node.test(p)) {
          return p;
        }

        p = p.getParent();
        if (p == null) {
          return null;
        }
      }

      return null;
    }
  },

  PARENT (">") {
    @Override
    Element findNextMatching(Element el, SelectorNode node) {
      Element p = el.getParent();

      if (p == null) {
        return null;
      }
      if (!node.test(p)) {
        return null;
      }

      return p;
    }
  },

  DIRECT_SIBLING ("+") {
    @Override
    Element findNextMatching(Element el, SelectorNode node) {
      Node previous = el.previousSibling();

      if (!(previous instanceof Element prevEl)) {
        return null;
      }
      if (!node.test(prevEl)) {
        return null;
      }

      return prevEl;
    }
  },

  SIBLING ("~") {
    @Override
    Element findNextMatching(Element el, SelectorNode node) {
      Node n = el.previousSibling();

      while (n != null) {
        if (!(n instanceof Element prevEl)) {
          n = n.previousSibling();
          continue;
        }

        if (node.test(prevEl)) {
          return prevEl;
        }

        n = n.previousSibling();
      }

      return null;
    }
  },

  NEST("&") {
    @Override
    Element findNextMatching(Element el, SelectorNode node) {
      if (node.test(el)) {
        return el;
      }

      return null;
    }

    @Override
    void append(StringBuilder builder) {
      // No-op
    }
  }
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

  abstract Element findNextMatching(Element el, SelectorNode node);
}
