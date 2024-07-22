package net.arcadiusmc.delphi.dom.selector;

import java.util.Objects;
import net.arcadiusmc.delphi.dom.DelphiElement;

public record TagNameFunction(String tagName) implements FilteringFunction {

  @Override
  public boolean test(DelphiElement element) {
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
