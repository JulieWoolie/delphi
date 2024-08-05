package net.arcadiusmc.delphidom.selector;

import java.util.Objects;
import net.arcadiusmc.delphidom.DelphiElement;

public record IdFunction(String elementId) implements SelectorFunction {

  @Override
  public boolean test(DelphiElement root, DelphiElement element) {
    String id = element.getId();
    return Objects.equals(elementId, id);
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append('#').append(elementId);
  }

  @Override
  public void appendDebug(StringBuilder builder) {
    builder.append("    <id value=").append('"').append(elementId).append('"').append(" />");
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.idColumn++;
  }
}
