package net.arcadiusmc.delphi.dom.selector;

import java.util.Objects;
import net.arcadiusmc.delphi.dom.DelphiElement;

public record IdFunction(String elementId) implements FilteringFunction {

  @Override
  public boolean test(DelphiElement element) {
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
