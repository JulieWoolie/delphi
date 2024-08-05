package net.arcadiusmc.delphidom.selector;

import com.google.common.base.Strings;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.NodeFlag;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.TagNames;
import net.kyori.adventure.util.TriState;

public record PseudoClassFunction(PseudoClass pseudo) implements SelectorFunction {

  @Override
  public boolean test(DelphiElement root, DelphiElement element) {
    return switch (pseudo) {
      case HOVER -> element.hasFlag(NodeFlag.HOVERED);
      case ACTIVE -> element.hasFlag(NodeFlag.CLICKED);
      case DISABLED -> buttonEnabled(element) == TriState.FALSE;
      case ENABLED -> buttonEnabled(element) == TriState.TRUE;
      case ROOT -> element.hasFlag(NodeFlag.ROOT);
      case LAST_CHILD -> PseudoFunctions.NTH_CHILD.test(root, element, -1);
      case FIRST_CHILD -> PseudoFunctions.NTH_CHILD.test(root, element, 0);
    };
  }

  private TriState buttonEnabled(DelphiElement element) {
    if (!element.getTagName().equals(TagNames.BUTTON)) {
      return TriState.NOT_SET;
    }

    String enabled = element.getAttribute(Attributes.ENABLED);

    if (Strings.isNullOrEmpty(enabled)) {
      return TriState.TRUE;
    }

    return switch (enabled) {
      case "true" -> TriState.TRUE;
      case "false" -> TriState.FALSE;
      default -> TriState.NOT_SET;
    };
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append(':');

    switch (pseudo) {
      case ENABLED -> builder.append("enabled");
      case DISABLED -> builder.append("disabled");
      case ACTIVE -> builder.append("active");
      case HOVER -> builder.append("hover");
      case ROOT -> builder.append("root");
      case LAST_CHILD -> builder.append("last-child");
      case FIRST_CHILD -> builder.append("first-child");
    }
  }

  @Override
  public void appendDebug(StringBuilder builder) {
    builder.append("    <pseudo-class class=").append('"');

    append(builder);

    builder.append('"').append(" />");
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }
}
