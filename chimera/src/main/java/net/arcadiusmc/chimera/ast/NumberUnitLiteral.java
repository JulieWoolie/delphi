package net.arcadiusmc.chimera.ast;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;

@Getter @Setter
public class NumberUnitLiteral extends Expression {

  private NumberLiteral number;
  private Unit unit;

  @Override
  public Primitive evaluate(ChimeraContext ctx) {
    if (number == null) {
      return null;
    }

    float value = number.evaluate(ctx).floatValue();
    return Primitive.create(value, Objects.requireNonNullElse(unit, Unit.NONE));
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.numberUnitLiteral(this, context);
  }
}
