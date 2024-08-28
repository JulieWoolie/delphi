package net.arcadiusmc.chimera.selector;

import java.util.Objects;
import net.arcadiusmc.dom.Element;

public record IdFunction(String elementId) implements SelectorFunction {

  @Override
  public boolean test(Element root, Element element) {
    String id = element.getId();
    return Objects.equals(elementId, id);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append('#').append(elementId);
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.idColumn++;
  }
}
