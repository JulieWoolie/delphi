package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VariableDecl extends Node {

  private Identifier name;
  private Expression value;

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.variableDecl(this, context);
  }
}
