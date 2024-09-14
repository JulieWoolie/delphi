package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VariableDecl extends Statement {

  private Identifier name;
  private Expression value;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.variableDecl(this);
  }
}
