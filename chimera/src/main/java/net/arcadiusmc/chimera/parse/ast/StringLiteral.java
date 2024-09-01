package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;

@Getter @Setter
public class StringLiteral extends Expression {

  private String value;

  @Override
  public String evaluate(ChimeraContext ctx) {
    return value;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.stringLiteral(this, context);
  }
}
