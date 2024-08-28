package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;
import net.arcadiusmc.chimera.Token;

@Getter @Setter
public class ErroneousExpr extends Expression {

  private Token token;

  @Override
  public Object evaluate(ChimeraContext ctx) {
    return null;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.error(this, context);
  }
}
