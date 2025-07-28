package com.juliewoolie.chimera.selector;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.dom.Element;

@Getter
@Setter
public class SelectorNode implements Selector {

  Selector selector = Selector.MATCH_ALL;
  Combinator combinator = Combinator.DESCENDANT;

  public SelectorNode copy() {
    var node = new SelectorNode();
    node.combinator = combinator;
    node.selector = selector;
    return node;
  }

  @Override
  public boolean test(Element el) {
    return selector.test(el);
  }

  @Override
  public void append(StringBuilder builder) {
    append(builder, combinator != Combinator.DESCENDANT);
  }

  public void append(StringBuilder builder, boolean appendCombinator) {
    if (appendCombinator) {
      combinator.append(builder);
    }

    selector.append(builder);
  }

  @Override
  public void appendSpec(Spec spec) {
    selector.appendSpec(spec);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    append(builder, false);
    return builder.toString();
  }
}
