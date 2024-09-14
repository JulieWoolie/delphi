package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;

@Getter @Setter
public class StringLiteral extends Expression {

  private String value;

  @Override
  public String evaluate(ChimeraContext ctx, Scope scope) {
    return value;
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.stringLiteral(this);
  }
}
