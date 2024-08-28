package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.chimera.selector.PseudoFunc.SelectorPseudoFunc;
import net.arcadiusmc.dom.Element;

public interface PseudoFunctions {

  IndexSelectorFunction NTH_CHILD = new IndexSelectorFunction() {
    @Override
    public boolean test(Element root, Element el, IndexSelector value) {
      return value.test(false, root, el);
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("nth-child");
    }
  };

  IndexSelectorFunction NTH_LAST_CHILD = new IndexSelectorFunction() {
    @Override
    public boolean test(Element root, Element el, IndexSelector value) {
      return value.test(true, root, el);
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("nth-last-child");
    }
  };

  AnbFunction NTH_OF_TYPE = new AnbFunction() {
    @Override
    public boolean test(Element root, Element el, AnB value) {
      IndexResult idx = IndexResult.indexMatching(
          false,
          el,
          e -> e.getTagName().equals(el.getTagName())
      );

      if (idx.index() == -1) {
        return false;
      }

      return value.indexMatches(idx.index());
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("nth-of-type");
    }
  };

  AnbFunction NTH_LAST_OF_TYPE = new AnbFunction() {
    @Override
    public boolean test(Element root, Element el, AnB value) {
      IndexResult idx = IndexResult.indexMatching(
          true,
          el,
          e -> e.getTagName().equals(el.getTagName())
      );

      if (idx.index() == -1) {
        return false;
      }

      return value.indexMatches(idx.index());
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("nth-last-of-type");
    }
  };

  PseudoFunc<SelectorGroup> IS = new SelectorPseudoFunc() {
    @Override
    public boolean test(Element root, Element el, SelectorGroup value) {
      return value.test(root, el);
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("is");
    }
  };

  PseudoFunc<SelectorGroup> NOT = new SelectorPseudoFunc() {
    @Override
    public boolean test(Element root, Element el, SelectorGroup value) {
      return !value.test(root, el);
    }

    @Override
    public void append(StringBuilder builder) {
      builder.append("not");
    }
  };

  interface IndexSelectorFunction extends PseudoFunc<IndexSelector> {

    @Override
    default void appendValue(StringBuilder builder, IndexSelector value) {
      value.append(builder);
    }
  }

  interface AnbFunction extends PseudoFunc<AnB> {

    @Override
    default void appendValue(StringBuilder builder, AnB value) {
      value.append(builder);
    }
  }
}
