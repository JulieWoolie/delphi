package net.arcadiusmc.chimera.selector;

import java.util.Objects;
import net.arcadiusmc.dom.Element;

public record TagNameFunction(String tagName) implements SelectorFunction {

  @Override
  public boolean test(Element root, Element element) {
    return Objects.equals(tagName, element.getTagName());
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append(tagName);
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.typeColumn++;
  }
}
