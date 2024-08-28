package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;

@Getter @Setter
public class NumberLiteral extends Expression {

  private Number value;


  @Override
  public Number evaluate(ChimeraContext ctx) {
    return value;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.numberLiteral(this, context);
  }
}
