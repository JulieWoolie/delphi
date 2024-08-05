package net.arcadiusmc.delphidom.selector;

import net.arcadiusmc.delphidom.DelphiElement;

public record PseudoFuncFunction<T>(PseudoFunc<T> func, T argument) implements SelectorFunction {

  @Override
  public boolean test(DelphiElement root, DelphiElement element) {
    return func.test(root, element, argument);
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
  public void appendDebug(StringBuilder builder) {
    builder.append("    <pseudo-func function=").append('"');

    func.append(builder);

    builder.append('"')
        .append(" argument=")
        .append('"');

    func.appendValue(builder, argument);

    builder.append('"')
        .append(" />");
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }
}
