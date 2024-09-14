package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NamespaceExpr extends Expression {

  private Identifier namespace;
  private Expression target;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.namespaced(this);
  }
}
