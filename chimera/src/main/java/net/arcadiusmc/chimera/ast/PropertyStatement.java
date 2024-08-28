package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.Location;

@Getter @Setter
public class PropertyStatement extends Node {

  private Identifier propertyName;
  private Expression value;

  private boolean important;
  private Location importantStart;

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.property(this, context);
  }
}
