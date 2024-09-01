package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InlineStyleStatement extends Node {
  private final List<PropertyStatement> properties = new ArrayList<>();

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.inlineStyle(this, context);
  }
}
