package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.chimera.parse.Token;

@Getter @Setter
public class ErroneousExpr extends Expression {

  private Token token;

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
    return null;
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.error(this);
  }
}
