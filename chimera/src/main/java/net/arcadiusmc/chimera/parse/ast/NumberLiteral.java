package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.dom.style.Primitive.Unit;

@Getter @Setter
public class NumberLiteral extends Expression {

  private Number value;
  private Unit unit = Unit.NONE;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.numberLiteral(this);
  }
}
