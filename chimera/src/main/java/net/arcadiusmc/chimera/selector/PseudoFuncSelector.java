package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;

public record PseudoFuncSelector<T>(PseudoFunc<T> func, T argument) implements Selector {

  @Override
  public boolean test(Element element) {
    return func.test(element, argument);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append(':');
    func.append(builder);

    builder.append('(');
    func.appendValue(builder, argument);
    builder.append(')');
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }

  @Override
  public String toString() {
    return getCssString();
  }
}
