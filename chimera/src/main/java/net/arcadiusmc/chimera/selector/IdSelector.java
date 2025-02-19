package net.arcadiusmc.chimera.selector;

import java.util.Objects;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;

public record IdSelector(String elementId) implements Selector {

  @Override
  public boolean test(Element element) {
    Document doc = element.getOwningDocument();
    return Objects.equals(doc.getElementById(elementId), element);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append('#').append(elementId);
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.idColumn++;
  }

  @Override
  public String getCssString() {
    return toString();
  }

  @Override
  public String toString() {
    return "#" + elementId;
  }
}
