package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PropertyStatement extends Statement {

  private Identifier propertyName;
  private Expression value;

  private ImportantMarker important;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.property(this);
  }
}
