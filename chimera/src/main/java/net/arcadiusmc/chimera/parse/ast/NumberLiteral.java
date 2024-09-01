package net.arcadiusmc.chimera.parse.ast;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;

@Getter @Setter
public class NumberLiteral extends Expression {

  private Number value;
  private Unit unit = Unit.NONE;

  @Override
  public Primitive evaluate(ChimeraContext ctx) {
    Unit unit = Objects.requireNonNullElse(this.unit, Unit.NONE);
    return Primitive.create(value.floatValue(), unit);
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.numberLiteral(this, context);
  }
}
