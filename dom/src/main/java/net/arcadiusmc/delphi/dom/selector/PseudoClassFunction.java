package net.arcadiusmc.delphi.dom.selector;

import com.google.common.base.Strings;
import java.util.List;
import net.arcadiusmc.delphi.StringUtil;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.delphi.dom.NodeFlag;
import net.arcadiusmc.dom.Attr;
import net.arcadiusmc.dom.TagNames;
import net.kyori.adventure.util.TriState;

public record PseudoClassFunction(PseudoClass pseudo) implements FilteringFunction {

  @Override
  public boolean test(DelphiElement element) {
    return switch (pseudo) {
      case HOVER -> element.hasFlag(NodeFlag.HOVERED);
      case ACTIVE -> element.hasFlag(NodeFlag.CLICKED);
      case DISABLED -> buttonEnabled(element) == TriState.FALSE;
      case ENABLED -> buttonEnabled(element) == TriState.TRUE;
      case ROOT -> element.hasFlag(NodeFlag.ROOT);
    };
  }

  private TriState buttonEnabled(DelphiElement element) {
    if (!element.getTagName().equals(TagNames.BUTTON)) {
      return TriState.NOT_SET;
    }

    String enabled = element.getAttribute(Attr.ENABLED);

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
    }
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }
}
