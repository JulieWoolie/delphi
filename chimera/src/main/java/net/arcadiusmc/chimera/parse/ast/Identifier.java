package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Identifier extends Expression {

  private String value;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.identifier(this);
  }
}
