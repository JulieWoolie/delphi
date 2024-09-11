package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PropertyStatement extends Statement {

  private Identifier propertyName;
  private Expression value;

  private ImportantMarker important;

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.property(this, context);
  }
}
