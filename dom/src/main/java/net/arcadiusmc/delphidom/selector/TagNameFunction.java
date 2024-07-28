package net.arcadiusmc.delphidom.selector;

import java.util.Objects;
import net.arcadiusmc.delphidom.DelphiElement;

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
  public void appendDebug(StringBuilder builder) {
    builder.append("    <tag-name value=").append('"').append(tagName).append('"').append(" />");
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.typeColumn++;
  }
}
