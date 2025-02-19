package net.arcadiusmc.chimera.selector;

import net.arcadiusmc.dom.Element;

public record TagNameSelector(String tagName) implements Selector {

  @Override
  public boolean test(Element element) {
    return element.getTagName().equalsIgnoreCase(tagName);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append(tagName);
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.typeColumn++;
  }

  @Override
  public String getCssString() {
    return tagName;
  }

  @Override
  public String toString() {
    return tagName;
  }
}
